#!/usr/bin/env bash

set -euo pipefail

API_URL="${API_URL:-http://localhost:8080/users}"
USER_PASSWORD="${USER_PASSWORD:-ChangeMe123!}"

create_user() {
  local username="$1"
  local email="$2"

  echo "Creating ${username}..."
  curl --fail-with-body --silent --show-error \
    --request POST "$API_URL" \
    --header "Content-Type: application/json" \
    --data "{\"username\":\"${username}\",\"email\":\"${email}\",\"password\":\"${USER_PASSWORD}\"}"
  echo
}

create_user "alice" "alice@example.com"
create_user "bob" "bob@example.com"
create_user "carol" "carol@example.com"
create_user "david" "david@example.com"
