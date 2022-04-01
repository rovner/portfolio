package io.rovner.retrofit;

import io.rovner.enteties.TodoItem;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TodoService {

    @GET("api/v1/todos")
    Call<List<TodoItem>> getAllTodos();

    @GET("api/v1/todos/todo/{id}")
    Call<TodoItem> getItem(@Path("id") Long id);

    @POST("api/v1/todos/todo")
    Call<TodoItem> addItem(@Body TodoItem item);

    @PUT("api/v1/todos/todo/{id}")
    Call<TodoItem> updateItem(@Path("id") Long id, @Body TodoItem item);

    @DELETE("api/v1/todos/todo/{id}")
    Call<Void> deleteItem(@Path("id") Long id);
}
