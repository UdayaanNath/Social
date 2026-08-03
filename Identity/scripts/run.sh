#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to Identity module directory
IDENTITY_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Change to Identity module directory
cd "$IDENTITY_DIR"

# Build the jar file
mvn clean package -DskipTests

# Run the Identity service
java -jar target/Identity-1.0-SNAPSHOT.jar server config/base.conf
