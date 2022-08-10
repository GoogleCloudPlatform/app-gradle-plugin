#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

sudo -E /opt/google-cloud-sdk/bin/gcloud components update
sudo -E /opt/google-cloud-sdk/bin/gcloud components install app-engine-java

cd github/app-gradle-plugin
./gradlew check prepareRelease
