package com.example.todoapp.service;

import com.example.todoapp.dto.EmailSendEvent;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoReminderService {

    private final TodoRepository todoRepository;
    private final EmailEventProducer emailEventProducer;

    private static final long NOTIFICATION_WINDOW_SECONDS = 30;

    @Scheduled(fixedRateString = "${todo.reminder.schedule.rate}")
    @Transactional
    public void checkAndSendReminders() {

        log.info("Hatırlatıcılar kontrol ediliyor...");

        long notificationTime = Instant.now().plus(NOTIFICATION_WINDOW_SECONDS, ChronoUnit.SECONDS).toEpochMilli();

        List<Todo> todosToSend = todoRepository.findByDeadlineIsNotNullAndDeadlineLessThanEqualAndCompletedFalseAndReminderSentFalse(notificationTime); //

        if (todosToSend.isEmpty()) {
            log.info("Gönderilecek yeni hatırlatıcı bulunamadı.");
            return;
        }

        log.info("{} adet gönderilecek hatırlatıcı bulundu. İşleniyor...", todosToSend.size());

        for (Todo todo : todosToSend) {
            String subject = "Todo Hatırlatması: " + todo.getTitle();
            String body = "'" + todo.getTitle() + "' başlıklı görevinizin son tarihi yaklaşıyor!" +
                    "\nSon Tarih: " + Instant.ofEpochMilli(todo.getDeadline());

            EmailSendEvent event = new EmailSendEvent(
                    todo.getUserEmail(),
                    subject,
                    body
            );

            emailEventProducer.sendEmailEvent(event);

            todo.setReminderSent(true);
            todoRepository.save(todo);
        }
    }
}