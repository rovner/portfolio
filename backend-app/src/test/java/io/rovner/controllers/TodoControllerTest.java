package io.rovner.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rovner.enteties.TodoItem;
import io.rovner.repositories.TodoRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Named.of;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@DisplayName("Todo rest controller test")
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository repository;

    @Test
    @DisplayName("List empty todo list")
    void shouldListEmptyTodos() throws Exception {
        when(repository.findAll()).thenReturn(emptyList());
        mockMvc.perform(get("/api/v1/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(asJson(emptyList())));
    }

    @Test
    @DisplayName("List todo list with 2 items")
    void shouldListTodos() throws Exception {
        List<TodoItem> items = asList(
                TodoItem.builder()
                        .id(1L)
                        .deadline(System.currentTimeMillis())
                        .task("test1")
                        .build(),
                TodoItem.builder()
                        .id(1000L)
                        .deadline(System.currentTimeMillis() + 60_000)
                        .task("test1000")
                        .build()
        );
        when(repository.findAll()).thenReturn(items);
        mockMvc.perform(get("/api/v1/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(asJson(items)));
    }

    @Test
    @DisplayName("Get todo item by id - item exist")
    void shouldReturnItemById() throws Exception {
        TodoItem item = TodoItem.builder()
                .id(1L)
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        mockMvc.perform(get("/api/v1/todos/todo/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(asJson(item)));
    }

    @Test
    @DisplayName("Get todo item by id - item does not exit")
    void shouldReturn404WhenNotFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/todos/todo/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create todo item")
    void shouldCreateItem() throws Exception {
        TodoItem item = TodoItem.builder()
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        TodoItem itemWithId = item.toBuilder()
                .id(1L)
                .build();
        when(repository.save(item)).thenReturn(itemWithId);
        mockMvc.perform(post("/api/v1/todos/todo")
                        .contentType(APPLICATION_JSON)
                        .content(asJson(item)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(asJson(itemWithId)));
    }

    @ParameterizedTest(name = "Update todo item ${0}")
    @MethodSource("updateArguments")
    void shouldUpdateItem(TodoItem item) throws Exception {
        TodoItem itemWithId = item.toBuilder()
                .id(1L)
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(TodoItem.builder()
                .id(1L)
                .task("old task")
                .deadline(System.currentTimeMillis() - 10000)
                .build()));
        when(repository.save(any())).thenReturn(itemWithId);
        mockMvc.perform(put("/api/v1/todos/todo/1")
                        .contentType(APPLICATION_JSON)
                        .content(asJson(item)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(asJson(itemWithId)));
    }

    @Test
    @DisplayName("Update todo item - item does not exit")
    void shouldReturn404WhenItemDoesNotExist() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/v1/todos/todo/1")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Delete todo item")
    void shouldDeleteItem() throws Exception {
        TodoItem item = TodoItem.builder()
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(repository).delete(item);
        mockMvc.perform(delete("/api/v1/todos/todo/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete non existing todo item")
    void shouldReturn404WhenDeletingNonExistingItem() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/api/v1/todos/todo/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(repository, never()).delete(any());
    }

    static Stream<Arguments> updateArguments() {
        return Stream.of(
                arguments(of("without task or deadline", TodoItem.builder()
                        .build())),
                arguments(of("with task and deadline", TodoItem.builder()
                        .deadline(System.currentTimeMillis())
                        .task("test")
                        .build())),
                arguments(of("with task only", TodoItem.builder()
                        .task("test")
                        .build())),
                arguments(of("with deadline only", TodoItem.builder()
                        .deadline(System.currentTimeMillis())
                        .build()))
        );
    }

    @SneakyThrows
    private static String asJson(Object object) {
        return new ObjectMapper().writeValueAsString(object);
    }
}

