#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the repository root (two levels up from identity/scripts/)
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Change to the repository root
cd "$REPO_ROOT"

docker run --rm \
  -p 8084:8084 \
  -p 8085:8085 \
  -e IDENTITY_PRIVATE_KEY_PATH=//run/secrets/identity_private_key \
  -e IDENTITY_PUBLIC_KEY_PATH=//run/secrets/identity_public_key \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  --mount type=bind,src=/d/Uday/Documents/Projects/Social/keys/private_key.pem,dst=/run/secrets/identity_private_key,readonly \
  --mount type=bind,src=/d/Uday/Documents/Projects/Social/keys/public_key.pem,dst=/run/secrets/identity_public_key,readonly \
  --memory="256m" \
  --cpus="0.1" \
  twitter-control-plane
