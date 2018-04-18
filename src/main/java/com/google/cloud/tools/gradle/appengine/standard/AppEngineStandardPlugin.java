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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.core.DeployAllTask;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
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
  private AppEngineStandardExtension appengineExtension;
  private RunExtension runExtension;
  private StageStandardExtension stageExtension;
  private File explodedWarDir;

  @Override
  public void apply(Project project) {
    this.project = project;
    appengineExtension =
        project.getExtensions().create("appengine", AppEngineStandardExtension.class);
    appengineExtension.createSubExtensions(project);

    new AppEngineCorePluginConfiguration()
        .configureCoreProperties(project, appengineExtension, APP_ENGINE_STANDARD_TASK_GROUP);

    explodedWarDir = new File(project.getBuildDir(), "exploded-" + project.getName());

    configureExtensions();

    createExplodedWarTask();
    createStageTask();
    createRunTasks();
  }

  private void configureExtensions() {

    // create the run extension and set defaults.
    runExtension = appengineExtension.getRun();
    runExtension.setStartSuccessTimeout(20);
    runExtension.setServices(explodedWarDir);
    runExtension.setServerVersion("1");

    // create the stage extension and set defaults.
    stageExtension = appengineExtension.getStage();
    File defaultStagedAppDir = new File(project.getBuildDir(), STAGED_APP_DIR_NAME);
    stageExtension.setSourceDirectory(explodedWarDir);
    stageExtension.setStagingDirectory(defaultStagedAppDir);

    // tools extension required to initialize cloudSdkBuilderFactory
    final ToolsExtension tools = appengineExtension.getTools();
    project.afterEvaluate(
        project -> {
          // create the sdk builder factory after we know the location of the sdk
          cloudSdkBuilderFactory = new CloudSdkBuilderFactory(tools.getCloudSdkHome(), null);

          // obtain deploy extension and set defaults
          DeployExtension deploy = appengineExtension.getDeploy();
          if (deploy.getDeployables() == null) {
            deploy.setDeployables(new File(stageExtension.getStagingDirectory(), "app.yaml"));
          }
          if (deploy.getAppEngineDirectory() == null) {
            deploy.setAppEngineDirectory(
                new File(stageExtension.getStagingDirectory(), "WEB-INF/appengine-generated"));
          }

          File appengineWebXml =
              project.getPlugins().hasPlugin(WarPlugin.class)
                  ? project
                      .getConvention()
                      .getPlugin(WarPluginConvention.class)
                      .getWebAppDir()
                      .toPath()
                      .resolve("WEB-INF")
                      .resolve("appengine-web.xml")
                      .toFile()
                  : project
                      .getProjectDir()
                      .toPath()
                      .resolve("src")
                      .resolve("main")
                      .resolve("webapp")
                      .resolve("WEB-INF")
                      .resolve("appengine-web.xml")
                      .toFile();
          deploy.setProjectResolver(new ProjectResolver(appengineWebXml));

          DeployAllTask deployAllTask =
              (DeployAllTask)
                  project
                      .getTasks()
                      .getByName(AppEngineCorePluginConfiguration.DEPLOY_ALL_TASK_NAME);
          deployAllTask.setStageDirectory(stageExtension.getStagingDirectory());
          deployAllTask.setDeployConfig(deploy);
        });
  }

  private void createExplodedWarTask() {
    project
        .getTasks()
        .create(
            EXPLODE_WAR_TASK_NAME,
            ExplodeWarTask.class,
            explodeWar -> {
              explodeWar.setExplodedAppDirectory(explodedWarDir);
              explodeWar.dependsOn(WarPlugin.WAR_TASK_NAME);
              explodeWar.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
              explodeWar.setDescription("Explode a war into a directory");

              project.afterEvaluate(
                  project ->
                      explodeWar.setWarFile(
                          ((War) project.getTasks().getByPath(WarPlugin.WAR_TASK_NAME))
                              .getArchivePath()));
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
                stageTask1 -> {
                  stageTask1.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
                  stageTask1.setDescription(
                      "Stage an App Engine standard environment application for deployment");
                  stageTask1.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

                  project.afterEvaluate(
                      project -> {
                        stageTask1.setStagingConfig(stageExtension);
                        stageTask1.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                      });
                });

    // All deployment tasks depend on the stage task.
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_CRON_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_DISPATCH_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_DOS_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_INDEX_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_QUEUE_TASK_NAME)
        .dependsOn(stageTask);
    project
        .getTasks()
        .getByName(AppEngineCorePluginConfiguration.DEPLOY_ALL_TASK_NAME)
        .dependsOn(stageTask);
  }

  private void createRunTasks() {
    project
        .getTasks()
        .create(
            RUN_TASK_NAME,
            DevAppServerRunTask.class,
            runTask -> {
              runTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
              runTask.setDescription("Run an App Engine standard environment application locally");
              runTask.dependsOn(project.getTasks().findByName(BasePlugin.ASSEMBLE_TASK_NAME));

              project.afterEvaluate(
                  project -> {
                    runTask.setRunConfig(runExtension);
                    runTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });

    project
        .getTasks()
        .create(
            START_TASK_NAME,
            DevAppServerStartTask.class,
            startTask -> {
              startTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
              startTask.setDescription(
                  "Run an App Engine standard environment application locally in the background");
              startTask.dependsOn(project.getTasks().findByName(BasePlugin.ASSEMBLE_TASK_NAME));

              project.afterEvaluate(
                  project -> {
                    startTask.setRunConfig(runExtension);
                    startTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                    startTask.setDevAppServerLoggingDir(
                        new File(project.getBuildDir(), DEV_APP_SERVER_OUTPUT_DIR_NAME));
                  });
            });

    project
        .getTasks()
        .create(
            STOP_TASK_NAME,
            DevAppServerStopTask.class,
            stopTask -> {
              stopTask.setGroup(APP_ENGINE_STANDARD_TASK_GROUP);
              stopTask.setDescription(
                  "Stop a locally running App Engine standard environment application");

              project.afterEvaluate(
                  project -> {
                    stopTask.setRunConfig(runExtension);
                    stopTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }
}
