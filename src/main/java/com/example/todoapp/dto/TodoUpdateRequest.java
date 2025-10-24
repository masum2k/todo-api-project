package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TodoUpdateRequest {

    private String title;
    @Size(max = 30, message = "Açıklama (description) en fazla 30 karakter olabilir.")
    private String description;

    private Boolean completed;

    private LocalDateTime deadline;
    private Priority priority;
    private List<String> tags;
}