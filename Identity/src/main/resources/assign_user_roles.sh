#!/usr/bin/env bash

set -euo pipefail

API_URL="${API_URL:-http://localhost:8080/user-roles}"
ALICE_ID="${ALICE_ID:-1}"
BOB_ID="${BOB_ID:-2}"
CAROL_ID="${CAROL_ID:-3}"
DAVID_ID="${DAVID_ID:-4}"

assign_role() {
  local user_id="$1"
  local username="$2"
  local role_id="$3"
  local role_name="$4"

  echo "Assigning ${role_name} to ${username}..."
  curl --fail-with-body --silent --show-error \
    --request POST "$API_URL" \
    --header "Content-Type: application/json" \
    --data "{\"userId\":${user_id},\"username\":\"${username}\",\"roleId\":${role_id},\"roleName\":\"${role_name}\",\"status\":\"ACTIVE\"}"
  echo
}

# Alice: ADMIN (role ID 1)
assign_role "$ALICE_ID" "alice" 1 "ADMIN"

# Bob: GET_USER (2), UPDATE_USER (3), DELETE_USER (4)
assign_role "$BOB_ID" "bob" 2 "GET_USER"
assign_role "$BOB_ID" "bob" 3 "UPDATE_USER"
assign_role "$BOB_ID" "bob" 4 "DELETE_USER"

# Carol: GET_USER (role ID 2)
assign_role "$CAROL_ID" "carol" 2 "GET_USER"

# David: GET_USER (2), UPDATE_USER (3)
assign_role "$DAVID_ID" "david" 2 "GET_USER"
assign_role "$DAVID_ID" "david" 3 "UPDATE_USER"
