#### Backend example app

#####To build execute
`./gradlew :build`
Also this will:
 - execute unit tests
 - execute spotbugs checks, build report
 - collect unit tests code coverage, build coverage report, check minimum coverage

#####To build docker image execute
`./gradlew :docker`

#####To run integration tests execute
`./gradlew :integrationTest`
This will:
 - execute api tests based on retrofit and testcontaiers
 - build allure report