package com.example.todoapp.mapper;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.model.Todo;

import java.util.List;

public class TodoMapper {

    public static TodoResponse toResponse(Todo todo) {

        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.isCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setDeadline(todo.getDeadline());
        response.setPriority(todo.getPriority());
        response.setTags(todo.getTags());
        return response;
    }

    public static List<TodoResponse> toResponseList(List<Todo> todos) {

        return todos.stream()
                .map(TodoMapper::toResponse)
                .toList();
    }

    public static Todo toEntity(TodoCreateRequest request) {

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDeadline(request.getDeadline());
        todo.setPriority(request.getPriority());
        todo.setTags(request.getTags());
        return todo;
    }

    public static void updateEntity(Todo todo, TodoUpdateRequest request) {

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        if (request.getDeadline() != null) {
            todo.setDeadline(request.getDeadline());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        if (request.getTags() != null) {
            todo.setTags(request.getTags());
        }
    }
}