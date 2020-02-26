#!/bin/bash

export TERM=${TERM:-dumb}
cd web-api-application
./gradlew --no-daemon build
