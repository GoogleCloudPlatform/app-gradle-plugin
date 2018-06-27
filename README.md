![project status image](https://img.shields.io/badge/stability-stable-brightgreen.svg)
[![build status image](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin.svg?branch=master)](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/appengine-gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/appengine-gradle-plugin)
# Google App Engine Gradle plugin

This Gradle plugin provides tasks to build and deploy Google App Engine applications.

| 2.0.0 Release Candidates are live |
| :------------------------------ |
| 2.0.0-rc have been published. The behavior of the appengine-gradle-plugin has changed since v1.+; please see the [CHANGELOG](CHANGELOG.md) for a full list of changes and an updated [USER GUIDE](USER_GUIDE.md) for details. If you are having trouble using or updating your plugin, please file a [new issue](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues).|

# Reference Documentation

App Engine Standard Environment:
* [Using Gradle and the App Engine Plugin (standard environment)](https://cloud.google.com/appengine/docs/java/tools/gradle)
* [App Engine Gradle Plugin Tasks and Parameters (standard environment)](https://cloud.google.com/appengine/docs/java/tools/gradle-reference)

App Engine Flexible Environment:
* [Using Gradle and the App Engine Plugin (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
* [App Engine Gradle Plugin Tasks and Parameters (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)

# Requirements

[Gradle](http://gradle.org) is required to build and run the plugin. Version compatibility is listed below.

| appengine-gradle-plugin | gradle version |
|-------------------------|----------------|
| 1.3.3 +                 | 3.4.1 or newer |
| 1.0.0 - 1.3.2           | 3.0 or newer   |

You must have [Google Cloud SDK](https://cloud.google.com/sdk/) installed.

Cloud SDK app-engine-java component is also required. Install it by running:

    gcloud components install app-engine-java

Login and configure Cloud SDK:

    gcloud init

# How to use

In your Gradle App Engine Java app, add the following plugin to your build.gradle:

```Groovy
apply plugin: 'com.google.cloud.tools.appengine'
```

The plugin JAR needs to be defined in the classpath of your build script. It is directly available on Maven Central. Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an example on how to retrieve it from Maven Central:

```Groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.google.cloud.tools:appengine-gradle-plugin:1.3.3'
  }
}
```

You can now run commands like `./gradlew appengineDeploy` in the root folder of your Java application.

# Supported tasks
| Task                       | Description
|----------------------------|---
| appengineShowConfiguration | Shows the current App Engine plugin configuration properties.
| appengineStage             | Generates an application directory for deployment.
| appengineDeploy            | Stages and deploys an application to App Engine.
| appengineDeployCron        | Deploys cron configuration (cron.yaml).
| appengineDeployDispatch    | Deploys dispatch configuration (dispatch.yaml).
| appengineDeployDos         | Deploys dos configuration (dos.yaml).
| appengineDeployIndex       | Deploys datastore index configuration (index.yaml).
| appengineDeployQueue       | Deploys queue configuration (queue.yaml).

Dev App Server tasks for standard environment apps only:

| Task           | Description
|----------------|---
| appengineRun   | Runs the App Engine local development server.
| appengineStart | Runs the App Engine local development server in the background.
| appengineStop  | Stops a running App Engine web development server.

Task documentation is available by running:

    ./gradlew help --task [task]

## Contributing

If you wish to build this plugin from source, please see the [contributor instructions](CONTRIBUTING.md).
