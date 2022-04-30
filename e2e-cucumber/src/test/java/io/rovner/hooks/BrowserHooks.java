
package io.rovner.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.rovner.steps.ui.Browser;

public class BrowserHooks {

    private final Browser browser;

    public BrowserHooks(Browser browser) {
        this.browser = browser;
    }

    @After
    public void closeWebDriver(Scenario scenario) {
        browser.close(scenario.isFailed());
    }

}
