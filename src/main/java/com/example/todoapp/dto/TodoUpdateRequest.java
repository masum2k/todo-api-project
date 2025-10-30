package com.example.todoapp.dto;

import com.example.todoapp.enums.Priority;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TodoUpdateRequest(
        @Size(min = 3, max = 100, message = "Başlık en az 3, en fazla 100 karakter olmalıdır.")
        String title,

        @Size(max = 255, message = "Açıklama en fazla 255 karakter olabilir.")
        String description,

        Boolean completed,

        Long deadline,

        Priority priority,

        @Size(max = 5, message = "En fazla 5 etiket eklenebilir.")
        List<String> tags
) {
}