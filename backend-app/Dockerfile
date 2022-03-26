FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx1G", "-XX:ActiveProcessorCount=1", "-XX:+UseContainerSupport", "-XX:+AlwaysActAsServerClassMachine", "-jar", "/app.jar", "--spring.config.location=file:/etc/app/application.yaml"]
