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

import com.google.cloud.tools.gradle.appengine.model.AppEngineFlexibleExtension;
import com.google.cloud.tools.gradle.appengine.task.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.task.StageFlexibleTask;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;

import java.io.File;
import java.util.Collections;

/**
 * Plugin definition for App Engine flexible environments
 */
public class AppEngineFlexiblePlugin implements Plugin<Project> {

  private static final String STAGE_TASK_NAME = "appengineStage";
  private static final String DEPLOY_TASK_NAME = "appengineDeploy";
  private static final String APP_ENGINE_FLEXIBLE_TASK_GROUP = "App Engine flexible environment";
  private static final String STAGED_APP_DIR_NAME = "staged-app";

  private Project project;
  private AppEngineFlexibleExtension extension;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Override
  public void apply(Project project) {

    this.project = project;
    createPluginExtension();

    createStageTask();
    createDeployTask();
  }

  private void createPluginExtension() {
    extension = project.getExtensions().create("appengine", AppEngineFlexibleExtension.class);

    extension.getStage().setStagingDirectory(new File(project.getBuildDir(), STAGED_APP_DIR_NAME));
    extension.getDeploy().setDeployables(Collections
        .singletonList(new File(extension.getStage().getStagingDirectory(), "app.yaml")));
    extension.getStage()
        .setAppEngineDirectory(new File(project.getProjectDir(), "src/main/appengine"));
    File dockerDirectory = new File(project.getProjectDir(), "src/main/docker");
    if (dockerDirectory.exists()) {
      extension.getStage().setDockerDirectory(dockerDirectory);
    }

    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        // we can only set the default location of "archive" after project evaluation (callback)
        if (extension.getStage().getArtifact() == null) {
          if (project.getPlugins().hasPlugin(WarPlugin.class)) {
            War war = (War) project.getProperties().get("war");
            extension.getStage().setArtifact(war.getArchivePath());
          } else if (project.getPlugins().hasPlugin(JavaPlugin.class)) {
            Jar jar = (Jar) project.getProperties().get("jar");
            extension.getStage().setArtifact(jar.getArchivePath());
          } else {
            throw new GradleException("Could not find JAR or WAR configuration");
          }

          // also create the sdk builder factory after we know the location of the sdk
          cloudSdkBuilderFactory = new CloudSdkBuilderFactory(
              extension.getTools().getCloudSdkHome());
        }
      }
    });
  }

  private void createStageTask() {
    project.getTasks()
        .create(STAGE_TASK_NAME, StageFlexibleTask.class, new Action<StageFlexibleTask>() {
          @Override
          public void execute(final StageFlexibleTask stageTask) {
            stageTask.setGroup(APP_ENGINE_FLEXIBLE_TASK_GROUP);
            stageTask.setDescription(
                "Stage an App Engine flexible environment application for deployment");
            stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                stageTask.setStagingConfig(extension.getStage());
              }
            });
          }
        });
  }

  private void createDeployTask() {
    project.getTasks().create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
      @Override
      public void execute(final DeployTask deployTask) {
        deployTask.setGroup(APP_ENGINE_FLEXIBLE_TASK_GROUP);
        deployTask.setDescription("Deploy an App Engine flexible environment application");
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
