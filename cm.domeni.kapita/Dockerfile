# syntax=docker/dockerfile:1.7

FROM gradle:9.3.1-jdk25 AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts ./
COPY openapi openapi
COPY src src

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jdk-jammy AS runtime
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=builder /workspace/build/libs/kapita-*.jar /app/app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "--enable-preview", "-jar", "/app/app.jar"]
