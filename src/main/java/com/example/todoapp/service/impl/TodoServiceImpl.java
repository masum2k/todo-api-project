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
    public TodoResponse createTodo(TodoCreateRequest createRequest) {
        Todo todo = todoMapper.toEntity(createRequest);
        Todo savedTodo = todoRepository.save(todo);

        return todoMapper.toResponse(savedTodo);
    }

    @Override
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));

        return todoMapper.toResponse(todo);
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoUpdateRequest updateRequest) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));

        todoMapper.updateEntity(updateRequest, existingTodo);
        Todo updatedTodo = todoRepository.save(existingTodo);

        return todoMapper.toResponse(updatedTodo);
    }

    @Override
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    public TodoResponse updateTodoCompletion(Long id, boolean isCompleted) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MESSAGE + id));
        existingTodo.setCompleted(isCompleted);
        Todo updatedTodo = todoRepository.save(existingTodo);

        return todoMapper.toResponse(updatedTodo);
    }

    @Override
    public Page<TodoResponse> getAllTodos(Boolean completed, Priority priority,
                                          String tag, Boolean overdue, Pageable pageable) {

        long now = Instant.now().toEpochMilli();
        Page<Long> todoIdsPage = todoRepository.findTodoIds(
                completed, priority, tag, overdue, now, pageable
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