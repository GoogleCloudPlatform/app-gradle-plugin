# Change Log
All notable changes to this project will be documented in this file.
## [unreleased]

### Added
* New `cloudSdkVersion` parameter to specify desired Cloud SDK version.
* New `downloadCloudSdk` task installs/updates the Cloud SDK and Java App Engine components ([#205](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/205)). 
Task runs automatically when `cloudSdkHome` is not configured.
* New `checkCloudSdk` task validates the Cloud SDK installation ([#212](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/212)). 
Task runs automatically when `cloudSdkHome` and `cloudSdkVersion` are both configured.
* New `appengine.tools.serviceAccountKeyFile` configuration parameter, and
  `appengineCloudSdkLogin` task. ([#235](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/212))
* New `appengineDeployAll` task to deploy application with all valid yaml configs simultaneously. ([#239](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/239), [#240](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/240))

### Changed
* Upgrade App Engine Plugins Core dependency to 0.5.2
* Remove deprecated `appYamls` parameter

### Fixed

## 1.3.5

### Added
* Build Extensions Statically ([#192](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/192))

### Fixed
* Make extensions accessible to Kotlin users ([#191](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/191))

## 1.3.4

### Added
* Check minimum gradle version ([#169](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/169))
* New `<additionalArguments>` parameter to pass additional arguments to Dev App Server ([#179](../../pulls/179)),
relevant pull request in App Engine Plugins Core:
[appengine-plugins-core/433](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/433)

### Fixed
* Gradle 3.4.1 is required.
* Upgrade App Engine Plugins Core dependency to 0.3.9 ([#179](../../pulls/179))

## 1.3.3

### Added
* Log Dev App Server output to file ([#156](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/156))

### Changed
* Gradle 4.0 is required.
* Preserve datastore-indexes-auto.xml across non-clean rebuilds ([#165](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/165))
* Use sync instead of copy on the explodeWar task ([#162](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/162))

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
