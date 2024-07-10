FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /usr/src/app

COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 mvn clean package spring-boot:repackage


FROM gcr.io/distroless/java21:nonroot

COPY --from=build /usr/src/app/target/telegram-bot-1.0.0.jar /home/nonroot/app/app.jar

# EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/home/nonroot/app/app.jar"]
