package com.example.todoapp.service;

import com.example.todoapp.dto.TodoNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoEventProducer {

    private static final String TOPIC_NAME = "todo-events";

    private final KafkaTemplate<String, TodoNotificationEvent> kafkaTemplate;

    public void sendTodoEvent(TodoNotificationEvent event) {
        try {
            log.info("Todo olayı Kafka'ya gönderiliyor: {}", event);
            kafkaTemplate.send(TOPIC_NAME, event.todoId().toString(), event);

        } catch (Exception e) {
            log.error("Kafka'ya olay gönderirken hata oluştu: {}", event, e);
        }
    }
}