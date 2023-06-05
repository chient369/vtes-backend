# syntax=docker/dockerfile:1

FROM maven:3.8.3-openjdk-11-slim AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:11.0.19_7-jdk-alpine

WORKDIR /app

ARG JAR_FILE=/app/target/vtes-backend-0.0.1-SNAPSHOT.jar

COPY --from=build ${JAR_FILE} ./app.jar

#CMD ["java", "-jar", "app.jar"]
