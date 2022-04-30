package io.rovner.steps.defs.alerts;

import io.cucumber.java.en.Then;
import io.rovner.steps.ui.Browser;
import org.openqa.selenium.Alert;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent;

public class AlertStepDefs {

    private final Browser browser;

    public AlertStepDefs(Browser browser) {
        this.browser = browser;
    }

    @Then("alert with message {string} should be visible")
    public void alertWithMessageShouldBeVisible(String expectedText) {
        browser.newWait(Duration.ofSeconds(30)).until(alertIsPresent());
        Alert alert = browser.getWebDriver().switchTo().alert();
        assertThat(alert.getText())
                .as("Alert text to be equal to " + expectedText)
                .isEqualTo(expectedText);
        alert.accept();
    }

}
