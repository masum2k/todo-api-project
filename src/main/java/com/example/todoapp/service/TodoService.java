package com.example.todoapp.service;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TodoService {

    TodoResponse createTodo(TodoCreateRequest createRequest);

    TodoResponse getTodoById(Long id);

    TodoResponse updateTodo(Long id, TodoUpdateRequest updateRequest);

    void deleteTodo(Long id);

    TodoResponse updateTodoCompletion(Long id, boolean isCompleted);

    Page<TodoResponse> getAllTodos(Boolean completed, Priority priority, String tag, Boolean overdue, Pageable pageable);
}
