#!/bin/bash

# Step 1: Stop previous containers
echo "Stopping Block Puller containers..."
docker compose -f docker-compose.yml -f docker-compose.app.yml stop
echo "Block Puller containers stopped."