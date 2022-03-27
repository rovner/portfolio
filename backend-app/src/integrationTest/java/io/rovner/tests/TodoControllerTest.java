package io.rovner.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.rovner.enteties.TodoItem;
import io.rovner.retrofit.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.BindMode.READ_ONLY;
import static org.testcontainers.utility.DockerImageName.parse;

@Slf4j
@Testcontainers
@Epic("Todo app")
@Story("Todo backend")
@Feature("Todo api")
public class TodoControllerTest {

    private final Network network = Network.newNetwork();
    private TodoService service;

    @Container
    private final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres")
            .withNetwork(network)
            .withNetworkAliases("db")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password")
            .withExposedPorts(5432)
            .withLogConsumer(new Slf4jLogConsumer(log));


    @Container
    private final GenericContainer<?> appContainer = new GenericContainer<>(parse("backend-app"))
            .dependsOn(postgresqlContainer)
            .withNetwork(network)
            .withClasspathResourceMapping("application.it.yaml", "/etc/app/application.yaml", READ_ONLY)
            .withExposedPorts(8080)
            .waitingFor(new HttpWaitStrategy().forPort(8080).forPath("/api/v1/todos"))
            .withStartupTimeout(ofMinutes(1))
            .withLogConsumer(new Slf4jLogConsumer(log));

    @BeforeEach
    void before() {
        service = new Retrofit.Builder()
                .baseUrl(String.format("http://localhost:%s", appContainer.getMappedPort(8080)))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoService.class);
    }

    @Test
    @DisplayName("List empty todo list")
    void shouldListEmptyTodos() throws IOException {
        Response<List<TodoItem>> response = service.getAllTodos().execute();
        assertThat(response.isSuccessful())
                .as("Rest api should return 200 OK")
                .isTrue();
        assertThat(response.body())
                .as("Rest api should return empty todo list in case of no data")
                .isEmpty();
    }
}
