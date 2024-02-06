🚌 In February 2024, this library has moved to [appengine-plugins/app-gradle-plugin](https://github.com/GoogleCloudPlatform/appengine-plugins/tree/main/app-gradle-plugin). This repository will be archived in the future. Future releases will appear in the new repository (https://github.com/GoogleCloudPlatform/appengine-plugins/releases). The Maven artifact coordinates (com.google.cloud.tools:appengine-gradle-plugin) remain the same.

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

The plugin needs to be defined in your `build.gradle`. It is directly available on Maven Central. Alternatively, you can download it from GitHub and deploy it to your local repository. The following code snippet shows an example on how to retrieve it from Maven Central:

### Using plugins block
Since the `appengine-gradle-plugin` is not published to the gradle plugin portal, you must refrence it from the Central Maven repository.  Add the maven central resolution to your `settings.gradle`
```Groovy
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    // add mavenLocal() if you are using a locally built version of the plugin
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id.startsWith('com.google.cloud.tools.appengine')) {
        useModule("com.google.cloud.tools:appengine-gradle-plugin:${requested.version}")
      }
    }
  }
}
```

Apply the plugin in your plugins block in your `build.gradle`
```Groovy
plugins {
  id 'com.google.cloud.tools.appengine' version '2.4.4'
}
```

### Using buildscript block
If you wish to apply the plugin via the legacy buildscript mechanism, add the following to your build.gradle.
```Groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.google.cloud.tools:appengine-gradle-plugin:2.4.4'
  }
}

apply plugin: 'com.google.cloud.tools.appengine'
```

You can now run commands like `./gradlew appengineDeploy` on your Java application.

## Goals and Configuration

Please see the [USER GUIDE](USER_GUIDE.md) for a full list of supported goals and configuration
options.
* [USER\_GUIDE for `app.yaml` based projects](USER_GUIDE.md#app-engine-appyaml-based-projects)
* [USER\_GUIDE for `appengine-web.xml` based projects](USER_GUIDE.md#app-engine-appengine-webxml-based-projects)

# Reference Documentation

App Engine Standard Environment:
* [(Java 8) Using Gradle and the App Engine Plugin (standard environment)](https://cloud.google.com/appengine/docs/legacy/standard/java/using-gradle)
* [(Java 8) App Engine Gradle Plugin Tasks and Parameters (standard environment)](https://cloud.google.com/appengine/docs/legacy/standard/java/gradle-reference)
* [Using Gradle and the App Engine Plugin (standard environment)](https://cloud.google.com/appengine/docs/standard/java-gen2/using-gradle)
* [App Engine Gradle Plugin Tasks and Parameters (standard environment)](https://cloud.google.com/appengine/docs/standard/java-gen2/gradle-reference)

App Engine Flexible Environment:
* [Using Gradle and the App Engine Plugin (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
* [App Engine Gradle Plugin Tasks and Parameters (flexible environment)](https://cloud.google.com/appengine/docs/flexible/java/gradle-reference)


# Contributing

If you wish to contribute to this plugin, please see the [contributor instructions](CONTRIBUTING.md).
