#!/bin/bash

# Package code
mvn clean package spring-boot:repackage

# Run dockerfile
docker build -t bot-#template#-img .

# Start local registry (if you it's not already running)
#docker run -d -p 5000:5000 --name registry registry:2.7

# Push local docker image to registry
docker tag bot-#template#-img localhost:5000/bot-#template#-img
docker push localhost:5000/bot-#template#-img

# Build kubernetes objects with kustomize
kustomize build k8s/local | kubectl apply -f -

# Check status of deployment
kubectl rollout status deployment/bot-#template# -n=bot-#template#-nsp

# Stop the local registry (optional)
#docker container stop registry