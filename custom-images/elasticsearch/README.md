# Running ElasticSearch 6.8 in Docker on Apple M1 Hardware

## Problem
The new MacBook Pro with the M1 chipset does not run the existing ElasticSearch 6.8 image from 
Docker Hub as it only targets the `linux/amd64` platform.

## Solution
These instructions build a Docker image of ElasticSearch 6.8 image that runs on the `linux/arm64` 
platform. It uses a base image from `blacktop/elasticsearch` and adds `curl`, which is required by 
the `elasticsearch:populate-index` job.

This image needs to be manually built and deployed by Dev-ops as it uses the newer `docker buildx` 
commands that are not supported by Concourse.

1. Configure Docker to build multi-arch images
   ```bash
   docker buildx create --name multiarch --driver docker-container --use
   ```
2. Build the ElasticSearch 6.8.13 image for `linux/arm64`
   ```bash
   docker buildx build \
     --push \
     --platform=linux/arm64,linux/amd64 \
     -t 169942020521.dkr.ecr.eu-west-2.amazonaws.com/local/elasticsearch:latest \
     -t 169942020521.dkr.ecr.eu-west-2.amazonaws.com/local/elasticsearch:6.8 \
     -t 169942020521.dkr.ecr.eu-west-2.amazonaws.com/local/elasticsearch:6.8.13 .
   ```
3. Build the ElasticSearch 7 image for `linux/arm64`
   ```bash
   docker buildx build \
   --push \
   --platform=linux/arm64,linux/amd64 \
   -t 169942020521.dkr.ecr.eu-west-2.amazonaws.com/local/elasticserarch:latest \
   -t 169942020521.dkr.ecr.eu-west-2.amazonaws.com/local/elasticsearch:7.10 \
   -f Dockerfile.es7 .
   ```
   