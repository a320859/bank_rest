# Stage 1: Build and test with Java 17
FROM openjdk:17-jdk-slim AS test
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package

# Stage 2: Build app for runtime on Java 21
FROM openjdk:21-jdk-slim AS runtime
WORKDIR /app
COPY --from=test /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
