package com.example.todoapp.controller;

import com.example.todoapp.annotation.TrackExecutionTime;
import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import com.example.todoapp.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@TrackExecutionTime
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(
            @Valid @RequestBody TodoCreateRequest createRequest,
            Authentication authentication) {
        return todoService.createTodo(createRequest, authentication.getName());
    }

    @GetMapping
    public Page<TodoResponse> getAllTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Boolean overdue,
            Pageable pageable,
            Authentication authentication) {
        return todoService.getAllTodos(completed, priority, tag, overdue,
                authentication.getName(), pageable);
    }

    @GetMapping("/{id}")
    public TodoResponse getTodoById(
            @PathVariable Long id,
            Authentication authentication) {
        return todoService.getTodoById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public TodoResponse updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoUpdateRequest updateRequest,
            Authentication authentication) {
        return todoService.updateTodo(id, updateRequest, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @PathVariable Long id,
            Authentication authentication) {
        todoService.deleteTodo(id, authentication.getName());
    }

    @PatchMapping("/{id}/completion")
    public TodoResponse updateTodoCompletion(
            @PathVariable Long id,
            @RequestParam boolean isCompleted,
            Authentication authentication) {
        return todoService.updateTodoCompletion(id, isCompleted, authentication.getName());
    }
}