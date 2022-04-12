#### Backend example app
- Example of spring boot app with Rest API
- Spring boot unit tests
- Rest API tests based on retrofit and test containers

##### To build execute
`./gradlew :build`
Also this will:
 - execute unit tests
 - execute spotbugs checks, build report
 - collect unit tests code coverage, build coverage report, check minimum coverage

##### To build docker image execute
`./gradlew :docker`

##### To run integration tests execute
`./gradlew :integrationTest`
This will:
 - execute api tests based on retrofit and testcontaiers
 - build allure report