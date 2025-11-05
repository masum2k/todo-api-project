package com.example.todoapp.service;

import com.example.todoapp.dto.EmailSendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventProducer {

    @Value("${kafka.topic.email-send}")
    private String emailSendTopic;

    private final KafkaTemplate<String, EmailSendEvent> kafkaTemplate;  //topic,event

    public void sendEmailEvent(EmailSendEvent event) {

        log.info("E-posta gönderme komutu Kafka'ya ({}) gönderiliyor: {}", emailSendTopic, event);
        try {
            kafkaTemplate.send(emailSendTopic, event.toEmail(), event);
        } catch (Exception e) {
            log.error("Kafka'ya e-posta komutu gönderirken hata oluştu: {}", event, e);
        }
    }
}