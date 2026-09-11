#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the repository root (two levels up from identity/scripts/)
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Change to the repository root
cd "$REPO_ROOT"

docker run -p 8082:8082 -p 8083:8083 \
  -e IDENTITY_SERVICE_URL=http://host.docker.internal:8080 \
  -e JEDIS_ENDPOINT=host.docker.internal \
  -e PORT=6379 \
  -e RATE_LIMIT_STRATEGY_NAME=TOKEN_BUCKET \
  --memory="256m" \
  --cpus="0.1" \
  api-gateway-service