#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

gcloud components update
gcloud components install app-engine-java

cd github/app-gradle-plugin
./gradlew check
# bash <(curl -s https://codecov.io/bash)
