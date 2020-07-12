#!/bin/bash

REGISTRY_IP=192.168.99.147
TAG=$REGISTRY_IP/$1:latest
DOCKERFILE=$1/docker/Dockerfile

gradle $1:build

docker build -t $TAG -f $DOCKERFILE .
docker push $TAG

