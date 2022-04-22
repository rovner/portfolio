FROM openjdk:8-jdk-alpine as base
RUN apk update && apk upgrade
ENV JAVA_OPTS=""
WORKDIR /app

FROM base as dev
RUN apk add inotify-tools
CMD ["sh", "hot-reload.sh"]

FROM base as build
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
# cache gradle and dependencies
RUN ./gradlew --no-daemon --console=plain bootJar || return 0
COPY . .
RUN ./gradlew --no-daemon --console=plain bootJar

FROM base as prod
COPY --from=build /app/build/libs/backend-app.jar .
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -Xmx1G -XX:ActiveProcessorCount=1 -XX:+UseContainerSupport -XX:+AlwaysActAsServerClassMachine -jar /app/backend-app.jar --spring.config.location=file:/etc/app/application.yaml
