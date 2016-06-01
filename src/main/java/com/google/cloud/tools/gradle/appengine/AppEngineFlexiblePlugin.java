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

import com.google.cloud.tools.gradle.appengine.model.internal.CloudSdkBuilderProvider;
import com.google.cloud.tools.gradle.appengine.model.AppEngineFlexibleModel;
import com.google.cloud.tools.gradle.appengine.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.task.StageFlexibleTask;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.project.ProjectIdentifier;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
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
 * Plugin definition for App Engine flexible environments
 */
public class AppEngineFlexiblePlugin implements Plugin<Project> {

  private static final String STAGE_TASK_NAME = "gcpAppStage";
  private static final String DEPLOY_TASK_NAME = "gcpAppDeploy";
  private static final String APP_ENGINE_FLEXIBLE_TASK_GROUP = "App Engine flexible environment";
  private static final String STAGED_APP_DIR_NAME = "staged-app";

  private static File archivePathFromProjectScope = null;

  @Override
  public void apply(Project project) {
    // pretty poor form here - sharing data via a static variable, but we need this hack to
    // find archives until jar/war RuleSource plugins are ready
    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        if (project.getPlugins().hasPlugin(WarPlugin.class)) {
          War war = (War) project.getProperties().get("war");
          archivePathFromProjectScope = war.getArchivePath();
        }
        else {
          Jar jar = (Jar) project.getProperties().get("jar");
          archivePathFromProjectScope = jar.getArchivePath();
        }
      }
    });
  }

  /**
   * RuleSource configuration for the plugin
   */
  public static class PluginRules extends RuleSource {

    @Model
    public void gcpApp(AppEngineFlexibleModel app) {
    }

    @Defaults
    public void setDefaults(AppEngineFlexibleModel app, @Path("buildDir") File buildDir,
        ProjectIdentifier project) {
      app.getStage().setArtifact(archivePathFromProjectScope);
      app.getStage().setStagingDirectory(new File(buildDir, STAGED_APP_DIR_NAME));
      List<File> deployables = Collections
          .singletonList(new File(app.getStage().getStagingDirectory(), "app.yaml"));
      app.getDeploy().setDeployables(deployables);

      // TODO : look up using the convention for sourcesets here?
      app.getStage().setDockerfile(new File(project.getProjectDir(), "src/main/docker/Dockerfile"));
      app.getStage().setAppYaml(new File(project.getProjectDir(), "src/main/appengine/app.yaml"));
    }

    @Mutate
    public void createCloudSdkBuilderProvider(final AppEngineFlexibleModel app) {
      app.getTools().setCloudSdkBuilderProvider(
          new CloudSdkBuilderProvider(app.getTools().getCloudSdkHome()));
    }

    @Mutate
    public void createStageTask(final ModelMap<Task> tasks, final AppEngineFlexibleModel app) {
      tasks.create(STAGE_TASK_NAME, StageFlexibleTask.class, new Action<StageFlexibleTask>() {
        @Override
        public void execute(StageFlexibleTask stageTask) {
          stageTask.setStagingConfig(app.getStage());
          stageTask.setGroup(APP_ENGINE_FLEXIBLE_TASK_GROUP);
          stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        }
      });
    }

    @Finalize
    public void createDeployTask(ModelMap<Task> tasks, final AppEngineFlexibleModel app) {

      tasks.create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
        @Override
        public void execute(DeployTask deployTask) {
          deployTask.setDeployConfig(app.getDeploy());
          deployTask.setCloudSdkBuilderProvider(app.getTools().getCloudSdkBuilderProvider());
          deployTask.setGroup(APP_ENGINE_FLEXIBLE_TASK_GROUP);
          deployTask.dependsOn(STAGE_TASK_NAME);
        }
      });
    }
  }
}
