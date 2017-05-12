#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

curl https://sdk.cloud.google.com | bash
GOOGLE_CLOUD_SDK_HOME=/Users/kbuilder/google-cloud-sdk
"$GOOGLE_CLOUD_SDK_HOME"/bin/gcloud components install app-engine-java

cd github/app-gradle-plugin
./gradlew check
# bash <(curl -s https://codecov.io/bash)
