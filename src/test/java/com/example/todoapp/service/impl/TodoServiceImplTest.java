package com.example.todoapp.service.impl;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import com.example.todoapp.exception.ResourceNotFoundException;
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

import java.time.LocalDateTime;
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

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo sampleTodo;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        sampleTodo = new Todo();
        sampleTodo.setId(1L);
        sampleTodo.setTitle("Test Todo");
        sampleTodo.setCompleted(false);
        sampleTodo.setCreatedAt(LocalDateTime.now());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getTodoById_whenTodoExists_shouldReturnTodoResponse() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));

        TodoResponse actualResponse = todoService.getTodoById(1L);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.getTitle()).isEqualTo("Test Todo");

        verify(todoRepository, times(1)).findById(1L);
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
    }

    @Test
    void createTodo_shouldSaveAndReturnTodoResponse() {
        TodoCreateRequest createRequest = new TodoCreateRequest();
        createRequest.setTitle("Yeni Test Todo");
        createRequest.setDescription("Bu bir test açıklamasıdır.");

        Todo savedTodo = new Todo();
        savedTodo.setId(2L);
        savedTodo.setTitle(createRequest.getTitle());
        savedTodo.setDescription(createRequest.getDescription());
        savedTodo.setCompleted(false);
        savedTodo.setCreatedAt(LocalDateTime.now());

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        TodoResponse actualResponse = todoService.createTodo(createRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(2L);
        assertThat(actualResponse.getTitle()).isEqualTo("Yeni Test Todo");
        assertThat(actualResponse.isCompleted()).isFalse();

        verify(todoRepository, times(1)).save(any(Todo.class));
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
        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Güncellenmiş Başlık");
        updateRequest.setCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TodoResponse actualResponse = todoService.updateTodo(1L, updateRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.getTitle()).isEqualTo("Güncellenmiş Başlık");
        assertThat(actualResponse.isCompleted()).isTrue();

        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_whenTodoDoesNotExist_shouldThrowResourceNotFoundException() {
        Long todoId = 99L;

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Bu guncelleme basarisiz olmali");

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> todoService.updateTodo(todoId, updateRequest)
        );

        assertThat(exception.getMessage()).isEqualTo("Todo not found with id: " + todoId);

        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void updateTodoCompletion_whenTodoExists_shouldUpdateCompletionAndReturnResponse() {
        when(todoRepository.findById(1L)).thenReturn(Optional.of(sampleTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean newCompletionState = true;

        TodoResponse actualResponse = todoService.updateTodoCompletion(1L, newCompletionState);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.isCompleted()).isTrue();

        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
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

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(idPage);

        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(2);
        assertThat(actualPage.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(actualPage.getContent().get(1).getId()).isEqualTo(2L);


        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class));
        verify(todoRepository, times(1)).findByIdsWithTags(todoIds);
    }

    @Test
    void getAllTodos_whenNoTodosExist_shouldReturnEmptyPage() {
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.isEmpty()).isTrue();

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class));
        verify(todoRepository, never()).findByIdsWithTags(anyList());
    }

    @Test
    void getAllTodos_whenCompletedFilterIsTrue_shouldReturnOnlyCompletedTodos() {
        sampleTodo.setCompleted(true);
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);

        when(todoRepository.findTodoIds(eq(true), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(true, null, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).isCompleted()).isTrue();

        verify(todoRepository, times(1))
                .findTodoIds(eq(true), isNull(), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenPriorityFilterIsHigh_shouldReturnOnlyHighPriorityTodos() {
        sampleTodo.setPriority(Priority.HIGH);
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);

        when(todoRepository.findTodoIds(isNull(), eq(Priority.HIGH), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, Priority.HIGH, null, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).getPriority()).isEqualTo(Priority.HIGH);

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), eq(Priority.HIGH), isNull(), isNull(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenTagFilterExists_shouldReturnTodosWithTag() {
        sampleTodo.setTags(List.of("java", "spring"));
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);
        String tagToSearch = "java";

        when(todoRepository.findTodoIds(isNull(), isNull(), eq(tagToSearch), isNull(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, tagToSearch, null, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).getTags()).contains("java");

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), eq(tagToSearch), isNull(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllTodos_whenOverdueFilterIsTrue_shouldReturnOverdueTodos() {
        sampleTodo.setDeadline(LocalDateTime.now().minusDays(1));
        List<Long> todoIds = List.of(1L);
        Page<Long> idPage = new PageImpl<>(todoIds, pageable, todoIds.size());
        List<Todo> todoList = List.of(sampleTodo);

        when(todoRepository.findTodoIds(isNull(), isNull(), isNull(), eq(true), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(idPage);
        when(todoRepository.findByIdsWithTags(todoIds))
                .thenReturn(todoList);

        Page<TodoResponse> actualPage = todoService.getAllTodos(null, null, null, true, pageable);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(1);
        assertThat(actualPage.getContent().get(0).getId()).isEqualTo(1L);

        verify(todoRepository, times(1))
                .findTodoIds(isNull(), isNull(), isNull(), eq(true), any(LocalDateTime.class), any(Pageable.class));
    }
}