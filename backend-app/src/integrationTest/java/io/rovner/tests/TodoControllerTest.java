package io.rovner.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.rovner.dao.TodoItemDao;
import io.rovner.enteties.TodoItem;
import io.rovner.helpers.Environment;
import io.rovner.retrofit.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static io.rovner.helpers.Allure.step;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@Epic("Todo app")
@Story("Todo backend")
@Feature("Todo api")
public class TodoControllerTest {

    private TodoService service;
    private TodoItemDao dao;

    @RegisterExtension
    private final Environment env = new Environment();

    @Container
    private final PostgreSQLContainer<?> postgresqlContainer = env.getDatabaseContainer();

    @Container
    private final GenericContainer<?> appContainer = env.getAppContainer();

    @BeforeEach
    void before() {
        service = env.getService(TodoService.class);
        dao = env.getDao(TodoItemDao.class);
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
        List<TodoItem> todos = asList(TodoItem.builder()
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
                        .build());
        todos.forEach(item -> dao.addTodo(item));
        Response<List<TodoItem>> response = service.getAllTodos().execute();
        step("Assert that rest api response is 200", () -> assertThat(response.code()).isEqualTo(200));
        step("Assert that todo list contains exact items", () -> assertThat(response.body()).isEqualTo(todos));
    }

    @Test
    @DisplayName("Create todo")
    void shouldCreateTodo() throws Exception {
        TodoItem task = TodoItem.builder()
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        Response<TodoItem> response = service.addItem(task).execute();
        step("Assert that rest api response is 200", () -> assertThat(response.code()).isEqualTo(200));

        step("Assert that created item exist in database", () ->
                assertThat(dao.getAllTodos()).containsExactlyInAnyOrder(task.toBuilder().id(1L).build()));
    }

    @Test
    @DisplayName("Response 500 if todo item is not valid")
    void shouldReturn500IfTodoItemIsNotValid() throws IOException {
        TodoItem task = TodoItem.builder()
                .deadline(System.currentTimeMillis())
                .task(StringUtils.repeat("a", 256))
                .build();
        Response<TodoItem> response = service.addItem(task).execute();
        step("Assert that rest api response is 500", () -> assertThat(response.code()).isEqualTo(500));
        step("Assert that created item does not exist in database", () -> assertThat(dao.getAllTodos()).isEmpty());
    }

    @Test
    @DisplayName("Get todo")
    void shouldGetTodo() throws IOException {
        TodoItem task = TodoItem.builder()
                .id(1L)
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        dao.addTodo(task);
        Response<TodoItem> response = service.getItem(1L).execute();
        step("Assert that rest api response is 200", () -> assertThat(response.code()).isEqualTo(200));
        step("Assert that item is correct", () ->
                assertThat(dao.getAllTodos()).containsExactlyInAnyOrder(task));
    }

    @Test
    @DisplayName("Response 404 on todo get if item does not exist")
    void shouldReturn404OnGetTodoIfDoesNotExist() throws IOException {
        Response<TodoItem> response = service.getItem(1L).execute();
        step("Assert that rest api response is 404", () -> assertThat(response.code()).isEqualTo(404));
    }

    @ParameterizedTest(name = "Update todo item {0}")
    @CsvSource({
            "with deadline and task, new task, 1648543266015",
            "with deadline,, 1648543266016",
            "with task, new task,",
            "without deadline or task,,"
    })
    void updateTodoItem(@SuppressWarnings("unused") String name, String newTask, Long newDeadline) throws IOException {
        Long oldDeadline = 1648543266010L;
        String oldTask = "test";
        TodoItem oldItem = TodoItem.builder()
                .id(1L)
                .deadline(oldDeadline)
                .task(oldTask)
                .build();
        dao.addTodo(oldItem);
        TodoItem newItem = TodoItem.builder()
                .deadline(newDeadline)
                .task(newTask)
                .build();
        Response<TodoItem> response = service.updateItem(1L, newItem).execute();
        step("Assert that rest api response is 200", () -> assertThat(response.code()).isEqualTo(200));
        step("Assert that todo is updated in database", () -> assertThat(dao.getTodo(1L))
                .isPresent()
                .get()
                .isEqualTo(TodoItem.builder()
                        .id(1L)
                        .deadline(newDeadline == null ? oldDeadline : newDeadline)
                        .task(newTask == null ? oldTask : newTask)
                        .build()));
    }

    @Test
    @DisplayName("Response 404 on todo update if item does not exist")
    void shouldReturn404OnUpdateTodoIfDoesNotExist() throws IOException {
        TodoItem newItem = TodoItem.builder()
                .deadline(1648543266010L)
                .task("test")
                .build();
        Response<TodoItem> response = service.updateItem(1L, newItem).execute();
        step("Assert that rest api response is 404", () -> assertThat(response.code()).isEqualTo(404));
    }

    @Test
    @DisplayName("Delete todo when item exist")
    void shouldDeleteTodo() throws IOException {
        TodoItem task = TodoItem.builder()
                .id(1L)
                .deadline(System.currentTimeMillis())
                .task("test")
                .build();
        dao.addTodo(task);
        Response<Void> response = service.deleteItem(1L).execute();
        step("Assert that rest api response is 200", () -> assertThat(response.code()).isEqualTo(200));
        step("Assert that todo is updated in database", () -> assertThat(dao.getTodo(1L)).isNotPresent());
    }

    @Test
    @DisplayName("Delete todo when item does not exist")
    void shouldReturn404nDeleteNonExistingItem() throws IOException {
        Response<Void> response = service.deleteItem(1L).execute();
        step("Assert that rest api response is 404", () -> assertThat(response.code()).isEqualTo(404));
    }
}
