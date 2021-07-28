# User Guide 2.+

## Applying the Plugin
Apply the plugin following the instructions in [README.md](README.md#how-to-use) and using the appropriate plugin identifier:
1. `"com.google.cloud.tools.appengine-appenginewebxml"` for war projects defined by an `appengine-web.xml`
1. `"com.google.cloud.tools.appengine-appyaml"` for projects defined by an `app.yaml`
1. `"com.google.cloud.tools.appengine"` for automatic environment determination (not recommended)


When you use the `com.google.cloud.tools.appengine` plugin it will automatically determine
your environment based on the presence of an `appengine-web.xml`
in `src/main/webapp/WEB-INF/`. It will enable _`appengine-appenginewebxml`_ if present, _`appengine-appyaml`_ otherwise.
Note: autodetection will not work if you set a custom [`webAppDirName` property](https://docs.gradle.org/current/userguide/war_plugin.html#sec:war_convention_properties) of the `war` plugin.
In this case, manually apply the right environment-specific plugin by specifying the corresponding plugin identifier as above.

The [Cloud SDK](https://cloud.google.com/sdk) is required for this plugin to
function. Versions greater than `2.0.0` will automatically download and install it for you. You may also choose to download and install it before running any tasks.

---

## App Engine `appengine-web.xml` based projects

Follow the [instructions](#applying-the-plugin) to apply the `appengine-appenginewebxml` plugin on the project

### Tasks
The plugin exposes the following tasks :

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
| `appengineShowConfiguration` | Print out the plugin configuration. |

### Configuration
Once you've [initialized](https://cloud.google.com/sdk/docs/initializing) `gcloud` you can run and deploy
your application using the defaults provided by the plugin. To view the default configuration values, run :

```
$ ./gradlew appengineShowConfiguration
```

If you wish to customize the plugin further, the plugin can be configured using the `appengine`
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
| `verbosity`             | The verbosity level for logging when gcloud is run. See [gcloud docs](https://cloud.google.com/sdk/gcloud/reference#--verbosity) for allowed values. |

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

| Parameter             | Description |
| --------------------- | ----------- |
| `environment`         | Environment variables to pass to the Dev App Server process |
| `host`                | Application host address. |
| `jvmFlags`            | JVM flags to pass to the App Server Java process. |
| `port`                | Application host port. |
| `startSuccessTimeout` | Amount of time in seconds to wait for the Dev App Server to start in the background. |
| `services`            | List of services to run |
| `additionalArguments` | Additional arguments to pass to the Dev App Server process |
| `automaticRestart`    | Automatically restart the server when explode-war directory has changed |
| `projectId`           | Set a Google Cloud Project Id on the running development server |

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
Deploy has some extra parameters for app.yaml based projects that are not listed here and will just be ignored.

| Parameter             | Description |
| --------------------- | ----------- |
| `appEngineDirectory`  | Location of configuration files (cron.yaml, dos.yaml, etc) for configuration specific deployments. |
| `bucket`              | The Google Cloud Storage bucket used to stage files associated with the deployment. |
| `gcloudMode`          | The gcloud preview mode (`alpha`, `beta`, etc) to use during deployments. |
| `projectId`           | The Google Cloud Project target for this deployment. This can also be set to `GCLOUD_CONFIG`.\* |
| `promote`             | Promote the deployed version to receive all traffic. |
| `server`              | The App Engine server to connect to. Typically, you do not need to change this value. |
| `stopPreviousVersion` | Stop the previously running version of this service after deploying a new one that receives all traffic. |
| `version`             | The version of the app that will be created or replaced by this deployment. This also can be set to `GCLOUD_CONFIG`.\* |

\* Setting a property to `GCLOUD_CONFIG` will deploy using the gcloud settings for the property.

---

### How do I deploy my project Configuration Files?

You can now deploy index.yaml/dos.yaml/etc for all environments.

Use the following tasks :
* `appengineDeployCron`
* `appengineDeployDispatch`
* `appengineDeployDos`
* `appengineDeployIndex`
* `appengineDeployQueue`

The deployment source directory can be overridden by setting the `appEngineDirectory` parameter
in the deploy configuration.

For appengine-web.xml based projects, it defaults to `${buildDir}/staged-app/WEB-INF/appengine-generated`.
You should not change this configuration; this is the location that your xml configs are converted
into yaml for deployment.

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

### How do I enable automatic reload of my application?

To enable automatic reload of your application:
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
must be an appengine-web.xml based application), and using a helper method to tie everything together.
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

The v2-alpha Dev Appserver is no longer supported from this plugin.

---

## App Engine `app.yaml` based projects

Follow the [instructions](#applying-the-plugin) to apply the `appengine-appyaml` plugin on the project

### Tasks
The plugin exposes the following tasks :

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
| `appengineShowConfiguration` | Print out the plugin configuration |

### Configuration
Once you've [initialized](https://cloud.google.com/sdk/docs/initializing) `gcloud` you can deploy
your application using the defaults provided by the plugin. To view the default configuration values, run :

```
$ ./gradlew appengineShowConfiguration
```

If you wish to customize the plugin further, the plugin can be configured using the `appengine`
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
| `serviceAccountKeyFile` | A Google project service account key file to run Cloud SDK operations requiring an authenticated user. |
| `cloudSdkHome`          | Location of the Cloud SDK. |
| `cloudSdkVersion`       | The desired version of the Cloud SDK (e.g. "192.0.0"). |
| `verbosity`             | The verbosity level for logging when gcloud is run. See [gcloud docs](https://cloud.google.com/sdk/gcloud/reference#--verbosity) for allowed values. |

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

| Parameter               | Description |
| ----------------------- | ----------- |
| `appEngineDirectory`    | The directory that contains app.yaml. |
| `dockerDirectory`       | The directory that contains Dockerfile and other docker context. |
| `artifact`              | The artifact to deploy (a file, like a .jar, a .war or a binary). |
| `stagingDirectory`      | The directory to which to stage the application. |
| `extraFilesDirectories` | The list of directories to copy extra files from. |

##### Deploy
The `deploy` configuration has the following parameters :

| Parameter             | Description |
| --------------------- | ----------- |
| `appEngineDirectory`  | Location of configuration files (cron.yaml, dos.yaml, etc) for configuration specific deployments. |
| `bucket`              | The Google Cloud Storage bucket used to stage files associated with the deployment. |
| `gcloudMode`          | The gcloud preview mode (`alpha`, `beta`, etc) to use during deployments. |
| `imageUrl`            | Deploy with a Docker URL from the Google container registry. |
| `projectId`           | The Google Cloud Project target for this deployment. This can also be set to `GCLOUD_CONFIG`.\* |
| `promote`             | Promote the deployed version to receive all traffic. |
| `server`              | The App Engine server to connect to. Typically, you do not need to change this value. |
| `stopPreviousVersion` | Stop the previously running version of this service after deploying a new one that receives all traffic. |
| `version`             | The version of the app that will be created or replaced by this deployment. This also can be set to `GCLOUD_CONFIG` |

\* setting a property to `GCLOUD_CONFIG` will deploy using the gcloud settings for the property.
