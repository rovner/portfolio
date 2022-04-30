package io.rovner.steps.defs.home;

import io.cucumber.java.en.Given;
import io.rovner.steps.ui.pages.HomePage;

public class HomePageStepDefs {

    private final HomePage homePage;

    public HomePageStepDefs(HomePage homePage) {
        this.homePage = homePage;
    }

    @Given("the home page is opened")
    public void theHomePageIsOpened() {
        homePage.openHomePage();
    }
}
