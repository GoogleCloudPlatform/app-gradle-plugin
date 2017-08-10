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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePlugin;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import java.io.File;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.War;

/** Plugin definition for App Engine standard environments. */
public class AppEngineStandardPlugin implements Plugin<Project> {

  public static final String APP_ENGINE_STANDARD_TASK_GROUP = "App Engine Standard environment";
  public static final String EXPLODE_WAR_TASK_NAME = "explodeWar";
  public static final String STAGE_TASK_NAME = "appengineStage";
  public static final String RUN_TASK_NAME = "appengineRun";
  public static final String START_TASK_NAME = "appengineStart";
  public static final String STOP_TASK_NAME = "appengineStop";

  public static final String STAGED_APP_DIR_NAME = "staged-app";
  public static final String DEV_APP_SERVER_OUTPUT_DIR_NAME = "dev-appserver-out";

  public static final String STAGE_EXTENSION = "stage";
  public static final String RUN_EXTENSION = "run";

  private Project project;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private RunExtension runExtension;
  private StageStandardExtension stageExtension;
  private File explodedWarDir;

  @Override
  public void apply(Project project) {
    this.project = project;
    project.getPluginManager().apply(AppEngineCorePlugin.class);

    explodedWarDir = new File(project.getBuildDir(), "exploded-" + project.getName());

    configureExtensions();

    createExplodedWarTask();
    createStageTask();
    createRunTasks();

    AppEngineCorePlugin.overrideCoreTasksGroup(project, APP_ENGINE_STANDARD_TASK_GROUP);
  }

  private void configureExtensions() {
    // obtain extensions defined by core plugin.
    ExtensionAware appengine =
        new ExtensionUtil(project).get(AppEngineCorePlugin.APPENGINE_EXTENSION);

    // create the run extension and set defaults.
    runExtension = appengine.getExtensions().create(RUN_EXTENSION, RunExtension.class, project);
    runExtension.setStartSuccessTimeout(20);
    runExtension.setServices(explodedWarDir);
    runExtension.setServerVersion("1");

    // create the stage extension and set defaults.
    stageExtension =
        appengine.getExtensions().create(STAGE_EXTENSION, StageStandardExtension.class, project);
    File defaultStagedAppDir = new File(project.getBuildDir(), STAGED_APP_DIR_NAME);
    stageExtension.setSourceDirectory(explodedWarDir);
    stageExtension.setStagingDirectory(defaultStagedAppDir);

    // obtain deploy extension and set defaults
    DeployExtension deploy = new ExtensionUtil(appengine).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    deploy.setDeployables(new File(defaultStagedAppDir, "app.yaml"));
    deploy.setAppEngineDirectory(new File(defaultStagedAppDir, "WEB-INF/appengine-generated"));

    // tools extension required to initialize cloudSdkBuilderFactory
    final ToolsExtension tools =
        new ExtensionUtil(appengine).get(AppEngineCorePlugin.TOOLS_EXTENSION);
    project.afterEvaluate(
        new Action<Project>() {
          @Override
          public void execute(Project project) {
            // create the sdk builder factory after we know the location of the sdk
            cloudSdkBuilderFactory = new CloudSdkBuilderFactory(tools.getCloudSdkHome());
          }
        });
  }

  private void createExplodedWarTask() {
    project
        .getTasks()
        .create(
            EXPLODE_WAR_TASK_NAME,
            ExplodeWarTask.class,
            new Action<ExplodeWarTask>() {
              @Override
              public void execute(final ExplodeWarTask explodeWar) {
                explodeWar.setExplodedAppDirectory(explodedWarDir);
                explodeWar.dependsOn(WarPlugin.WAR_TASK_NAME);
                explodeWar.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                explodeWar.setDescription("Explode a war into a directory");

                project.afterEvaluate(
                    new Action<Project>() {
                      @Override
                      public void execute(Project project) {
                        explodeWar.setWarFile(
                            ((War) project.getTasks().getByPath(WarPlugin.WAR_TASK_NAME))
                                .getArchivePath());
                      }
                    });
              }
            });
    project.getTasks().getByName(BasePlugin.ASSEMBLE_TASK_NAME).dependsOn(EXPLODE_WAR_TASK_NAME);
  }

  private void createStageTask() {

    StageStandardTask stageTask =
        project
            .getTasks()
            .create(
                STAGE_TASK_NAME,
                StageStandardTask.class,
                new Action<StageStandardTask>() {
                  @Override
                  public void execute(final StageStandardTask stageTask) {
                    stageTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                    stageTask.setDescription(
                        "Stage an App Engine standard environment application for deployment");
                    stageTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                    project.afterEvaluate(
                        new Action<Project>() {
                          @Override
                          public void execute(Project project) {
                            stageTask.setStagingConfig(stageExtension);
                            stageTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                          }
                        });
                  }
                });

    // All deployment tasks depend on the stage task.
    project.getTasks().getByName(AppEngineCorePlugin.DEPLOY_TASK_NAME).dependsOn(stageTask);
    project.getTasks().getByName(AppEngineCorePlugin.DEPLOY_CRON_TASK_NAME).dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePlugin.DEPLOY_DISPATCH_TASK_NAME)
        .dependsOn(stageTask);
    project.getTasks().getByName(AppEngineCorePlugin.DEPLOY_DOS_TASK_NAME).dependsOn(stageTask);
    project.getTasks().getByName(AppEngineCorePlugin.DEPLOY_INDEX_TASK_NAME).dependsOn(stageTask);
    project.getTasks().getByName(AppEngineCorePlugin.DEPLOY_QUEUE_TASK_NAME).dependsOn(stageTask);
  }

  private void createRunTasks() {
    project
        .getTasks()
        .create(
            RUN_TASK_NAME,
            DevAppServerRunTask.class,
            new Action<DevAppServerRunTask>() {
              @Override
              public void execute(final DevAppServerRunTask runTask) {
                runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                runTask.setDescription(
                    "Run an App Engine standard environment application locally");
                runTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                project.afterEvaluate(
                    new Action<Project>() {
                      @Override
                      public void execute(Project project) {
                        runTask.setRunConfig(runExtension);
                        runTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                      }
                    });
              }
            });

    project
        .getTasks()
        .create(
            START_TASK_NAME,
            DevAppServerStartTask.class,
            new Action<DevAppServerStartTask>() {
              @Override
              public void execute(final DevAppServerStartTask startTask) {
                startTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                startTask.setDescription(
                    "Run an App Engine standard environment application locally in the background");
                startTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                project.afterEvaluate(
                    new Action<Project>() {
                      @Override
                      public void execute(Project project) {
                        startTask.setRunConfig(runExtension);
                        startTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                        startTask.setDevAppServerLoggingDir(
                            new File(project.getBuildDir(), DEV_APP_SERVER_OUTPUT_DIR_NAME));
                      }
                    });
              }
            });

    project
        .getTasks()
        .create(
            STOP_TASK_NAME,
            DevAppServerStopTask.class,
            new Action<DevAppServerStopTask>() {
              @Override
              public void execute(final DevAppServerStopTask stopTask) {
                stopTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                stopTask.setDescription(
                    "Stop a locally running App Engine standard environment application");

                project.afterEvaluate(
                    new Action<Project>() {
                      @Override
                      public void execute(Project project) {
                        stopTask.setRunConfig(runExtension);
                        stopTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                      }
                    });
              }
            });
  }
}
