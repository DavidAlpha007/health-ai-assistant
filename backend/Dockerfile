# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build output (JAR file) to the container
COPY target/health-ai-assistant-1.0.0.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]