package com.example.todoapp.service;

import com.example.todoapp.dto.EmailSendEvent;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoReminderService {

    private final TodoRepository todoRepository;
    private final EmailEventProducer emailEventProducer;

    @Value("${todo.reminder.window-seconds:30}")
    private long notificationWindowSeconds;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.systemDefault());

    @Scheduled(fixedRateString = "${todo.reminder.schedule.rate}")
    @Transactional
    public void checkAndSendReminders() {
        long notificationTime = Instant.now()
                .plusSeconds(notificationWindowSeconds)
                .toEpochMilli();

        List<Todo> todosToRemind = todoRepository
                .findByDeadlineIsNotNullAndDeadlineLessThanEqualAndCompletedFalseAndReminderSentFalse(notificationTime);

        if (todosToRemind.isEmpty()) {
            log.debug("No reminders to send");
            return;
        }

        log.info("Sending {} reminder(s)", todosToRemind.size());

        todosToRemind.forEach(this::sendReminderAndMarkAsSent);
    }

    private void sendReminderAndMarkAsSent(Todo todo) {
        try {
            String formattedDeadline = DATE_FORMATTER.format(Instant.ofEpochMilli(todo.getDeadline()));

            EmailSendEvent event = new EmailSendEvent(
                    todo.getUserEmail(),
                    "Todo Reminder: " + todo.getTitle(),
                    String.format(
                            "Your task '%s' is approaching its deadline!%n%nDeadline: %s%n%nPriority: %s",
                            todo.getTitle(),
                            formattedDeadline,
                            todo.getPriority()
                    )
            );

            emailEventProducer.sendEmailEvent(event);
            todo.setReminderSent(true);
            todoRepository.save(todo);

            log.debug("Reminder sent successfully for todo: {}", todo.getId());
        } catch (Exception e) {
            log.error("Failed to send reminder for todo: {}", todo.getId(), e);
        }
    }
}