# Use the official OpenJDK base image with the version that matches your Java version
FROM openjdk:23-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven or Gradle build artifact (jar file) to the container
# Adjust this based on how your build tool names the jar file
COPY build/libs/demo-0.0.1-SNAPSHOT.jar rest-api.jar

# Expose the port your application runs on (default for Spring Boot is 8080)
EXPOSE 8080

# Run the jar file when the container starts
ENTRYPOINT ["java", "-jar", "rest-api.jar"]