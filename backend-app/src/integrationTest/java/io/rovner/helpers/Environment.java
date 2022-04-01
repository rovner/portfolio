package io.rovner.helpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.rovner.enteties.TodoItem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static org.testcontainers.containers.BindMode.READ_ONLY;
import static org.testcontainers.ext.ScriptUtils.runInitScript;
import static org.testcontainers.utility.DockerImageName.parse;

@Slf4j
public class Environment implements BeforeEachCallback, AfterEachCallback {

    private final Network network = Network.newNetwork();

    private final PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres:12.10")
            .withStartupAttempts(3)
            .withNetwork(network)
            .withNetworkAliases("db")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("password")
            .withExposedPorts(5432)
            .withLogConsumer(new Slf4jLogConsumer(log));

    private final GenericContainer<?> appContainer = new GenericContainer<>(parse("backend-app"))
            .withStartupAttempts(3)
            .dependsOn(databaseContainer)
            .withNetwork(network)
            .withClasspathResourceMapping("application.it.yaml", "/etc/app/application.yaml", READ_ONLY)
            .withExposedPorts(8080)
            .waitingFor(new HttpWaitStrategy().forPort(8080).forPath("/api/v1/todos"))
            .withStartupTimeout(ofMinutes(1))
            .withLogConsumer(new Slf4jLogConsumer(log));

    private Connection connection;

    public Environment() {
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        databaseContainer.start();
        appContainer.start();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public PostgreSQLContainer<?> getDatabaseContainer() {
        return databaseContainer;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public GenericContainer<?> getAppContainer() {
        return appContainer;
    }

    public <T> T getService(Class<T> serviceClass) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new AllureStepOkHttp3())
                        .build())
                .baseUrl(String.format("http://localhost:%s", appContainer.getMappedPort(8080)))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(serviceClass);
    }

    @SneakyThrows
    public <T> T getDao(Class<T> daoClass) {
        connection = DriverManager.getConnection(
                databaseContainer.getJdbcUrl(), databaseContainer.getUsername(), databaseContainer.getPassword());
        return daoClass.getConstructor(Connection.class)
                .newInstance(connection);
    }

    @Step("Execute sql script")
    public void executeSqlScript(String scriptPath) {
        attachScript(scriptPath);
        runInitScript(new JdbcDatabaseDelegate(databaseContainer, ""), scriptPath);
    }

    @SuppressWarnings("UnusedReturnValue")
    @SneakyThrows
    @Attachment("SQL script")
    private static String attachScript(String scriptPath) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(scriptPath);
        return IOUtils.toString(Objects.requireNonNull(stream), UTF_8);
    }
}
