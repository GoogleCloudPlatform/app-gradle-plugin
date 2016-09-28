[![experimental](http://badges.github.io/stability-badges/dist/experimental.svg)](http://github.com/badges/stability-badges)
[![build status image](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin.svg?branch=master)](https://travis-ci.org/GoogleCloudPlatform/app-gradle-plugin)
# Google App Engine Gradle plugin (BETA)

This Gradle plugin provides tasks to build and deploy Google App Engine applications.

# Requirements

[Gradle](http://gradle.org) is required to build and run the plugin.

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
    classpath 'com.google.cloud.tools:appengine-gradle-plugin:0.1.1-beta'
  }
}
```

You can now run commands like `./gradlew appengineDeploy` in the root folder of your Java application.

# Supported tasks
- appengineStage
- appengineDeploy

Dev App Server goals for standard environment apps only:
- appengineRun
- appengineStart 
- appengineStop

Task documentation is available by running:

    ./gradlew help --task [task]

## Contributing

If you wish to build this plugin from source, please see the [contributor instructions](CONTRIBUTING.md).
