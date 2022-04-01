package io.rovner.dao;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.qameta.allure.Step;
import io.rovner.enteties.TodoItem;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoItemDao {

    private final Connection connection;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public TodoItemDao(Connection connection) {
        this.connection = connection;
    }

    @SneakyThrows
    @Step("Insert new todo in database")
    public void addTodo(TodoItem item) {
        try (PreparedStatement statement =
                     connection.prepareStatement("insert into todo_item(id, task, deadline) values (?, ?, ?)")) {
            statement.setLong(1, item.getId());
            statement.setString(2, item.getTask());
            statement.setLong(3, item.getDeadline());
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    public List<TodoItem> getAllTodos() {
        List<TodoItem> todos = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from todo_item");
            while (rs.next()) {
                todos.add(TodoItem.builder()
                        .id(rs.getLong("id"))
                        .deadline(rs.getLong("deadline"))
                        .task(rs.getString("task"))
                        .build());
            }
            rs.close();
        }
        return todos;
    }

    @SneakyThrows
    public Optional<TodoItem> getTodo(Long id) {
        try (PreparedStatement statement = connection.prepareStatement("select * from todo_item where id = ?")) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                if (!rs.isLast()) {
                    throw new RuntimeException(String.format("More than one todo item fetched by id %d", id));
                }
                return Optional.of(TodoItem.builder()
                        .id(rs.getLong("id"))
                        .deadline(rs.getLong("deadline"))
                        .task(rs.getString("task"))
                        .build());
            }
            rs.close();
            return Optional.empty();
        }
    }
}
