#!/usr/bin/env bash

# Build the project
./gradlew clean && ./gradlew build

if [[ $? -eq 0 ]]; then
  tag=latest #$(openssl rand -hex 6)

  # Build the image
  docker build --platform linux/amd64 -t "$PERSONAL_AWS.dkr.ecr.us-east-1.amazonaws.com/auroraweather:$tag" .

  # Get ECR login creds, and deploy
  aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "$PERSONAL_AWS.dkr.ecr.us-east-1.amazonaws.com"
  docker push "$PERSONAL_AWS.dkr.ecr.us-east-1.amazonaws.com/auroraweather:$tag"
else
  echo "Gradle build failed"
fi
