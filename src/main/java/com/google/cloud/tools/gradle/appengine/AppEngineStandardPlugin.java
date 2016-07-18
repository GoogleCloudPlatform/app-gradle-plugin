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

import com.google.cloud.tools.gradle.appengine.model.AppEngineStandardModel;
import com.google.cloud.tools.gradle.appengine.model.hidden.CloudSdkBuilderFactory;
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
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginConvention;
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
import org.gradle.model.internal.core.Hidden;

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


  public void apply(Project project) {
    project.getPluginManager().apply(WarPlugin.class);

    // Create an extension to share data from project space to model space
    final StandardDataExtension projectData = project.getExtensions()
        .create("_internalProjectData", StandardDataExtension.class);

    configureJavaRuntimeCompatibility(project, projectData);
  }

  private void configureJavaRuntimeCompatibility(final Project project,
      final StandardDataExtension projectData) {
    project.afterEvaluate(new Action<Project>(){
      @Override
      public void execute(Project project){
        JavaPluginConvention javaConvention = project.getConvention()
            .getPlugin(JavaPluginConvention.class);
        JavaVersion javaVersion = javaConvention.getTargetCompatibility();
        projectData.setJavaVersion(javaVersion);
      }
    });

    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        WarPluginConvention warConfig = project.getConvention()
            .getPlugin(WarPluginConvention.class);
        File appengineWebXml = new File(warConfig.getWebAppDir(), "WEB-INF/appengine-web.xml");
        projectData.setAppengineWebXml(appengineWebXml);

      }
    });
  }

  /**
   * RuleSource configuration for the plugin
   */
  public static class PluginRules extends RuleSource {

    @Model
    public void appengine(AppEngineStandardModel app) {
    }

    @Model
    @Hidden
    public void cloudSdkBuilderFactory(CloudSdkBuilderFactory factory) {
    }

    @Defaults
    public void setDefaults(AppEngineStandardModel app, @Path("buildDir") File buildDir,
        ExtensionContainer extension) {
      app.getStage().setSourceDirectory(new File(buildDir, EXPLODED_APP_DIR_NAME));
      app.getStage().setStagingDirectory(new File(buildDir, STAGED_APP_DIR_NAME));

      List<File> deployables = Collections
          .singletonList(new File(app.getStage().getStagingDirectory(), "app.yaml"));
      app.getRun().setAppYamls(deployables);
      app.getDeploy().setDeployables(deployables);

      StandardDataExtension projectData = extension.getByType(StandardDataExtension.class);
      if (projectData.getJavaVersion().compareTo(JavaVersion.VERSION_1_8) >= 0 &&
          AppEngineWebXml.parse(projectData.getAppengineWebXml()).isVm()) {
        app.getStage().setRuntime("java");
      }

    }

    @Mutate
    public void createCloudSdkBuilderFactory(final CloudSdkBuilderFactory factory,
        final AppEngineStandardModel app) {
      factory.setCloudSdkHome(app.getTools().getCloudSdkHome());
    }

    @Mutate
    public void createExplodedWarTask(final ModelMap<Task> tasks,
        @Path("tasks.war") final War warTask, @Path("buildDir") final File buildDir) {

      tasks.create(EXPLODE_WAR_TASK_NAME, ExplodeWarTask.class, new Action<ExplodeWarTask>() {
        @Override
        public void execute(ExplodeWarTask explodeWarTask) {
          explodeWarTask.setExplodedAppDirectory(new File(buildDir, EXPLODED_APP_DIR_NAME));
          explodeWarTask.dependsOn(warTask);
          explodeWarTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          explodeWarTask.setDescription("Explode a war into a directory");
          explodeWarTask.setWarFile(warTask.getArchivePath());
        }
      });
      tasks.get(BasePlugin.ASSEMBLE_TASK_NAME).dependsOn(EXPLODE_WAR_TASK_NAME);

    }

    @Finalize
    public void configureDevAppServerJava8RunFlag(final AppEngineStandardModel app) {
      List<String> jvmFlags = app.getRun().getJvmFlags();
      jvmFlags = (jvmFlags == null) ? Lists.<String>newArrayList() : jvmFlags;
      jvmFlags.add("-Dappengine.user.timezone=UTC");
      app.getRun().setJvmFlags(jvmFlags);
      System.out.println(app.getRun().getJvmFlags());
    }

    @Finalize
    public void createStageTask(final ModelMap<Task> tasks, final AppEngineStandardModel app,
      final CloudSdkBuilderFactory factory) {

      tasks.create(STAGE_TASK_NAME, StageStandardTask.class, new Action<StageStandardTask>() {
        @Override
        public void execute(StageStandardTask stageTask) {
          stageTask.setStagingConfig(app.getStage());
          stageTask.setCloudSdkBuilderFactory(factory);
          stageTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          stageTask.setDescription("Stage an App Engine standard environment application for deployment");
          stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);
        }
      });
    }

    @Finalize
    public void createRunTasks(final ModelMap<Task> tasks, final AppEngineStandardModel app,
      final CloudSdkBuilderFactory factory) {

      tasks.create(RUN_TASK_NAME, DevAppServerRunTask.class, new Action<DevAppServerRunTask>() {
        @Override
        public void execute(DevAppServerRunTask runTask) {
          runTask.setRunConfig(app.getRun());
          runTask.setCloudSdkBuilderFactory(factory);
          runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          runTask.setDescription("Run an App Engine standard environment application locally");
          runTask.dependsOn(STAGE_TASK_NAME);
        }
      });

      tasks.create(START_TASK_NAME, DevAppServerStartTask.class, new Action<DevAppServerStartTask>() {
        @Override
        public void execute(DevAppServerStartTask startTask) {
          startTask.setRunConfig(app.getRun());
          startTask.setCloudSdkBuilderFactory(factory);
          startTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          startTask.setDescription("Run an App Engine standard environment application locally in the background");
          startTask.dependsOn(STAGE_TASK_NAME);
        }
      });

      tasks.create(STOP_TASK_NAME, DevAppServerStopTask.class, new Action<DevAppServerStopTask>() {
        @Override
        public void execute(DevAppServerStopTask stopTask) {
          stopTask.setRunConfig(app.getRun());
          stopTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          stopTask.setDescription("Stop a locally running App Engine standard environment application");
        }
      });
    }

    @Finalize
    public void createDeployTask(final ModelMap<Task> tasks, final AppEngineStandardModel app,
      final CloudSdkBuilderFactory factory) {

      tasks.create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
        @Override
        public void execute(DeployTask deployTask) {
          deployTask.setDeployConfig(app.getDeploy());
          deployTask.setCloudSdkBuilderFactory(factory);
          deployTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
          deployTask.setDescription("Deploy an App Engine standard environment application");
          deployTask.dependsOn(STAGE_TASK_NAME);
        }
      });
    }
  }
}
