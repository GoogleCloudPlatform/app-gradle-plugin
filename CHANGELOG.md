# Change Log
All notable changes to this project will be documented in this file.

## 2.5.1-SNAPSHOT

## 2.5.0

* Update to appengine-plugins-core 0.10.0 that supports GAE java17 and java21 runtimes.

## 2.4.5
### Changed
* Update to appengine-plugins-core 0.9.9, for automatic Java 17 compatibility when running local devserver [appengine-plugins-core#894](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/894).

## 2.4.4
### Changed
* Update to appengine-plugins-core 0.9.8 which includes [a version upgrade for gson to 2.8.9](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/889) and fix for [`StringIndexOutOfBoundsException`](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/890/files). ([#418](../../pull/418))

## 2.4.3
### Changed
* Update to appengine-plugins-core 0.9.7 for Java 17 compatibility ([#413](../../pull/413))

## 2.4.2
### Changed
* Update to appengine-plugins-core 0.9.5 ([#405](../../pull/405))

## 2.4.1
### Fixed
* Fixed bug when using `plugins` block in `build.gradle` ([#388](../../pull/388))

## 2.4.0
### Added
* `appengine.tools.verbosity` option for defining gcloud log verbosity ([#384](../../pull/384))

## 2.3.0
### Changed
* Update to appengine-plugins-core 0.9.0 ([#377](../../pull/377))
  * Includes support for binary artifacts for app.yaml based deployments ([appengine-plugins-core:#840](https://github.com/GoogleCloudPlatform/appengine-plugins-core/issues/840))

## 2.2.0
### Changed
* Update to appengine-plugins-core 0.8.1 ([#364](../../pull/364))

### Added
* Automatically copy `Class-Path` entries in jar manifest to staging directory ([appengine-plugins-core:#804](https://github.com/GoogleCloudPlatform/appengine-plugins-core/pull/804))

## 2.1.0
### Added
* `gcloudMode` to `appengine.deploy` block ([#356](../../pull/356))

## 2.0.1
### Fixed
* Remove release candidate warning ([#344](../../pull/344))

## 2.0.0
See 2.0.0-rc1 - 2.0.0-rc6 for details
### Key Changes
* Dev App Server
  * `v2-alpha` support removed.
  * `appengineStop` doesn't fail build on failure, just logs error
* Cloud Sdk
  * `cloudSdkHome`, `cloudSdkVersion`, `serviceAccountKeyFile` added to
    `appengine.tools` configuration block.
  * `appengineCloudSdkLogin` to trigger cloud sdk login flow
  * `checkCloudSdk` validates version of installed sdk on system
  * Managed Cloud SDK
    * Auto download google cloud sdk if no `cloudSdkHome` provided
    * Skip download in `--offline` mode
    * Do not download appengine components for app.yaml based deployments
* Auto inject command runners into customer user appengine tasks
* Support for multiple extra files directories: `extraFilesDirectory` -> `extraFilesDirectories`
* Support for app.yaml based standard deployments
* `project` is renamed to `projectId`
* `projectId` and `version` are required parameters. Must be set to a value or to `GCLOUD_CONFIG` to delegate to gcloud.
* `projectId` can be configured for multi-module dev appserver runs
* `deployables` config parameter removed
* `appYamls` config paramter removed

## 2.0.0-rc6
### Changed
* Remove support for `APPENGINE_CONFIG` option in `projectId` and `version` ([#325](../../pull/325))
* Remove support for `v2-alpha` devappserver version ([#336](../../pull/336))

### Fixed
* Actually skip downloading/updating the cloud sdk in offline mode ([#337](../../pull/337))

## 2.0.0-rc5
### Changed
* Update appengine-plugins-core to 0.7.3 ([#322](../../pull/322))
* Resolve `GCLOUD_CONFIG`,`APPENGINE_CONFIG` late ([#321](../../pull/321))
* Fix `UP-TO-DATE` configuration on some tasks ([#320](../../pull/320))
* Only download appengine components of cloud sdk when running compat projects ([#317](../../pull/317))
* `extraFilesDirectory`-> `ExtraFilesDirectories` ([#315](../../pull/315)), ([#316](../../pull/316))

## 2.0.0-rc4
### Changed
* Add deprecated error for `appengine.deploy.project` ([#295](../../pull/295))
* Old flex build path is now used for all app.yaml (standard and flex) based builds ([#310](../../pull/310))
* Update appengine-plugins-core to 0.7.1 ([#308](../../pull/308))

## 2.0.0-rc3
### Changed
* Changed `appengine.deploy.project` -> `appengine.deploy.projectId` ([#286](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/286)).
* New `appengine.run.projectId` to set project for devserver runs. ([#286](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/286)).


## 2.0.0-rc2
### Added
* Auto inject internal command runners into custom app engine tasks ([#279](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/279)).

## 2.0.0-rc1
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
* `appengineStop` no longer fails if the stop request to server fails, but it will log an error. ([#267](https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/267))
* Upgrade App Engine Plugins Core dependency to 0.5.2.
* Remove deprecated `appYamls` parameter.
* `project` and `version` are no longer pulled from the global gcloud state by default. `project` must be configured in build.gradle using the `deploy.project` property, users may use special keywords for project to specify that they would like to read it from appengine-web.xml (`project = "APPENGINE_CONFIG"`) or from gcloud global state (`project = "GCLOUD_CONFIG"`). `version` is also configured the same way.
* Removed `deployables` parameter. To deploy specific configuration files, use the appropriate deploy tasks
(i.e. appengineDeployCron, appengineDeployIndex, etc.) ([#261](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/261)).

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
