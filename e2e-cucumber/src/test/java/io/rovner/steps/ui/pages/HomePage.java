package io.rovner.steps.ui.pages;

import io.rovner.Config;
import io.rovner.steps.ui.Browser;

import static io.rovner.Config.getUiUrl;

public class HomePage {

    private final Browser browser;

    public HomePage(Browser browser) {
        this.browser = browser;
    }

    public void openHomePage() {
        browser.getWebDriver().get(getUiUrl());
    }
}
