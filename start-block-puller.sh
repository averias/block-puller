#!/bin/bash

# Step 0: Stop previous containers, if any
echo "Stopping previous containers containers..."
docker compose -f docker-compose.yml -f docker-compose.app.yml stop

# Step 1: Build the Spring Boot application using Maven
echo "Building Block Puller Spring Boot app..."
./mvnw clean package -DskipTests

# Step 2: Build the Docker image
echo "Building Block Puller Spring Boot app Docker image..."
docker build -t block-puller .

# Step 3: Restart the app service using Docker Compose
echo "Building Block Puller Spring Boot app Docker container and infra containers..."
# docker compose up --build -d spring-app
docker compose -f docker-compose.yml -f docker-compose.app.yml up --force-recreate --build block-puller-app

# Step 4: Stop all containers
echo "Stopping all containers..."
docker compose -f docker-compose.yml -f docker-compose.app.yml stop

#echo "Block Puller updated and running in Docker containers."