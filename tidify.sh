#!/bin/bash

# Clean the project
./gradlew clean

# Build the project
./gradlew build

# Set the Docker image name and tag
DOCKER_IMAGE_NAME=tbnsok/tidify-be
DOCKER_IMAGE_TAG=latest

# Build the Docker image using buildx
docker buildx build --platform linux/amd64 --load --tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} . # original

# Push the Docker image to the Docker Hub registry
docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
