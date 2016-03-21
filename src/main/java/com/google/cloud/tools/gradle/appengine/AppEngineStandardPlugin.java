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

import com.google.cloud.tools.gradle.appengine.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.model.AppEngineStandardModel;
import com.google.cloud.tools.gradle.appengine.task.ExplodeWarTask;
import com.google.cloud.tools.gradle.appengine.task.DevAppServerRunTask;
import com.google.cloud.tools.gradle.appengine.task.StageStandardTask;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.bundling.War;
import org.gradle.model.Defaults;
import org.gradle.model.Finalize;
import org.gradle.model.Model;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.Path;
import org.gradle.model.RuleSource;
import org.gradle.model.Validate;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Plugin definition for App Engine standard environments
 */
public class AppEngineStandardPlugin implements Plugin<Project> {

  private static final String APP_ENGINE_STANDARD_TASK_GROUP = "App Engine flexible environment";

  private static final String EXPLODE_APP_TASK_NAME = "appExplode";
  private static final String STAGE_TASK_NAME = "appStage";
  private static final String RUN_TASK_NAME = "appRun";
  private static final String DEPLOY_TASK_NAME = "appDeploy";

  private static final String EXPLODED_APP_DIR_NAME = "exploded-app";
  private static final String STAGED_APP_DIR_NAME = "staged-app";


  public void apply(Project project) {
    project.getPluginManager().apply(WarPlugin.class);

    // We can't configure war tasks in model space because the War plugin is not a RuleSource plugin
    // so do it here for now till Gradle updates all the core plugins.
    createExplodedWarTask(project);
  }

  private void createExplodedWarTask(final Project project) {
    WarPluginConvention warConfig = project.getConvention().getPlugin(WarPluginConvention.class);
    final War warTask = (War) project.getTasks().getByName(WarPlugin.WAR_TASK_NAME);
    final Task assembleTask = project.getTasks().getByName(BasePlugin.ASSEMBLE_TASK_NAME);
    project.getTasks()
        .create(EXPLODE_APP_TASK_NAME, ExplodeWarTask.class, new Action<ExplodeWarTask>() {
          @Override
          public void execute(final ExplodeWarTask explodeWarTask) {
            explodeWarTask
                .setExplodedAppDirectory(new File(project.getBuildDir(), EXPLODED_APP_DIR_NAME));
            explodeWarTask.dependsOn(warTask);
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

      List<File> deployables = Collections.singletonList(app.getStage().getStagingDirectory());
      app.getRun().setAppYamls(deployables);
      app.getDeploy().setDeployables(deployables);
    }

    @Mutate
    public void createStageTask(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(STAGE_TASK_NAME, StageStandardTask.class, new Action<StageStandardTask>() {
        @Override
        public void execute(StageStandardTask stageTask) {
          stageTask.setStagingConfig(app.getStage());
          stageTask.setCloudSdkHome(app.getTools().getCloudSdkHome());
          stageTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          stageTask.dependsOn(EXPLODE_APP_TASK_NAME);
        }
      });
    }

    @Finalize
    public void createRunTask(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(RUN_TASK_NAME, DevAppServerRunTask.class, new Action<DevAppServerRunTask>() {
        @Override
        public void execute(DevAppServerRunTask runTask) {
          runTask.setRunConfig(app.getRun());
          runTask.setCloudSdkHome(app.getTools().getCloudSdkHome());
          runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          runTask.dependsOn(STAGE_TASK_NAME);
        }
      });
    }

    @Finalize
    public void createDeployTask(final ModelMap<Task> tasks, final AppEngineStandardModel app) {

      tasks.create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
        @Override
        public void execute(DeployTask deployTask) {
          deployTask.setDeployConfig(app.getDeploy());
          deployTask.setCloudSdkHome(app.getTools().getCloudSdkHome());
          deployTask.dependsOn(STAGE_TASK_NAME);
        }
      });
    }

    @Validate
    public void validate(AppEngineStandardModel app) {
      if (app.getTools().getCloudSdkHome() == null) {
        throw new GradleException("gcpApp.tools.cloudSdkHome home not set");
      }
    }
  }
}
