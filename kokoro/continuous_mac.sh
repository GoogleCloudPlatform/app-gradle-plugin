#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

curl https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-154.0.1-darwin-x86_64.tar.gz -o google-cloud-sdk-154.0.1-darwin-x86_64.tar.gz
tar -xzf google-cloud-sdk-154.0.1-darwin-x86_64.tar.gz
./google-cloud-sdk/install.sh

./google-cloud-sdk/bin/gcloud components update
./google-cloud-sdk/bin/gcloud components install app-engine-java

GOOGLE_CLOUD_SDK_HOME="$(pwd)"/google-cloud-sdk

cd github/app-gradle-plugin
./gradlew check
# bash <(curl -s https://codecov.io/bash)
