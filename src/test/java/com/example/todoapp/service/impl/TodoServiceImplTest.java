package com.example.todoapp.service.impl;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.mapper.TodoMapper;
import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo sampleTodo;
    private Pageable pageable;
    private TodoResponse sampleTodoResponse;
    private long now;

    @BeforeEach
    void setUp() {
        now = Instant.now().toEpochMilli();

        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Test Todo");
        sampleTodo.setCompleted(false);
        sampleTodo.setCreatedAt(now);
        sampleTodo.setPriority(Priority.MEDIUM);

        sampleTodoResponse = new TodoResponse(
                1L, "Test Todo", null, false, now, null, Priority.MEDIUM, null
        );

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getTodoById_whenTodoExists_shouldReturnTodoResponse() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoMapper.toResponse(sampleTodo)).thenReturn(sampleTodoResponse);

        TodoResponse actualResponse = todoService.getTodoById(1L);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.title()).isEqualTo("Test Todo");

        verify(todoRepository, times(1)).findById(1L);
        verify(todoMapper, times(1)).toResponse(sampleTodo);
    }

    @Test
    void getTodoById_whenTodoDoesNotExist_shouldThrowResourceNotFoundException() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> todoService.getTodoById(99L)
        );

        assertThat(exception.getMessage()).isEqualTo("Todo not found with id: 99");
        verify(todoRepository).findById(99L);
        verify(todoMapper, never()).toResponse(any());
    }

    @Test
    void createTodo_shouldSaveAndReturnTodoResponse() {
        TodoCreateRequest createRequest = new TodoCreateRequest(
                "Yeni Test Todo", "Bu bir test açıklamasıdır.", null, Priority.LOW, null
        );

        Todo todoToSave = new Todo();
        todoToSave.setTitle("Yeni Test Todo");
        todoToSave.setDescription("Bu bir test açıklamasıdır.");
        todoToSave.setPriority(Priority.LOW);

        Todo savedTodo = new Todo();
        savedTodo.setId(2L);
        savedTodo.setTitle("Yeni Test Todo");
        savedTodo.setDescription("Bu bir test açıklamasıdır.");
        savedTodo.setPriority(Priority.LOW);
        savedTodo.setCompleted(false);
        savedTodo.setCreatedAt(now);

        TodoResponse response = new TodoResponse(
                2L, "Yeni Test Todo", "Bu bir test açıklamasıdır.", false, now, null, Priority.LOW, null
        );

        when(todoMapper.toEntity(createRequest)).thenReturn(todoToSave);
        when(todoRepository.save(todoToSave)).thenReturn(savedTodo);
        when(todoMapper.toResponse(savedTodo)).thenReturn(response);

        TodoResponse actualResponse = todoService.createTodo(createRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(2L);
        assertThat(actualResponse.title()).isEqualTo("Yeni Test Todo");
        assertThat(actualResponse.completed()).isFalse();

        verify(todoRepository, times(1)).save(todoToSave);
        verify(todoMapper, times(1)).toEntity(createRequest);
        verify(todoMapper, times(1)).toResponse(savedTodo);
    }

    @Test
    void deleteTodo_whenTodoExists_shouldDeleteTodo() {
        Long todoId = 1L;

        when(todoRepository.existsById(todoId)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(todoId);

        todoService.deleteTodo(todoId);

        verify(todoRepository, times(1)).existsById(todoId);
        verify(todoRepository, times(1)).deleteById(todoId);
    }

    @Test
    void deleteTodo_whenTodoDoesNotExist_shouldThrowResourceNotFoundException() {
        Long todoId = 99L;

        when(todoRepository.existsById(todoId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> todoService.deleteTodo(todoId)
        );

        assertThat(exception.getMessage()).isEqualTo("Todo not found with id: " + todoId);

        verify(todoRepository, times(1)).existsById(todoId);
        verify(todoRepository, never()).deleteById(todoId);
    }

    @Test
    void updateTodo_whenTodoExists_shouldUpdateAndReturnTodoResponse() {
        TodoUpdateRequest updateRequest = new TodoUpdateRequest(
                "Güncellenmiş Başlık", null, true, null, null, null
        );

        TodoResponse updatedResponse = new TodoResponse(
                1L, "Güncellenmiş Başlık", null, true, now, null, Priority.MEDIUM, null
        );

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        doNothing().when(todoMapper).updateEntity(updateRequest, sampleTodo);
        when(todoRepository.save(sampleTodo)).thenReturn(sampleTodo);
        when(todoMapper.toResponse(sampleTodo)).thenReturn(updatedResponse);

        TodoResponse actualResponse = todoService.updateTodo(1L, updateRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.title()).isEqualTo("Güncellenmiş Başlık");
        assertThat(actualResponse.completed()).isTrue();

        verify(todoRepository, times(1)).findById(1L);
        verify(todoMapper, times(1)).updateEntity(updateRequest, sampleTodo);
        verify(todoRepository, times(1)).save(sampleTodo);
        verify(todoMapper, times(1)).toResponse(sampleTodo);
    }

    @Test
    void updateTodo_whenTodoDoesNotExist_shouldThrowResourceNotFoundException() {
        Long todoId = 99L;

        TodoUpdateRequest updateRequest = new TodoUpdateRequest(
                "Bu guncelleme basarisiz olmali", null, null, null, null, null
        );

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> todoService.updateTodo(todoId, updateRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Todo not found with id: " + todoId);

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoMapper, never()).updateEntity(any(), any());
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void updateTodoCompletion_whenTodoExists_shouldUpdateCompletionAndReturnResponse() {
        boolean newCompletionState = true;

        TodoResponse updatedResponse = new TodoResponse(
                1L, "Test Todo", null, newCompletionState, now, null, Priority.MEDIUM, null
        );

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(todoMapper.toResponse(any(Todo.class))).thenReturn(updatedResponse);

        TodoResponse actualResponse = todoService.updateTodoCompletion(1L, newCompletionState);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(1L);
        assertThat(actualResponse.completed()).isTrue();
        assertThat(sampleTodo.isCompleted()).isTrue();

        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
        verify(todoMapper, times(1)).toResponse(any(Todo.class));
    }

    @Test
    void updateTodoCompletion_whenTodoDoesNotExist_shouldThrowResourceNotFoundException() {
        Long todoId = 99L;

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> todoService.updateTodoCompletion(todoId, true)
        );

        assertThat(exception.getMessage()).isEqualTo("Todo not found with id: " + todoId);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void getAllTodos_whenNoFilter_shouldReturnPagedTodos() {
        List<Long> todoIds = List.of(1L, 2L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());

        Todo sampleTodo2 = new Todo();
        sampleTodo2.setId(2L);
        sampleTodo2.setTitle("İkinci Test Todo");
        List<Todo> todoList = List.of(sampleTodo, sampleTodo2);

        TodoResponse response1 = new TodoResponse(1L, "Test Todo", null, false, now, null, null, null);
        TodoResponse response2 = new TodoResponse(2L, "İkinci Test Todo", null, false, now, null, null, null);
        List<TodoResponse> responseList = List.of(response1, response2);

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class)))
                .thenReturn(idPage);

        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);
        when(todoMapper.toResponseList(todoList)).thenReturn(responseList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(2);
        assertThat(actualPage.getContent().get(0).id()).isEqualTo(1L);
        assertThat(actualPage.getContent().get(1).id()).isEqualTo(2L);


        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class));
        verify(todoRepository, times(1)).findByIdsWithTags(todoIds);
        verify(todoMapper, times(1)).toResponseList(todoList);
    }

    @Test
    void getAllTodos_whenNoTodosExist_shouldReturnEmptyPage() {
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.isEmpty()).isTrue();

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class));
        verify(todoRepository, never()).findByIdsWithTags(anyList());
    }

    @Test
    void getAllTodos_whenCompletedFilterIsTrue_shouldReturnOnlyCompletedTodos() {
        sampleTodo.setCompleted(true);
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);
        TodoResponse response1 = new TodoResponse(1L, "Test Todo", null, true, now, null, null, null);
        List<TodoResponse> responseList = List.of(response1);

        when(todoRepository.findTodoIds(eq(true), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);
        when(todoMapper.toResponseList(todoList)).thenReturn(responseList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(true, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).completed()).isTrue();

        verify(todoRepository, times(1))
                .findTodoIds(eq(true), isNull(), isNull(), isNull(), any(long.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenPriorityFilterIsHigh_shouldReturnOnlyHighPriorityTodos() {
        sampleTodo.setPriority(Priority.HIGH);
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);
        TodoResponse response1 = new TodoResponse(1L, "Test Todo", null, false, now, null, Priority.HIGH, null);
        List<TodoResponse> responseList = List.of(response1);

        when(todoRepository.findTodoIds(isNull(), eq(Priority.HIGH), isNull(), isNull(), any(long.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);
        when(todoMapper.toResponseList(todoList)).thenReturn(responseList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, Priority.HIGH, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).priority()).isEqualTo(Priority.HIGH);

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), eq(Priority.HIGH), isNull(), isNull(), any(long.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenTagFilterExists_shouldReturnTodosWithTag() {
        sampleTodo.setTags(List.of("java", "spring"));
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);
        String tagToSearch = "java";
        TodoResponse response1 = new TodoResponse(1L, "Test Todo", null, false, now, null, null, List.of("java", "spring"));
        List<TodoResponse> responseList = List.of(response1);

        when(todoRepository.findTodoIds(isNull(), isNull(), eq(tagToSearch), isNull(), any(long.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);
        when(todoMapper.toResponseList(todoList)).thenReturn(responseList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, tagToSearch, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1L);
        assertThat(actualPage.getContent().get(0).tags()).contains("java");

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), eq(tagToSearch), isNull(), any(long.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenOverdueFilterIsTrue_shouldReturnOverdueTodos() {
        long pastDeadline = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
        sampleTodo.setDeadline(pastDeadline);
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);
        TodoResponse response1 = new TodoResponse(1L, "Test Todo", null, false, now, pastDeadline, null, null);
        List<TodoResponse> responseList = List.of(response1);

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), eq(true), any(long.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);
        when(todoMapper.toResponseList(todoList)).thenReturn(responseList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, true, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).id()).isEqualTo(1L);

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), eq(true), any(long.class), any(Pageable.class));
    }
}