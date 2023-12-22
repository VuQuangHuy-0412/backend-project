# Stage 1: Build the Spring Boot application
FROM openjdk:17-jdk-alpine AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew && ./gradlew build # Or replace with your build command (e.g., Maven or Gradle)

# Stage 2: Create the final image with only the built artifact
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/backend-project-0.0.1-SNAPSHOT.jar /app/backend-project.jar

EXPOSE 8084
CMD ["java", "-jar", "backend-project.jar"]
