package io.rovner.dao;

import io.qameta.allure.Step;
import io.rovner.enteties.TodoItem;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoItemDao {

    private final Connection connection;

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
        }
        return todos;
    }
}
