package io.rovner.repositories;

import io.rovner.enteties.TodoItem;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<TodoItem, Long> {
}