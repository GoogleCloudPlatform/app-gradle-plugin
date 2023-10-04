#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

gcloud components update --quiet
gcloud components install app-engine-java --quiet

# use adoptopenjdk11 until Java 11 support is added to Kokoro MacOS environment
brew install adoptopenjdk11
JAVA_HOME=$(/usr/libexec/java_home -v11)

cd github/app-gradle-plugin
./gradlew check
# bash <(curl -s https://codecov.io/bash)
