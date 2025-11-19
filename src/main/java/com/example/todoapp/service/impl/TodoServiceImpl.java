package com.example.todoapp.service.impl;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.mapper.TodoMapper;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class TodoServiceImpl implements TodoService {

    private static final String TODO_NOT_FOUND_MESSAGE = "Todo not found with id: ";
    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    public TodoResponse createTodo(TodoCreateRequest createRequest, String userEmail) {
        Todo todo = todoMapper.toEntity(createRequest);

        todo.setUserEmail(userEmail);

        Todo savedTodo = todoRepository.save(todo);
        return todoMapper.toResponse(savedTodo);
    }

    @Override
    @Cacheable(value = "todos", key = "#id")
    public TodoResponse getTodoById(Long id, String userEmail) {
        Todo todo = todoRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));

        return todoMapper.toResponse(todo);
    }

    @Override
    @CachePut(value = "todos", key = "#id")
    public TodoResponse updateTodo(Long id, TodoUpdateRequest updateRequest, String userEmail) {
        Todo existingTodo = todoRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));

        todoMapper.updateEntity(updateRequest, existingTodo);
        Todo updatedTodo = todoRepository.save(existingTodo);

        return todoMapper.toResponse(updatedTodo);
    }

    @Override
    @CacheEvict(value = "todos", key = "#id")
    public void deleteTodo(Long id, String userEmail) {
        if (!todoRepository.existsByIdAndUserEmail(id, userEmail)) {
            throw new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id);
        }

        todoRepository.deleteById(id);
    }

    @Override
    @CachePut(value = "todos", key = "#id")
    public TodoResponse updateTodoCompletion(Long id, boolean isCompleted, String userEmail) {
        Todo existingTodo = todoRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));

        existingTodo.setCompleted(isCompleted);
        Todo updatedTodo = todoRepository.save(existingTodo);

        return todoMapper.toResponse(updatedTodo);
    }

    @Override
    public Page<TodoResponse> getAllTodos(Boolean completed, Priority priority,
                                          String tag, Boolean overdue, String userEmail, Pageable pageable) {

        long now = Instant.now().toEpochMilli();

        Page<Long> todoIdsPage = todoRepository.findTodoIds(
                completed, priority, tag, overdue, userEmail, now, pageable
        );

        if (todoIdsPage.getContent().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, todoIdsPage.getTotalElements());
        }
        List<Todo> todos = todoRepository.findByIdsWithTags(todoIdsPage.getContent());


        List<TodoResponse> todoResponses = todoMapper.toResponseList(todos);

        return new PageImpl<>(
                todoResponses,
                pageable,
                todoIdsPage.getTotalElements()
        );
    }
}