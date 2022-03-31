package io.rovner.helpers;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Request;
import okhttp3.Response;

import static io.rovner.helpers.Allure.step;

public class AllureStepOkHttp3 extends AllureOkHttp3 {

    @Override
    public Response intercept(final Chain chain) {
        final Request request = chain.request();
        String step = String.format("Execute %s %s", request.method(), request.url());
        return step(step, () -> super.intercept(chain));
    }
}
