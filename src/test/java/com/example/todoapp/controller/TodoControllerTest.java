package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoCreateRequest;
import com.example.todoapp.dto.TodoResponse;
import com.example.todoapp.dto.TodoUpdateRequest;
import com.example.todoapp.enums.Priority;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService todoService;

    @Test
    void getTodoById_whenTodoExists_shouldReturn200AndTodoResponse() throws Exception {

        TodoResponse fakeResponse = new TodoResponse();
        fakeResponse.setId(1L);
        fakeResponse.setTitle("Controller Test Todo");
        fakeResponse.setCompleted(false);
        fakeResponse.setPriority(Priority.MEDIUM);
        fakeResponse.setCreatedAt(LocalDateTime.now());

        when(todoService.getTodoById(1L)).thenReturn(fakeResponse);

        mockMvc.perform(
                        get("/api/todos/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Controller Test Todo"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void getTodoById_whenTodoDoesNotExist_shouldReturn404NotFound() throws Exception {

        when(todoService.getTodoById(99L))
                .thenThrow(new ResourceNotFoundException("Todo not found with id: 99"));

        mockMvc.perform(
                        get("/api/todos/99")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Todo not found with id: 99"));
    }

    @Test
    void createTodo_whenRequestIsValid_shouldReturn201CreatedAndTodoResponse() throws Exception {

        TodoCreateRequest createRequest = new TodoCreateRequest();
        createRequest.setTitle("Yeni Todo Oluştur");
        createRequest.setPriority(Priority.LOW);

        TodoResponse fakeResponse = new TodoResponse();
        fakeResponse.setId(1L);
        fakeResponse.setTitle("Yeni Todo Oluştur");
        fakeResponse.setPriority(Priority.LOW);
        fakeResponse.setCompleted(false);
        fakeResponse.setCreatedAt(LocalDateTime.now()); // bunu kontrol etme

        when(todoService.createTodo(any(TodoCreateRequest.class))).thenReturn(fakeResponse);

        mockMvc.perform(
                        post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Yeni Todo Oluştur"))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    @Test
    void createTodo_whenTitleIsBlank_shouldReturn400BadRequest() throws Exception {

        TodoCreateRequest invalidRequest = new TodoCreateRequest();
        invalidRequest.setTitle("");
        invalidRequest.setDescription("Bu test başarısız olmalı");

        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(
                        post("/api/todos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation Error(s): title: Title (başlık) alanı boş olamaz."));

        verify(todoService, never()).createTodo(any(TodoCreateRequest.class));
    }

    @Test
    void deleteTodo_whenTodoExists_shouldReturn204NoContent() throws Exception {

        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(
                        delete("/api/todos/1")
                )
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo(1L);
    }

    @Test
    void deleteTodo_whenTodoDoesNotExist_shouldReturn404NotFound() throws Exception {

        Long todoId = 99L;

        doThrow(new ResourceNotFoundException("Todo not found with id: " + todoId))
                .when(todoService).deleteTodo(todoId);

        mockMvc.perform(
                        delete("/api/todos/" + todoId)
                )
                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Todo not found with id: " + todoId));

        verify(todoService, times(1)).deleteTodo(todoId);
    }

    @Test
    void updateTodo_whenTodoExistsAndRequestIsValid_shouldReturn200AndUpdatedTodo() throws Exception {

        Long todoId = 1L;

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Güncellenmiş Başlık");
        updateRequest.setCompleted(true);

        TodoResponse fakeResponse = new TodoResponse();
        fakeResponse.setId(todoId);
        fakeResponse.setTitle("Güncellenmiş Başlık");
        fakeResponse.setCompleted(true);
        fakeResponse.setPriority(Priority.LOW);

        when(todoService.updateTodo(any(Long.class), any(TodoUpdateRequest.class)))
                .thenReturn(fakeResponse);

        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(
                        put("/api/todos/" + todoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value("Güncellenmiş Başlık"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateTodo_whenTodoDoesNotExist_shouldReturn404NotFound() throws Exception {

        long todoId = 99L;

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Bu güncelleme başarısız olmalı");

        when(todoService.updateTodo(any(Long.class), any(TodoUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Todo not found with id: " + todoId));

        String jsonRequest = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(
                        put("/api/todos/" + todoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Todo not found with id: " + todoId));
    }

    @Test
    void updateTodo_whenDescriptionIsTooLong_shouldReturn400BadRequest() throws Exception {

        long todoId = 1L;
        TodoUpdateRequest invalidRequest = new TodoUpdateRequest();
        String longDescription = "1234567890123456789012345678901";
        invalidRequest.setDescription(longDescription);

        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(
                        put("/api/todos/" + todoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation Error(s): description: Açıklama (description) en fazla 30 karakter olabilir."));

        verify(todoService, never()).updateTodo(any(Long.class), any(TodoUpdateRequest.class));
    }

    @Test
    void updateTodoCompletion_whenTodoExists_shouldReturn200AndUpdatedTodo() throws Exception {

        long todoId = 1L;
        boolean isCompleted = true;

        TodoResponse fakeResponse = new TodoResponse();
        fakeResponse.setId(todoId);
        fakeResponse.setTitle("Tamamlandı olarak işaretlendi");
        fakeResponse.setCompleted(isCompleted);

        when(todoService.updateTodoCompletion(todoId, isCompleted)).thenReturn(fakeResponse);

        mockMvc.perform(
                        put("/api/todos/" + todoId + "/completion")
                                .param("isCompleted", "true")
                )
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value("Tamamlandı olarak işaretlendi"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService, times(1)).updateTodoCompletion(todoId, isCompleted);
    }

    @Test
    void updateTodoCompletion_whenTodoDoesNotExist_shouldReturn404NotFound() throws Exception {

        Long todoId = 99L;
        boolean isCompleted = true;

        when(todoService.updateTodoCompletion(todoId, isCompleted))
                .thenThrow(new ResourceNotFoundException("Todo not found with id: " + todoId));

        mockMvc.perform(
                        put("/api/todos/" + todoId + "/completion")
                                .param("isCompleted", "true")
                )
                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Todo not found with id: " + todoId));

        verify(todoService, times(1)).updateTodoCompletion(todoId, isCompleted);
    }

    @Test
    void getAllTodos_whenNoFilter_shouldReturn200AndTodoPage() throws Exception {

        TodoResponse fakeTodo1 = new TodoResponse();
        fakeTodo1.setId(1L);
        fakeTodo1.setTitle("İlk Todo");
        List<TodoResponse> fakeList = List.of(fakeTodo1);
        Page<TodoResponse> fakePage = new PageImpl<>(fakeList, Pageable.ofSize(10), fakeList.size());

        when(todoService.getAllTodos(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(fakePage);

        mockMvc.perform(
                        get("/api/todos")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getAllTodos_whenNoTodosExist_shouldReturn200AndEmptyPage() throws Exception {

        Page<TodoResponse> fakeEmptyPage = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(10), 0);

        when(todoService.getAllTodos(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(fakeEmptyPage);

        mockMvc.perform(
                        get("/api/todos")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getAllTodos_whenTagFilterExists_shouldReturn200AndFilteredPage() throws Exception {

        TodoResponse fakeTodo1 = new TodoResponse();
        fakeTodo1.setId(1L);
        fakeTodo1.setTitle("Java Todo");
        fakeTodo1.setTags(List.of("java"));

        List<TodoResponse> fakeList = List.of(fakeTodo1);
        Page<TodoResponse> fakePage = new PageImpl<>(fakeList, Pageable.ofSize(10), fakeList.size());

        String tagToSearch = "java";

        when(todoService.getAllTodos(isNull(), isNull(), eq(tagToSearch), isNull(), any(Pageable.class)))
                .thenReturn(fakePage);

        mockMvc.perform(
                        get("/api/todos")
                                .param("tag", tagToSearch)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getAllTodos_whenOverdueFilterExists_shouldReturn200AndFilteredPage() throws Exception {

        TodoResponse fakeTodo1 = new TodoResponse();
        fakeTodo1.setId(1L);
        fakeTodo1.setTitle("Gecikmiş Görev");

        List<TodoResponse> fakeList = List.of(fakeTodo1);
        Page<TodoResponse> fakePage = new PageImpl<>(fakeList, Pageable.ofSize(10), fakeList.size());

        when(todoService.getAllTodos(isNull(), isNull(), isNull(), eq(true), any(Pageable.class)))
                .thenReturn(fakePage);

        mockMvc.perform(
                        get("/api/todos")
                                .param("overdue", "true")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Gecikmiş Görev"));
    }
}