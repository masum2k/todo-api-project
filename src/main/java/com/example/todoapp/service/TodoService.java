package com.example.todoapp.service;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TodoService {

    TodoResponse createTodo(
            @Valid @NotNull(message = "Create request null olamaz") TodoCreateRequest createRequest
    );

    TodoResponse getTodoById(
            @NotNull(message = "ID null olamaz") @Min(value = 1, message = "ID 1'den küçük olamaz") Long id
    );

    TodoResponse updateTodo(
            @NotNull(message = "ID null olamaz") @Min(value = 1, message = "ID 1'den küçük olamaz") Long id,
            @Valid @NotNull(message = "Update request null olamaz") TodoUpdateRequest updateRequest
    );

    void deleteTodo(
            @NotNull(message = "ID null olamaz") @Min(value = 1, message = "ID 1'den küçük olamaz") Long id
    );

    TodoResponse updateTodoCompletion(
            @NotNull(message = "ID null olamaz") @Min(value = 1, message = "ID 1'den küçük olamaz") Long id,
            boolean isCompleted
    );

    Page<TodoResponse> getAllTodos(Boolean completed, Priority priority, String tag, Boolean overdue, Pageable pageable);
}