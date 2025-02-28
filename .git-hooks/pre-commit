#!/bin/bash

echo "Running pre-commit checks for Spring Boot backend..."

# Step 1: Build Check - Ensure the project compiles
echo "Running build check with Maven verify..."
mvn clean verify
if [ $? -ne 0 ]; then
    echo "Error: Maven build failed."
    EXIT_CODE=1
fi
