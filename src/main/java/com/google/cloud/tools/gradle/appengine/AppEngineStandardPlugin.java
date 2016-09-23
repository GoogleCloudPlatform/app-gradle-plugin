/*
 * Copyright (c) 2016 Google Inc. All Right Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.model.AppEngineStandardExtension;
import com.google.cloud.tools.gradle.appengine.task.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerRunTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerStartTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerStopTask;
import com.google.cloud.tools.gradle.appengine.task.ExplodeWarTask;
import com.google.cloud.tools.gradle.appengine.task.StageStandardTask;
import com.google.cloud.tools.gradle.appengine.util.AppEngineWebXml;
import com.google.common.collect.Lists;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.bundling.War;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Plugin definition for App Engine standard environments
 */
public class AppEngineStandardPlugin implements Plugin<Project> {

  private static final String APP_ENGINE_STANDARD_TASK_GROUP = "App Engine standard environment";

  private static final String EXPLODE_WAR_TASK_NAME = "explodeWar";
  private static final String STAGE_TASK_NAME = "appengineStage";
  private static final String RUN_TASK_NAME = "appengineRun";
  private static final String START_TASK_NAME = "appengineStart";
  private static final String STOP_TASK_NAME = "appengineStop";
  private static final String DEPLOY_TASK_NAME = "appengineDeploy";

  private static final String EXPLODED_APP_DIR_NAME = "exploded-app";
  private static final String STAGED_APP_DIR_NAME = "staged-app";


  private Project project;
  private AppEngineStandardExtension extension;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Override
  public void apply(Project project) {

    this.project = project;
    createPluginExtension();

    createExplodedWarTask();
    createStageTask();
    createRunTasks();
    createDeployTask();
  }

  private void createPluginExtension() {
    extension = project.getExtensions().create("appengine", AppEngineStandardExtension.class);

    extension.getStage().setSourceDirectory(new File(project.getBuildDir(), EXPLODED_APP_DIR_NAME));
    extension.getStage().setStagingDirectory(new File(project.getBuildDir(), STAGED_APP_DIR_NAME));
    extension.getRun().setAppYamls(
        Collections.singletonList(new File(project.getBuildDir(), EXPLODED_APP_DIR_NAME)));
    extension.getDeploy().setDeployables(Collections
        .singletonList(new File(extension.getStage().getStagingDirectory(), "app.yaml")));

    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        // special handing for java8
        if (extension.getStage().getRuntime() == null) {
          WarPluginConvention war = project.getConvention().getPlugin(WarPluginConvention.class);
          File appengineWebXml = new File(war.getWebAppDir(), "WEB-INF/appengine-web.xml");
          JavaVersion javaVersion = project.getConvention().getPlugin(JavaPluginConvention.class)
              .getTargetCompatibility();
          if (javaVersion.compareTo(JavaVersion.VERSION_1_8) >= 0 &&
              AppEngineWebXml.parse(appengineWebXml).isVm()) {
            extension.getStage().setRuntime("java");
          }
        }

        // create the sdk builder factory after we know the location of the sdk
        cloudSdkBuilderFactory = new CloudSdkBuilderFactory(extension.getTools().getCloudSdkHome());

        // add special flag for devappserver on java8
        if (JavaVersion.current().compareTo(JavaVersion.VERSION_1_8) >= 0) {
          List<String> jvmFlags = extension.getRun().getJvmFlags();
          jvmFlags = (jvmFlags == null) ? Lists.<String>newArrayList() : jvmFlags;
          jvmFlags.add("-Dappengine.user.timezone=UTC");
          extension.getRun().setJvmFlags(jvmFlags);
        }
      }
    });
  }

  private void createExplodedWarTask() {
    project.getTasks()
        .create(EXPLODE_WAR_TASK_NAME, ExplodeWarTask.class, new Action<ExplodeWarTask>() {
          @Override
          public void execute(final ExplodeWarTask explodeWar) {
            explodeWar
                .setExplodedAppDirectory(new File(project.getBuildDir(), EXPLODED_APP_DIR_NAME));
            explodeWar.dependsOn(WarPlugin.WAR_TASK_NAME);
            explodeWar.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
            explodeWar.setDescription("Explode a war into a directory");

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                explodeWar.setWarFile(
                    ((War) project.getTasks().getByPath(WarPlugin.WAR_TASK_NAME)).getArchivePath());
              }
            });
          }
        });
    project.getTasks().getByName(BasePlugin.ASSEMBLE_TASK_NAME).dependsOn(EXPLODE_WAR_TASK_NAME);
  }

  private void createStageTask() {

    project.getTasks()
        .create(STAGE_TASK_NAME, StageStandardTask.class, new Action<StageStandardTask>() {
          @Override
          public void execute(final StageStandardTask stageTask) {
            stageTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
            stageTask.setDescription(
                "Stage an App Engine standard environment application for deployment");
            stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                stageTask.setStagingConfig(extension.getStage());
                stageTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
              }
            });
          }
        });
  }

  private void createRunTasks() {
    project.getTasks()
        .create(RUN_TASK_NAME, DevAppServerRunTask.class, new Action<DevAppServerRunTask>() {
          @Override
          public void execute(final DevAppServerRunTask runTask) {
            runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
            runTask.setDescription("Run an App Engine standard environment application locally");
            runTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                runTask.setRunConfig(extension.getRun());
                runTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
              }
            });
          }
        });

    project.getTasks()
        .create(START_TASK_NAME, DevAppServerStartTask.class, new Action<DevAppServerStartTask>() {
          @Override
          public void execute(final DevAppServerStartTask startTask) {
            startTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
            startTask.setDescription(
                "Run an App Engine standard environment application locally in the background");
            startTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                startTask.setRunConfig(extension.getRun());
                startTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
              }
            });
          }
        });

    project.getTasks()
        .create(STOP_TASK_NAME, DevAppServerStopTask.class, new Action<DevAppServerStopTask>() {
          @Override
          public void execute(final DevAppServerStopTask stopTask) {
            stopTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
            stopTask.setDescription(
                "Stop a locally running App Engine standard environment application");

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                stopTask.setRunConfig(extension.getRun());
              }
            });
          }
        });
  }

  private void createDeployTask() {
    project.getTasks().create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
      @Override
      public void execute(final DeployTask deployTask) {
        deployTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
        deployTask.setDescription("Deploy an App Engine standard environment application");
        deployTask.dependsOn(STAGE_TASK_NAME);

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(extension.getDeploy());
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }
}
