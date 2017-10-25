#!/bin/bash -
# Usage: ./prepare_release.sh <release version>

set -e

Colorize() {
	echo "$(tput setff $2)$1$(tput sgr0)"
}

EchoRed() {
	echo "$(tput setaf 1; tput bold)$1$(tput sgr0)"
}
EchoGreen() {
	echo "$(tput setaf 2; tput bold)$1$(tput sgr0)"
}

Die() {
	EchoRed "$1"
	exit 1
}

DieUsage() {
    Die "Usage: ./prepare_release.sh <release version>"
}

# Usage: CheckVersion <version>
CheckVersion() {
    [[ $1 =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] || Die "Version not in ###.###.### format."
}

[ $# -ne 2 ] || DieUsage

EchoGreen '===== RELEASE SETUP SCRIPT ====='

VERSION=$1
CheckVersion ${VERSION}

if [[ $(git status -uno --porcelain) ]]; then
    Die 'There are uncommitted changes.'
fi

# Checks out a new branch for this version release (eg. 1.5.7).
git checkout -b release_v${VERSION}

# Changes the version for release and creates the commits/tags.
echo | ./gradlew release

# Pushes the release branch to Github.
git push --tags --set-upstream origin ${VERSION}

# File a PR on Github for the new branch. Have someone LGTM it, which gives you permission to continue.
EchoGreen 'File a PR for the new release branch:'
echo https://github.com/GoogleCloudPlatform/app-gradle-plugin/compare/release_v${VERSION}
