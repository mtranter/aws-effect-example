#!/usr/bin/env bash

set -euo pipefail

export AWS_REGION="ap-southeast-1"
DOCKER_REPO_BASE="$(aws sts get-caller-identity --query 'Account' --output text).dkr.ecr.${AWS_REGION}.amazonaws.com"
CHANNELS_IMAGE="aws-effect-channels"
AUTH_IMAGE="aws-effect-auth"
VERSION=$(git rev-parse --short HEAD)

function create_image_repo() {
    REPO_NAME=$1
    aws ecr describe-repositories --repository-names ${REPO_NAME} || \
        aws ecr create-repository --repository-name ${REPO_NAME}
}

function target_deploy() {

    create_image_repo $CHANNELS_IMAGE
    create_image_repo $AUTH_IMAGE
    docker login --username AWS --password $(aws ecr get-login-password --region $AWS_REGION) $DOCKER_REPO_BASE
    CHANNELS_IMAGE_NAME="$DOCKER_REPO_BASE/$CHANNELS_IMAGE"
    AUTH_IMAGE_NAME="$DOCKER_REPO_BASE/$AUTH_IMAGE"
    earth --push \
          --build-arg CHANNELS_IMAGE_NAME=$CHANNELS_IMAGE_NAME  \
          --build-arg AUTH_IMAGE_NAME=$AUTH_IMAGE_NAME  \
          --build-arg VERSION=$VERSION \
          +package
          
    pushd ./deploy
    terragrunt apply -auto-approve -var auth_image_uri="${AUTH_IMAGE_NAME}:${VERSION}" -var channels_image_uri="${CHANNELS_IMAGE_NAME}:${VERSION}"
    popd
}

if type -t "target_$1" &>/dev/null; then
  target_$1 ${@:2}
else
  echo "usage: $0 <target>

target:
    deploy                     --  Deploy the app
"
  exit 1
fi