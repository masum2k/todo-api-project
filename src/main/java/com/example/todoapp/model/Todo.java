package com.example.todoapp.model;

import com.example.todoapp.enums.Priority;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Başlık boş olamaz (Entity Validasyonu)")
    @Size(min = 3, max = 100, message = "Başlık 3 ile 100 karakter arasında olmalıdır (Entity Validasyonu)")
    @Column(nullable = false)
    private String title;

    @Size(max = 255, message = "Açıklama 255 karakterden fazla olamaz (Entity Validasyonu)")
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(updatable = false, nullable = false)
    private long createdAt;

    private Long deadline;

    @NotNull(message = "Öncelik alanı boş olamaz (Entity Validasyonu)")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Size(max = 5, message = "En fazla 5 etiket eklenebilir (Entity Validasyonu)")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Email(message = "Geçerli bir e-posta adresi olmalıdır (Entity Validasyonu)")
    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private boolean reminderSent = false;
}