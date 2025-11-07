package com.example.todoapp.controller;

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
// YENİ IMPORT: Güvenlik konteksinden kullanıcıyı almak için
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(@Valid @RequestBody TodoCreateRequest createRequest) {
        String userEmail = getAuthenticatedUserEmail();
        return todoService.createTodo(createRequest, userEmail);
    }

    @GetMapping
    public Page<TodoResponse> getAllTodos(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "priority", required = false) Priority priority,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "overdue", required = false) Boolean overdue,
            Pageable pageable) {

        String userEmail = getAuthenticatedUserEmail();
        return todoService.getAllTodos(completed, priority, tag, overdue, userEmail, pageable);
    }

    @GetMapping("/{id}")
    public TodoResponse getTodoById(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        return todoService.getTodoById(id, userEmail);
    }

    @PutMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable Long id, @Valid @RequestBody TodoUpdateRequest updateRequest) {
        String userEmail = getAuthenticatedUserEmail();
        return todoService.updateTodo(id, updateRequest, userEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable Long id) {
        String userEmail = getAuthenticatedUserEmail();
        todoService.deleteTodo(id, userEmail);
    }

    @PatchMapping("/{id}/completion") // Bu metot ismini @PutMapping olarak değiştirmek daha doğru olabilir
    public TodoResponse updateTodoCompletion(@PathVariable Long id, @RequestParam boolean isCompleted) {
        String userEmail = getAuthenticatedUserEmail();
        return todoService.updateTodoCompletion(id, isCompleted, userEmail);
    }
}