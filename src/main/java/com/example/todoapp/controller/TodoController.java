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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(@Valid @RequestBody TodoCreateRequest createRequest) {
        return todoService.createTodo(createRequest);
    }

    @GetMapping
    public Page<TodoResponse> getAllTodos(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "priority", required = false) Priority priority,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "overdue", required = false) Boolean overdue,
            Pageable pageable) {

        return todoService.getAllTodos(completed, priority, tag, overdue, pageable);
    }

    @GetMapping("/{id}")
    public TodoResponse getTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    @PutMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable Long id, @Valid @RequestBody TodoUpdateRequest updateRequest) {
        return todoService.updateTodo(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}/completion")
    public TodoResponse updateTodoCompletion(@PathVariable Long id, @RequestParam boolean isCompleted) {
        return todoService.updateTodoCompletion(id, isCompleted);
    }

}