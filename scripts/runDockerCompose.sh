#!/bin/sh

exec docker-compose up -d --build --scale locust-worker=3
