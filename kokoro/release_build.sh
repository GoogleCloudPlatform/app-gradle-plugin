#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

# workaround for gcloud update failures (remove in future) [https://issuetracker.google.com/issues/119096137]
sudo /opt/google-cloud-sdk/bin/gcloud components remove container-builder-local

sudo /opt/google-cloud-sdk/bin/gcloud components update
sudo /opt/google-cloud-sdk/bin/gcloud components install app-engine-java

cd github/app-gradle-plugin
./gradlew check prepareRelease
