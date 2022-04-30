### End-to-end tests examples with java, cucumber, selenium, and allure

##### Run from command line
`./gradlew cucumber`

##### Idea setup
- Install plugin 'Cucumber for Java'
- Go to `Edit configurations` -> `Edit configuration templates` -> `Cucumber Java` and set
    - Glue: `io.rovner`
    - VM options: `-Dallure.results.directory=build/allure-results -javaagent:build/aspectj.jar`
    - Program Arguments: `--plugin pretty --plugin io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm --plugin org.jetbrains.plugins.cucumber.java.run.CucumberJvm5SMFormatter`
- Run scenario directly from feature file