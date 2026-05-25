FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY build/libs/*.jar app.jar

USER app
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]