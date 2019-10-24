![project status image](https://img.shields.io/badge/stability-stable-brightgreen.svg)
[![build status image](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin.svg?branch=master)](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/appengine-gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.google.cloud.tools/appengine-gradle-plugin)
# Google App Engine Gradle plugin

This Gradle plugin provides tasks to build and deploy Google App Engine applications.

# Requirements

[Gradle](http://gradle.org) is required to build and run the plugin. Version compatibility is listed below.

| appengine-gradle-plugin | gradle version |
|-------------------------|----------------|
| 2.0.0 +                 | 4.0 or newer   |
| 1.3.3 +                 | 3.4.1 or newer |
| 1.0.0 - 1.3.2           | 3.0 or newer   |

[Google Cloud SDK](https://cloud.google.com/sdk/) is required but will be
automatically installed by the plugin.

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
    classpath 'com.google.cloud.tools:appengine-gradle-plugin:2.2.0'
  }
}
```

You can now run commands like `./gradlew appengineDeploy` in the root folder of your Java application.

## Goals and Configuration

Please see the [USER GUIDE](USER_GUIDE.md) for a full list of supported goals and configuration
options.
* [USER\_GUIDE for `app.yaml` based projects](USER_GUIDE.md#app-engine-appyaml-based-projects)
* [USER\_GUIDE for `appengine-web.xml` based projects](USER_GUIDE.md#app-engine-appengine-webxml-based-projects)

# Reference Documentation

App Engine Standard Environment:
* [Using Gradle and the App Engine Plugin (standard environment)](https://cloud.google.com/appengine/docs/java/tools/gradle)
* [App Engine Gradle Plugin Tasks and Parameters (standard environment)](https://cloud.google.com/appengine/docs/java/tools/gradle-reference)

App Engine Flexible Environment:
* [Using Gradle and the App Engine Plugin (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
* [App Engine Gradle Plugin Tasks and Parameters (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)


# Contributing

If you wish to contribute to this plugin, please see the [contributor instructions](CONTRIBUTING.md).
