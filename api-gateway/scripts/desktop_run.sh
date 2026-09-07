#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to API Gateway module directory
API_GATEWAY_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Change to Identity module directory
cd "$API_GATEWAY_DIR"

# Build the jar file
mvn clean package -DskipTests

# Run the Identity service
java -jar target/api-gateway-1.0-SNAPSHOT.jar server config/base.conf