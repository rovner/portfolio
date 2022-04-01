FROM openjdk:8-jdk-alpine
ENV JAVA_OPTS=""
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -Xmx1G -XX:ActiveProcessorCount=1 -XX:+UseContainerSupport -XX:+AlwaysActAsServerClassMachine -jar /app.jar --spring.config.location=file:/etc/app/application.yaml
