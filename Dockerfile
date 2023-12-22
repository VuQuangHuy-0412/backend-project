# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy AS builder
RUN $JAVA_HOME/bin/jlink \
    --add-modules java.se \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre/

FROM ubuntu:jammy
ENV JAVA_HOME=/opt/java/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /jre/ $JAVA_HOME

# Use Fat JAR for simplicity
COPY ./target/app.jar ./app.jar
ENTRYPOINT ["java", "-jar", "./app.jar"]
