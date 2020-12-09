FROM alpine:3.11

test:
    BUILD ./frontend+utest
    BUILD ./api+test

package:
    ARG CHANNELS_IMAGE_NAME
    ARG AUTH_IMAGE_NAME
    ARG VERSION
    BUILD ./frontend+package
    BUILD \
          --build-arg CHANNELS_IMAGE_NAME=$CHANNELS_IMAGE_NAME \
          --build-arg AUTH_IMAGE_NAME=$AUTH_IMAGE_NAME \
          --build-arg VERSION=$VERSION \
          ./api+package
