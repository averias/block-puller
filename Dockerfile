#FROM ubuntu:latest
#LABEL authors="bewater"
#
#ENTRYPOINT ["top", "-b"]

# Use a base image with Java runtime
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built Spring Boot jar into the container
COPY target/block-puller-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8085

# Run the jar file
CMD ["java", "-jar", "app.jar"]
