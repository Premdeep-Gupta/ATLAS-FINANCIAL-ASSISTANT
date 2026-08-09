#!/bin/bash

# Atlas AI Financial Assistant Startup Script

echo "============================================================"
echo "    ATLAS AI FINANCIAL ASSISTANT - SPRING BOOT PLATFORM     "
echo "============================================================"

# Navigate to backend directory
cd "$(dirname "$0")/backend" || exit 1

# Check if .env exists in root
if [ -f "../.env" ]; then
    echo "Loading environment variables from .env file..."
    export $(grep -v '^#' ../.env | xargs)
fi

echo "Building and launching Spring Boot Application..."
./mvnw spring-boot:run
