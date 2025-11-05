package com.example.todoapp.dto;

public record EmailSendEvent(
        String toEmail,
        String subject,
        String body
) {
}