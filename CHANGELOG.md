# Change Log
All notable changes to this project will be documented in this file.
## [unreleased]

### Added

### Changed

### Fixed

## 1.3.3

### Changed
* Preserve datastore-indexes-auto.xml across non-clean rebuilds ([#165](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/165))

## 1.3.2

### Added
* Allow direct application of standard or flexible plugin ([#144](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/144))

### Fixed
* Fix path to appengine-web.xml in fallback detection of standard or flexible environment ([#136](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/136))

## 1.3.1
### Added
* New `environment` option in the `run` closure to pass environment variables to Dev App Server ([#128](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pulls/128)) ([appengine-plugins-core/381](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/381))
* Automatically read environment from `appengine-web.xml` ([appengine-plugins-core/378](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/378))

### Changed
* Upgrade App Engine Plugins Core dependency to 0.3.2 ([#128](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pulls/128))

## 1.3.0

### Changed
* `appengineShowConfiguration` no longer prints the gradle project parameters of extensions ([#121](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/121))

## 1.3.0-rc1

### Added
* Dev Appserver1 integration ([#113](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/113))
* Primitive [User Guide](USER_GUIDE.md)

### Changed
* Default local dev server is Java Dev Appserver1
* Flex Staging only copies app.yaml to staging out via ([#363](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/363))
* Output directory for ExplodeWar is now `${buildDir}/exploded-<projectName>` instead of `exploded-app` ([#117](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/117))

## 1.1.1

### Fixed
* Flex deployments failing in multimodule configuration ([#108](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/108))
