/*
 * Copyright 2016 Google LLC. All Rights Reserved.
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

package com.google.cloud.tools.gradle.appengine.appyaml;

import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkOperations;
import com.google.cloud.tools.gradle.appengine.core.DeployAllTask;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.core.DeployTask;
import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import java.io.File;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;

/** Plugin definition for App Engine app.yaml based projects. */
public class AppEngineAppYamlPlugin implements Plugin<Project> {

  public static final String APP_ENGINE_APP_YAML_TASK_GROUP = "App Engine app.yaml based projects";
  private static final String STAGE_TASK_NAME = "appengineStage";

  private static final String STAGED_APP_DIR_NAME = "staged-app";

  private Project project;
  private AppEngineAppYamlExtension appengineExtension;
  private StageAppYamlExtension stageExtension;

  @Override
  public void apply(Project project) {
    this.project = project;
    appengineExtension =
        project.getExtensions().create("appengine", AppEngineAppYamlExtension.class);
    appengineExtension.createSubExtensions(project);

    new AppEngineCorePluginConfiguration()
        .configureCoreProperties(
            project, appengineExtension, APP_ENGINE_APP_YAML_TASK_GROUP, false);

    configureExtensions();
    createStageTask();
  }

  private void configureExtensions() {

    // create the app.yaml staging extension and set defaults.
    stageExtension = appengineExtension.getStage();
    File defaultStagedAppDir = new File(project.getBuildDir(), STAGED_APP_DIR_NAME);
    stageExtension.setStagingDirectory(defaultStagedAppDir);
    stageExtension.setAppEngineDirectory(new File(project.getProjectDir(), "src/main/appengine"));
    File dockerOptionalDir = new File(project.getProjectDir(), "src/main/docker");
    if (dockerOptionalDir.exists()) {
      // only set the docker directory if we find it.
      stageExtension.setDockerDirectory(dockerOptionalDir);
    }

    // tools extension required to initialize cloudSdkOperations
    final ToolsExtension tools = appengineExtension.getTools();
    project.afterEvaluate(
        project -> {
          // create the sdk builder factory after we know the location of the sdk
          try {
            new CloudSdkOperations(tools.getCloudSdkHome(), null, tools.getVerbosity());
          } catch (CloudSdkNotFoundException ex) {
            // this should be caught in AppEngineCorePluginConfig before it can ever reach here.
            throw new GradleException("Could not find CloudSDK: ", ex);
          }

          // we can only set the default location of "archive" after project evaluation (callback)
          if (stageExtension.getArtifact() == null) {
            if (project.getPlugins().hasPlugin(WarPlugin.class)) {
              War war = (War) project.getProperties().get("war");
              stageExtension.setArtifact(war.getArchivePath());
            } else if (project.getPlugins().hasPlugin(JavaPlugin.class)) {
              Jar jar = (Jar) project.getProperties().get("jar");
              stageExtension.setArtifact(jar.getArchivePath());
            } else {
              throw new GradleException("Could not find JAR or WAR configuration");
            }
          }

          // obtain deploy extension set defaults
          DeployExtension deploy = appengineExtension.getDeploy();

          // grab default project configuration from staging default
          if (deploy.getAppEngineDirectory() == null) {
            deploy.setAppEngineDirectory(stageExtension.getAppEngineDirectory());
          }

          DeployAllTask deployAllTask =
              (DeployAllTask)
                  project
                      .getTasks()
                      .getByName(AppEngineCorePluginConfiguration.DEPLOY_ALL_TASK_NAME);
          deployAllTask.setStageDirectory(stageExtension.getStagingDirectory());
          deployAllTask.setDeployExtension(deploy);

          DeployTask deployTask =
              (DeployTask)
                  project.getTasks().getByName(AppEngineCorePluginConfiguration.DEPLOY_TASK_NAME);
          deployTask.setDeployConfig(deploy);
          deployTask.setAppYaml(stageExtension.getStagingDirectory().toPath().resolve("app.yaml"));
        });
  }

  private void createStageTask() {
    StageAppYamlTask stageTask =
        project
            .getTasks()
            .create(
                STAGE_TASK_NAME,
                StageAppYamlTask.class,
                stageTask1 -> {
                  stageTask1.setGroup(APP_ENGINE_APP_YAML_TASK_GROUP);
                  stageTask1.setDescription(
                      "Stage an App Engine app.yaml based project for deployment");
                  stageTask1.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                  project.afterEvaluate(project -> stageTask1.setStagingConfig(stageExtension));
                });
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_ALL_TASK_NAME)
        .dependsOn(stageTask);
  }
}
