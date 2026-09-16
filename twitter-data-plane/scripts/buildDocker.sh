#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the repository root (two levels up from twitter-data-plane/scripts/)
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Change to the repository root
cd "$REPO_ROOT"

# Build the Docker image
docker build -f twitter-data-plane/docker/Dockerfile -t twitter-data-plane .