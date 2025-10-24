package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private Priority priority;
    private List<String> tags;
}