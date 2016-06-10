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

import com.google.cloud.tools.gradle.appengine.model.hidden.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.model.AppEngineStandardModel;
import com.google.cloud.tools.gradle.appengine.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerRunTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerStartTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerStopTask;
import com.google.cloud.tools.gradle.appengine.task.ExplodeWarTask;
import com.google.cloud.tools.gradle.appengine.task.StageStandardTask;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.War;
import org.gradle.model.Defaults;
import org.gradle.model.Finalize;
import org.gradle.model.Model;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.Path;
import org.gradle.model.RuleSource;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Plugin definition for App Engine standard environments
 */
public class AppEngineStandardPlugin implements Plugin<Project> {

  private static final String APP_ENGINE_STANDARD_TASK_GROUP = "App Engine standard environment";

  private static final String EXPLODE_WAR_TASK_NAME = "explodeWar";
  private static final String STAGE_TASK_NAME = "gcpAppStage";
  private static final String RUN_TASK_NAME = "gcpAppRun";
  private static final String START_TASK_NAME = "gcpAppStart";
  private static final String STOP_TASK_NAME = "gcpAppStop";
  private static final String DEPLOY_TASK_NAME = "gcpAppDeploy";

  private static final String EXPLODED_APP_DIR_NAME = "exploded-app";
  private static final String STAGED_APP_DIR_NAME = "staged-app";


  public void apply(Project project) {
    project.getPluginManager().apply(WarPlugin.class);

    // We can't configure war tasks in model space because the War plugin is not a RuleSource plugin
    // so do it here for now till Gradle updates all the core plugins.
    createExplodedWarTask(project);
  }

  private void createExplodedWarTask(final Project project) {
    final War warTask = (War) project.getTasks().getByName(WarPlugin.WAR_TASK_NAME);
    project.getTasks()
        .create(EXPLODE_WAR_TASK_NAME, ExplodeWarTask.class, new Action<ExplodeWarTask>() {
          @Override
          public void execute(final ExplodeWarTask explodeWarTask) {
            explodeWarTask
                .setExplodedAppDirectory(new File(project.getBuildDir(), EXPLODED_APP_DIR_NAME));
            explodeWarTask.dependsOn(warTask);
            project.getTasks().getByName(BasePlugin.ASSEMBLE_TASK_NAME).dependsOn(explodeWarTask);
            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                explodeWarTask.setWarFile(warTask.getArchivePath());
              }
            });
          }
        });
  }

  /**
   * RuleSource configuration for the plugin
   */
  public static class PluginRules extends RuleSource {

    @Model
    public void gcpApp(AppEngineStandardModel app) {
    }

    @Defaults
    public void setDefaults(AppEngineStandardModel app, @Path("buildDir") File buildDir) {
      app.getStage().setSourceDirectory(new File(buildDir, EXPLODED_APP_DIR_NAME));
      app.getStage().setStagingDirectory(new File(buildDir, STAGED_APP_DIR_NAME));

      List<File> deployables = Collections
          .singletonList(new File(app.getStage().getStagingDirectory(), "app.yaml"));
      app.getRun().setAppYamls(deployables);
      app.getDeploy().setDeployables(deployables);
    }

    @Mutate
    public void createCloudSdkBuilderFactory(final AppEngineStandardModel app) {
      app.getTools().setCloudSdkBuilderFactory(
          new CloudSdkBuilderFactory(app.getTools().getCloudSdkHome()));
    }

    @Mutate
    public void createStageTask(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(STAGE_TASK_NAME, StageStandardTask.class, new Action<StageStandardTask>() {
        @Override
        public void execute(StageStandardTask stageTask) {
          stageTask.setStagingConfig(app.getStage());
          stageTask.setCloudSdkBuilderFactory(app.getTools().getCloudSdkBuilderFactory());
          stageTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        }
      });
    }

    @Finalize
    public void createRunTasks(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(RUN_TASK_NAME, DevAppServerRunTask.class, new Action<DevAppServerRunTask>() {
        @Override
        public void execute(DevAppServerRunTask runTask) {
          runTask.setRunConfig(app.getRun());
          runTask.setCloudSdkBuilderFactory(app.getTools().getCloudSdkBuilderFactory());
          runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          runTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        }
      });

      tasks.create(START_TASK_NAME, DevAppServerStartTask.class, new Action<DevAppServerStartTask>() {
        @Override
        public void execute(DevAppServerStartTask startTask) {
          startTask.setRunConfig(app.getRun());
          startTask.setCloudSdkBuilderFactory(app.getTools().getCloudSdkBuilderFactory());
          startTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          startTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        }
      });

      tasks.create(STOP_TASK_NAME, DevAppServerStopTask.class, new Action<DevAppServerStopTask>() {
        @Override
        public void execute(DevAppServerStopTask stopTask) {
          stopTask.setRunConfig(app.getRun());
          stopTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
        }
      });
    }

    @Finalize
    public void createDeployTask(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
        @Override
        public void execute(DeployTask deployTask) {
          deployTask.setDeployConfig(app.getDeploy());
          deployTask.setCloudSdkBuilderFactory(app.getTools().getCloudSdkBuilderFactory());
          deployTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          deployTask.dependsOn(STAGE_TASK_NAME);
        }
      });
    }
  }
}
