# Use Eclipse Temurin (AdoptOpenJDK) image with Java 21
FROM eclipse-temurin:21-jdk-jammy


# Set working directory in container
WORKDIR /app


# Copy the built jar file into the container
COPY target/library-management-0.0.1-SNAPSHOT.jar app.jar


# Expose port that Spring Boot runs on (default 8080)
EXPOSE 8080


# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
