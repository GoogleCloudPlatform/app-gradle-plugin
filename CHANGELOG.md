# Change Log
All notable changes to this project will be documented in this file.
## [unreleased]

### Added

### Changed

### Fixed

## 1.3.1
### Added
* New `environment` option in the `run` closure to pass environment variables to Dev App Server ([#128](../../pulls/128)) ([appengine-plugins-core/381](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/381))
* Automatically read environment from `appengine-web.xml` ([appengine-plugins-core/378](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/378))

### Changed
* Upgrade App Engine Plugins Core dependency to 0.3.2 ([#128](../../pulls/128))

## 1.3.0

### Changed
* `appengineShowConfiguration` no longer prints the gradle project parameters of extensions ([#121](../../pull/121))

## 1.3.0-rc1

### Added
* Dev Appserver1 integration ([#113](../../pull/113))
* Primitive [User Guide](USER_GUIDE.md)

### Changed
* Default local dev server is Java Dev Appserver1
* Flex Staging only copies app.yaml to staging out via ([#363](../../pull/363))
* Output directory for ExplodeWar is now `${buildDir}/exploded-<projectName>` instead of `exploded-app` ([#117](../../pull/117))

## 1.1.1

### Fixed
* Flex deployments failing in multimodule configuration ([#108](../../issues/108))
