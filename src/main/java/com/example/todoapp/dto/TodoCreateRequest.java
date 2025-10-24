package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TodoCreateRequest {
    @NotBlank(message = "Title (başlık) alanı boş olamaz.")
    private String title;
    private String description;

    private LocalDateTime deadline;
    private Priority priority;
    private List<String> tags;
}