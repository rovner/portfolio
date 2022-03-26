package io.rovner.errors;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TodoItemNotFoundException extends ResponseStatusException {
    public TodoItemNotFoundException(long id) {
        super(NOT_FOUND, String.format("Todo item with id %s does not exist", id));
    }
}
