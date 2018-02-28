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

package com.google.cloud.tools.gradle.appengine.flexible;

import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import java.io.File;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;

/** Plugin definition for App Engine flexible environments. */
public class AppEngineFlexiblePlugin implements Plugin<Project> {

  public static final String APP_ENGINE_FLEXIBLE_TASK_GROUP = "App Engine Flexible environment";
  private static final String STAGE_TASK_NAME = "appengineStage";

  private static final String STAGED_APP_DIR_NAME = "staged-app";

  private Project project;
  private AppEngineFlexibleExtension appengineExtension;
  private StageFlexibleExtension stageExtension;

  @Override
  public void apply(Project project) {
    this.project = project;
    appengineExtension =
        project.getExtensions().create("appengine", AppEngineFlexibleExtension.class);
    appengineExtension.createSubExtensions(project);

    new AppEngineCorePluginConfiguration()
        .configureCoreProperties(project, appengineExtension, APP_ENGINE_FLEXIBLE_TASK_GROUP);

    configureExtensions();
    createStageTask();
  }

  private void configureExtensions() {

    // create the flexible stage extension and set defaults.
    stageExtension = appengineExtension.getStage();
    File defaultStagedAppDir = new File(project.getBuildDir(), STAGED_APP_DIR_NAME);
    stageExtension.setStagingDirectory(defaultStagedAppDir);
    stageExtension.setAppEngineDirectory(new File(project.getProjectDir(), "src/main/appengine"));
    File dockerOptionalDir = new File(project.getProjectDir(), "src/main/docker");
    if (dockerOptionalDir.exists()) {
      // only set the docker directory if we find it.
      stageExtension.setDockerDirectory(dockerOptionalDir);
    }

    // obtain deploy extension set defaults
    DeployExtension deploy = appengineExtension.getDeploy();
    deploy.setDeployables(new File(defaultStagedAppDir, "app.yaml"));
    // grab default project configuration from staging default
    deploy.setAppEngineDirectory(stageExtension.getAppEngineDirectory());

    project.afterEvaluate(
        project -> {
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
        });
  }

  private void createStageTask() {
    StageFlexibleTask stageTask =
        project
            .getTasks()
            .create(
                STAGE_TASK_NAME,
                StageFlexibleTask.class,
                stageTask1 -> {
                  stageTask1.setGroup(APP_ENGINE_FLEXIBLE_TASK_GROUP);
                  stageTask1.setDescription(
                      "Stage an App Engine flexible environment application for deployment");
                  stageTask1.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                  project.afterEvaluate(project -> stageTask1.setStagingConfig(stageExtension));
                });
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_TASK_NAME)
        .dependsOn(stageTask);
  }
}
