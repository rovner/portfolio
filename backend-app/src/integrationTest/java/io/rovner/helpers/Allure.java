package io.rovner.helpers;

import io.qameta.allure.Step;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;

public class Allure {

    @Step("{step}")
    @SneakyThrows
    public static <T> T step(@SuppressWarnings("unused") String step, Callable<T> callable) {
        return callable.call();
    }

    @Step("{step}")
    public static void step(@SuppressWarnings("unused") String step, Runnable runnable) {
        runnable.run();
    }
}
