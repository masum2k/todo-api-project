package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;

import java.io.Serializable;
import java.util.List;

public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        long createdAt,
        Long deadline,
        Priority priority,
        List<String> tags,
        String userEmail
) implements Serializable {
}