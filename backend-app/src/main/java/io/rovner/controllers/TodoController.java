package io.rovner.controllers;

import io.rovner.enteties.TodoItem;
import io.rovner.errors.TodoItemNotFoundException;
import io.rovner.repositories.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings("unused")
@RestController
@RequestMapping("api/v1/todos")
@Slf4j
public class TodoController {

    @Autowired
    private TodoRepository repository;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Iterable<TodoItem> getItems() {
        return repository.findAll();
    }

    @GetMapping(value = "todo/{id}", produces = APPLICATION_JSON_VALUE)
    public TodoItem getItem(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TodoItemNotFoundException(id));
    }

    @PostMapping(value = "todo", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public TodoItem addItem(@RequestBody TodoItem item) {
        return repository.save(item);
    }

    @PutMapping(value = "todo/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public TodoItem updateItem(@PathVariable Long id, @RequestBody TodoItem item) {
        TodoItem newItem = repository.findById(id)
                .orElseThrow(() -> new TodoItemNotFoundException(id));
        if (item.getDeadline() != null) {
            newItem.setDeadline(item.getDeadline());
        }
        if (item.getTask() != null) {
            newItem.setTask(item.getTask());
        }
        return repository.save(newItem);
    }

    @DeleteMapping(value = "todo/{id}")
    public void deleteItem(@PathVariable Long id) {
        repository.deleteById(id);
    }
}