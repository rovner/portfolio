package io.rovner.steps.api.services;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.rovner.Config.getApiUrl;

public class ServiceUtils {
    public static <T> T buildService(Class<T> serviceClass) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new AllureOkHttp3())
                        .build())
                .baseUrl(getApiUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(serviceClass);
    }
}
