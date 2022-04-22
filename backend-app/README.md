### Backend example app
- Example of spring boot app with Rest API
- Spring boot unit tests
- Rest API tests based on retrofit and test containers

##### Build: 
`./gradlew :build`

This will:
 - execute unit tests
 - execute spotbugs checks, build report
 - collect unit tests code coverage, build coverage report, check minimum coverage

##### Build docker image: 
`./gradlew :docker`

##### Run integration tests: 
`./gradlew :integrationTest`

This will:
 - execute api tests based on retrofit and testcontaiers
 - build allure report