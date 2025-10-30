package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TodoCreateRequest(
        @NotBlank(message = "Title (başlık) alanı boş olamaz.")
        @Size(min = 3, max = 100, message = "Başlık en az 3, en fazla 100 karakter olmalıdır.")
        String title,

        @Size(max = 255, message = "Açıklama en fazla 255 karakter olabilir.")
        String description,

        Long deadline,

        @NotNull(message = "Priority (öncelik) alanı boş olamaz.")
        Priority priority,

        @Size(max = 5, message = "En fazla 5 etiket eklenebilir.")
        List<String> tags
) {
}//record,mail(kafka),schedule