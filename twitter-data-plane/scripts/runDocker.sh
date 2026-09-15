#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the repository root (two levels up from twitter-data-plane/scripts/)
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Change to the repository root
cd "$REPO_ROOT"

docker run --rm \
  -p 8086:8086 \
  -p 8087:8087 \
  -e POSTGRESQL_DB_USER=social_app \
  -e POSTGRESQL_DB_PASSWORD=123456789 \
  -e POSTGRESQL_DB_URL=postgresql://host.docker.internal:5432/social \
  -e IDENTITY_PRIVATE_KEY_PATH=//run/secrets/identity_private_key \
  -e IDENTITY_PUBLIC_KEY_PATH=//run/secrets/identity_public_key \
  -e MONGODB_CONNECTION_STRING=mongodb://host.docker.internal:27017 \
  -e MONGODB_DATABASE_NAME=social \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  --mount type=bind,src=/d/Uday/Documents/Projects/Social/keys/private_key.pem,dst=/run/secrets/identity_private_key,readonly \
  --mount type=bind,src=/d/Uday/Documents/Projects/Social/keys/public_key.pem,dst=/run/secrets/identity_public_key,readonly \
  --memory="256m" \
  --cpus="0.1" \
  twitter-data-plane
