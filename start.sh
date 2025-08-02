#!/bin/bash

# Start services
docker-compose up -d --build

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
while ! docker-compose exec db mysqladmin ping -h localhost --silent; do
    sleep 1
done

echo "MySQL is ready!"

# Initialize database (Flyway will handle this automatically)
echo "Application is starting..."