package io.rovner.tests;

import io.qameta.allure.*;
import io.rovner.enteties.TodoItem;
import io.rovner.helpers.Environment;
import io.rovner.retrofit.TodoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.rovner.helpers.Allure.step;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@Epic("Todo app")
@Story("Todo backend")
@Feature("Todo api")
public class TodoControllerTest {

    private final Environment env = new Environment();
    private TodoService service;

    @Container
    private final PostgreSQLContainer<?> postgresqlContainer = env.getDatabaseContainer();

    @Container
    private final GenericContainer<?> appContainer = env.getAppContainer();

    @BeforeEach
    void before() {
        service = env.getService(TodoService.class);
    }

    @Test
    @DisplayName("List empty todos")
    void shouldListEmptyTodos() throws IOException {
        Response<List<TodoItem>> response = service.getAllTodos().execute();
        step("Assert that rest api response is 200", () -> assertThat(response.isSuccessful()).isTrue());
        step("Assert that todo list is empty", () -> assertThat(response.body()).isEmpty());
    }

    @Test
    @DisplayName("List todos")
    void shouldListNonEmptyTodos() throws IOException {
        env.executeSqlScript("todos_init.sql");
        Response<List<TodoItem>> response = service.getAllTodos().execute();
        step("Assert that rest api response is 200", () -> assertThat(response.isSuccessful()).isTrue());
        step("Assert that todo list contains exact items", () -> assertThat(response.body())
                .containsExactlyInAnyOrder(
                        TodoItem.builder()
                                .id(1L)
                                .deadline(1648543266000L)
                                .task("task 1")
                                .build(),
                        TodoItem.builder()
                                .id(2L)
                                .deadline(1648543266001L)
                                .task("task 2")
                                .build(),
                        TodoItem.builder()
                                .id(1234L)
                                .deadline(1648543266002L)
                                .task("task 1234")
                                .build()));
    }

    @Test
    @DisplayName("Create todo")
    void shouldCreateTodo() throws IOException {

    }

    @Test
    @DisplayName("Response 500 if todo item is not valid")
    void shouldReturn500IfTodoItemIsNotValid() throws IOException {

    }

    @Test
    @DisplayName("Get todo")
    void shouldGetTodo() throws IOException {

    }

    @Test
    @DisplayName("Response 404 on todo get if item does not exist")
    void shouldReturn404OnGetTodoIfDoesNotExist() throws IOException {

    }

    @ParameterizedTest(name = "Update todo item ${0}")
    @CsvSource({
            "with deadline and task, new task, 1648543266015",
            "with deadline, null, 1648543266016",
            "with task, new task, null",
            "without deadline or task, null, null"
    })
    void updateTodoItem(String name, String task, Long deadline) throws IOException {

    }

    @Test
    @DisplayName("Response 404 on todo update if item does not exist")
    void shouldReturn404OnUpdateTodoIfDoesNotExist() throws IOException {

    }

    @Test
    @DisplayName("Delete todo")
    void shouldDeleteTodo() throws IOException {

    }

    @Test
    @DisplayName("Delete todo")
    void shouldReturn200OnDeleteNonExistingItem() throws IOException {

    }
}
