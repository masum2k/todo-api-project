package com.example.todoapp.mapper;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.model.Todo;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface TodoMapper {

    TodoResponse toResponse(Todo todo);

    List<TodoResponse> toResponseList(List<Todo> todos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now().toEpochMilli())")
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "reminderSent", ignore = true)
    Todo toEntity(TodoCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "reminderSent", ignore = true)
    void updateEntity(TodoUpdateRequest request, @MappingTarget Todo todo);
}