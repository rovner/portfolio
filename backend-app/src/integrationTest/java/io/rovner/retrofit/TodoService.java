package io.rovner.retrofit;

import io.rovner.enteties.TodoItem;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface TodoService {

    @GET("api/v1/todos")
    Call<List<TodoItem>> getAllTodos();

    @GET("api/v1/todos/todo/{id}")
    Call<TodoItem> getItem(Long id);
}
