# Stage 1: Build the Spring Boot application
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew && ./gradlew build # Or replace with your build command (e.g., Maven or Gradle)

# Stage 2: Create the final image with only the built artifact
FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
COPY --from=build /app/build/libs/backend-project-0.0.1-SNAPSHOT.jar /app/backend-project.jar

RUN apt-get update; apt-get install -y fontconfig libfreetype6

EXPOSE 8084
CMD ["java", "-jar", "backend-project.jar"]
