# User Guide 2.+

| NOTE             |
| :---------------- |
| The behavior of the appengine-gradle-plugin has changed since v1.+; please see the [CHANGELOG](CHANGELOG.md) for a full list of changes. If you are having trouble using or updating your plugin, please file a [new issue](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues).|

## Applying the Plugin
Include the plugin jar in your buildscript classpath and apply the appropriate Standard or Flexible plugin:

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath "com.google.cloud.tools:appengine-gradle-plugin:<version>"
  }
}

apply plugin: "com.google.cloud.tools.appengine-standard"
// or
apply plugin: "com.google.cloud.tools.appengine-flexible"
```

You can also use the `com.google.cloud.tools.appengine` plugin that will automatically determine
your environment based on the presence of an `appengine-web.xml`
in `src/main/webapp/WEB-INF/`, _Standard_ if present, _Flexible_ otherwise.

The [Cloud SDK](https://cloud.google.com/sdk) is required for this plugin to
function. Download and install it before running any tasks.

---

## App Engine Standard

### Tasks
For App Engine standard, the plugin exposes the following tasks :

#### Local Run

| Task             | Description |
| ---------------- | ----------- |
| `appengineRun`   | Run the application locally. |
| `appengineStart` | Start the application in the background. |
| `appengineStop`  | Stop a running application. |

#### Deployment

| Task                      | Description |
| ------------------------- | ----------- |
| `appengineStage`          | Stage an application for deployment. |
| `appengineDeploy`         | Deploy an application. |
| `appengineDeployCron`     | Deploy cron configuration. |
| `appengineDeployDispatch` | Deploy dispatch configuration. |
| `appengineDeployDos`      | Deploy dos configuration. |
| `appengineDeployIndex`    | Deploy datastore index configuration. |
| `appengineDeployQueue`    | Deploy queue configuration. |

#### Other

| Task                         | Description |
| ---------------------------- | ----------- |
| `appengineCloudSdkLogin`     | Launch the Cloud SDK login webflow and set the global Cloud SDK auth state. |
| `appengineShowConfiguration` | Print out the appengine standard gradle plugin configuration. |

### Configuration
Once you've [initialized](https://cloud.google.com/sdk/docs/initializing) `gcloud` you can run and deploy
your application using the defaults provided by the plugin. To view the default configuration values, run :

```
$ ./gradlew appengineShowConfiguration
```

If you wish to customize the plugin further, the standard plugin can be configured using the `appengine`
configuration closure.

```groovy
appengine {
  tools {
    // configure the Cloud Sdk tooling
  }
  run {
    // configure local run
  }
  stage {
    // configure staging for deployment
  }
  deploy {
    // configure deployment
  }
}
```

##### Tools
The `tools` configuration has the following parameters :

| Parameter               | Description |
| ----------------------- | ----------- |
| `serviceAccountKeyFile` | A Google project service account key file to run Cloud SDK operations requiring an authenticated user. |
| `cloudSdkHome`          | Location of the Cloud SDK. |
| `cloudSdkVersion`       | The desired version of the Cloud SDK (e.g. "192.0.0"). |

The Cloud SDK will be installed/updated/verified depending on which parameters are configured:

| Parameters Specified   | Action |
| ---------------------- | ------ |
| None                   | Latest version of Cloud SDK is downloaded and installed. |
| Both parameters        | Cloud SDK installation specified at `cloudSdkHome` is verified. |
| `cloudSdkHome` only    | No verification. |
| `cloudSdkVersion` only | Cloud SDK at specified version is downloaded and installed. |

The Cloud SDK is installed in `$USER_HOME/.cache/google-cloud-tools-java/managed-cloud-sdk/<version>/google-cloud-sdk`
on Linux, `$USER_HOME/Library/Application Support/google-cloud-tools-java/managed-cloud-sdk/<version>/google-cloud-sdk`
on OSX, and `%LOCALAPPDATA%/google/ct4j-cloud-sdk/<version>/google-cloud-sdk` on Windows.
The Cloud SDK installation/verification occurs automatically before running any appengine tasks, but
it can also be called explicitly by running the tasks `downloadCloudSdk` and `checkCloudSdk`.

##### Run
The `run` configuration has the following parameters :
Note that only a subset are valid for Dev App Server version "1" and all are valid for Dev App Server version "2-alpha".

Valid for versions "1" and "2-alpha"

| Parameter             | Description |
| --------------------- | ----------- |
| `environment`         | Environment variables to pass to the Dev App Server process |
| `host`                | Application host address. |
| `jvmFlags`            | JVM flags to pass to the App Server Java process. |
| `port`                | Application host port. |
| `startSuccessTimeout` | Amount of time in seconds to wait for the Dev App Server to start in the background. |
| `serverVersion`       | Server versions to use, options are "1" or "2-alpha" |
| `services`            | List of services to run |
| `additionalArguments` | Additional arguments to pass to the Dev App Server process |

Only valid for version "2-alpha"

| Parameter (2-alpha only) |
| ------------------------ |
| `adminHost`              |
| `adminPort`              |
| `allowSkippedFiles`      |
| `apiPort`                |
| `authDomain`             |
| `automaticRestart`       |
| `clearDatastore`         |
| `customEntrypoint`       |
| `datastorePath`          |
| `defaultGcsBucketName`   |
| `devAppserverLogLevel`   |
| `logLevel`               |
| `maxModuleInstances`     |
| `pythonStartupArgs`      |
| `pythonStartupScript`    |
| `runtime`                |
| `skipSdkUpdateCheck`     |
| `storagePath`            |
| `threadsafeOverride`     |
| `useMtimeFileWatcher`    |

##### Stage
The `stage` configuration has the following parameters :

| Parameter               | Description |
| ----------------------- | ----------- |
| `compileEncoding`       | The character encoding to use when compiling JSPs. |
| `deleteJsps`            | Delete the JSP source files after compilation. |
| `disableJarJsps`        | Disable adding the classes generated from JSPs. |
| `disableUpdateCheck`    | Disable checking for App Engine SDK updates. |
| `enableJarClasses`      | Jar the WEB-INF/classes content. |
| `enableJarSplitting`    | Split JAR files larger than 10 MB into smaller fragments. |
| `enableQuickstart`      | Use Jetty quickstart to process servlet annotations. |
| `jarSplittingExcludes`  | Exclude files that match the list of comma separated SUFFIXES from all JAR files. |
| `sourceDirectory`       | The location of the compiled web application files, or the exploded WAR. This is used as the source for staging. |
| `stagingDirectory`      | The directory to which to stage the application. |

##### Deploy
The `deploy` configuration has the following parameters :
Deploy has some Flexible environment only parameters that are not listed here and will just be ignored.

| Parameter             | Description |
| --------------------- | ----------- |
| `appEngineDirectory`  | Location of configuration files (cron.yaml, dos.yaml, etc) for configuration specific deployments. |
| `bucket`              | The Google Cloud Storage bucket used to stage files associated with the deployment. |
| `project`             | The Google Cloud Project target for this deployment. This can also be set to `GCLOUD_CONFIG` or `APPENGINE_CONFIG`.\* |
| `promote`             | Promote the deployed version to receive all traffic. |
| `server`              | The App Engine server to connect to. Typically, you do not need to change this value. |
| `stopPreviousVersion` | Stop the previously running version of this service after deploying a new one that receives all traffic. |
| `version`             | The version of the app that will be created or replaced by this deployment. This also can be set to `GCLOUD_CONFIG` or `APPENGINE_CONFIG`.\* |

\* setting a property to `GCLOUD_CONFIG` will deploy using the gcloud settings for the property.
\* setting a property to `APPENGINE_CONFIG` will deploy using the value read from `appengine-web.xml`.

---

### How do I deploy my project Configuration Files?

You can now deploy index.yaml/dos.yaml/etc for both flexible and standard environments.

Use the following tasks :
* `appengineDeployCron`
* `appengineDeployDispatch`
* `appengineDeployDos`
* `appengineDeployIndex`
* `appengineDeployQueue`

The deployment source directory can be overridden by setting the `appEngineDirectory` parameter
in the deploy configuration.

For standard it defaults to `${buildDir}/staged-app/WEB-INF/appengine-generated`, you should probably
not change this configuration, for standard configured projects, this is the location that your
xml configs are converted into yaml for deployment.

```groovy
appengine {
  deploy {
    appEngineDirectory = "my/custom/appengine/project/configuration/directory"
  }
}
```

### How do I debug Dev Appserver v1?

You can debug the Dev App Server v1 using the jvmFlags :

```groovy
appengine {
  run {
    jvmFlags = ["-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"]
  }
}
```

### How do I enable hot reload of my application?

To enable hot reload of classes:
1. You must tell the Dev App Server v1 to scan for changes :
    ```groovy
    appengine {
      run {
        automaticRestart = true
      }
    }
    ```
2. While your app is running, just run `explodeWar` to copy the changes into the exploded app directly and reflect your changes into the running application.

If you wish to try gradle's experimental `--continuous` for automatic change application, see [#174](https://github.com/GoogleCloudPlatform/app-gradle-plugin/issues/174).

### How do I put datastore somewhere else (so it's not deleted across rebuilds)?
```groovy
appengine {
  run {
    jvmFlags = ["-Ddatastore.backing_store=/path/to/my/local_db.bin"]
  }
}
```

### How do I run multiple modules on the Dev App Server v1?

Multimodule support can be done by adding all the runnable modules to a single runner's configuration (which currently
must be an appengine-standard application), and using a helper method to tie everything together.
```groovy
appengine {
  run {
    // configure the app to point to the right service directories
    services = [
        projectAsService(project),
        projectAsService(":another-module")
    ]
  }
}
```

### I want to use Dev Appserver 2 (alpha), how do I switch to it?

To switch back to the Dev App Server v2-alpha (that was default in version < 1.3.0) use the `serverVersion` parameter

```
appengine {
  run {
    serverVersion = "2-alpha"
  }
}
```

---

## App Engine Flexible

### Tasks
For App Engine flexible, the plugin exposes the following tasks :

#### Deployment

| Task                      | Description |
| ------------------------- | ----------- |
| `appengineStage`          | Stage an application for deployment. |
| `appengineDeploy`         | Deploy an application. |
| `appengineDeployCron`     | Deploy cron configuration. |
| `appengineDeployDispatch` | Deploy dispatch configuration. |
| `appengineDeployDos`      | Deploy dos configuration. |
| `appengineDeployIndex`    | Deploy datastore index configuration. |
| `appengineDeployQueue`    | Deploy queue configuration. |

#### Other

| Task                         | Description |
| ---------------------------- | ----------- |
| `appengineShowConfiguration` | Print out the appengine flexible gradle plugin configuration |

### Configuration
Once you've [initialized](https://cloud.google.com/sdk/docs/initializing) `gcloud` you can deploy
your application using the defaults provided by the plugin. To view the default configuration values, run :

```
$ ./gradlew appengineShowConfiguration
```

If you wish to customize the plugin further, the standard plugin can be configured using the `appengine`
configuration closure.

```groovy
appengine {
  tools {
    // configure the Cloud Sdk tooling
  }
  stage {
    // configure staging for deployment
  }
  deploy {
    // configure deployment
  }
}
```

##### Tools
The `tools` configuration has the following parameters :

| Parameter         | Description |
| ----------------- | ----------- |
| `cloudSdkHome`    | Location of the Cloud SDK. |
| `cloudSdkVersion` | The desired version of the Cloud SDK (e.g. "192.0.0"). |

The Cloud SDK will be installed/updated/verified depending on which parameters are configured:

| Parameters Specified   | Action |
| ---------------------- | ------ |
| None                   | Latest version of Cloud SDK is downloaded and installed. |
| Both parameters        | Cloud SDK installation specified at `cloudSdkHome` is verified. |
| `cloudSdkHome` only    | No verification. |
| `cloudSdkVersion` only | Cloud SDK at specified version is downloaded and installed. |

The Cloud SDK is installed in `$USER_HOME/.cache/google-cloud-tools-java/managed-cloud-sdk/<version>/google-cloud-sdk`
on Linux, `$USER_HOME/Library/Application Support/google-cloud-tools-java/managed-cloud-sdk/<version>/google-cloud-sdk`
on OSX, and `%LOCALAPPDATA%/google-cloud-tools-java/managed-cloud-sdk/<version>/google-cloud-sdk` on Windows.
The Cloud SDK installation/verification occurs automatically before running any appengine tasks, but
it can also be called explicitly by running the tasks `downloadCloudSdk` and `checkCloudSdk`.

##### Stage
The `stage` configuration has the following parameters :

| Parameter            | Description |
| -------------------- | ----------- |
| `appEngineDirectory` | The directory that contains app.yaml. |
| `dockerDirectory`    | The directory that contains Dockerfile and other docker context. |
| `artifact`           | The artifact to deploy (a file, like a .jar or a .war). |
| `stagingDirectory`   | The directory to which to stage the application |

##### Deploy
The `deploy` configuration has the following parameters :

| Parameter             | Description |
| --------------------- | ----------- |
| `appEngineDirectory`  | Location of configuration files (cron.yaml, dos.yaml, etc) for configuration specific deployments. |
| `bucket`              | The Google Cloud Storage bucket used to stage files associated with the deployment. |
| `imageUrl`            | Deploy with a Docker URL from the Google container registry. |
| `project`             | The Google Cloud Project target for this deployment. This can also be set to `GCLOUD_CONFIG`.\* |
| `promote`             | Promote the deployed version to receive all traffic. |
| `server`              | The App Engine server to connect to. Typically, you do not need to change this value. |
| `stopPreviousVersion` | Stop the previously running version of this service after deploying a new one that receives all traffic. |
| `version`             | The version of the app that will be created or replaced by this deployment. This also can be set to `GCLOUD_CONFIG` |

\* setting a property to `GCLOUD_CONFIG` will deploy using the gcloud settings for the property.
