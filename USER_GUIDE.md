# User Guide (1.3.0-rc)
Primitive guide for users 1.3.0 and above (need to expand)

# Yaml configuration deployment

You can now deploy index.yaml/dos.yaml/etc without configuring deployables for
both flexible and standard environments.

Use the following tasks
* `appengineDeployCron`
* `appengineDeployDispatch`
* `appengineDeployDos`
* `appengineDeployIndex`
* `appengineDeployQueue`

The deployment source directory can be overridden by setting the `appEngineDirectory` parameter
in the deploy configuration.

```groovy
appengine {
  deploy {
    appEngineDirectory = "my/custom/appengine/project/configuration/directory"
  }
}
```
* For standard it defaults to `${buildDir}/staged-app/WEB-INF/appengine-generated`
* For flexible it defaults to `src/main/appengine`


# Dev App Server v1

Dev App Server v1 is the default configured local run server from version 1.3.0 onwards.

## Parameters 

Dev App Server v1 parameters continue to be set in the `appengine.run` configuration closure,
It uses a subset of Dev App Server 2 parameters that have been available as part of the
run configuration.

* ~~`appYamls`~~ - deprecated in favor of `services`.
* `services` - a list of services to run [default is the current module].
* `host` - host address to run on [default is localhost].
* `port` - port to run on [default is 8080].
* `jvmFlags` - jvm flags to send the to the process that started the dev server.

Any other configuration parameter is Dev App Server v2 ONLY, and will print a warning and
be ignored.

## Debugger

You can debug the Dev App Server v1 using the jvmFlags
```groovy
appengine {
  run {
    jvmFlags = ["-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"]
  }
}
```

## Putting the Datastore somewhere else (so it's not deleted across rebuilds)
```groovy
appengine {
  run {
    jvmFlags = ["-Ddatastore.backing_store=/path/to/my/local_db.bin"]
  }
}
```

## Running Multiple Modules

Multimodule support can be done by adding all the runnable modules to a single runner's configuration (which currently
must be an appengine-standard application), and using a helper method to tie everything together.
```groovy
appengine {
  run {
    // configure the app to point to the right service directories
    services = [
        getExplodedAppDir(project), 
        getExplodedAppDir(project(":another-module"))
    ]
  }
}

// helper method to obtain correct directory and set up dependencies
def getExplodedAppDir(Project p) {
  // if not 'this' module, then do some setup.
  if (p != project) {
    // make sure we evaluate other modules first so we get the right value
    evaluationDependsOn(p.path)
    // make sure we build "run" depends on the other modules exploding wars
    project.tasks.appengineRun.dependsOn p.tasks.explodeWar
  }
  return p.tasks.explodeWar.explodedAppDirectory
}
```

## Switch to Dev App Server v2-alpha

To switch back to the Dev App Server v2-alpha (that was default in version < 1.3.0) use the `serverVersion` parameter

```
appengine {
  run {
    serverVersion = "2-alpha"
  }
}
```
