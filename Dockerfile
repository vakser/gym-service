# Use official OpenJDK 21 base image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/gym-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the service port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
