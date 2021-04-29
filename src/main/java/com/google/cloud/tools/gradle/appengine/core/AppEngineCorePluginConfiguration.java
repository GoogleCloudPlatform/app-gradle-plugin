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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.util.GradleVersion;

/**
 * Core plugin for App Engine, contains common tasks like deploy and show configuration Also
 * instantiates the "tools" extension to specify the cloud sdk path.
 */
public class AppEngineCorePluginConfiguration {

  public static final GradleVersion GRADLE_MIN_VERSION = GradleVersion.version("4.9");

  public static final String LOGIN_TASK_NAME = "appengineCloudSdkLogin";
  public static final String DEPLOY_TASK_NAME = "appengineDeploy";
  public static final String DEPLOY_CRON_TASK_NAME = "appengineDeployCron";
  public static final String DEPLOY_DISPATCH_TASK_NAME = "appengineDeployDispatch";
  public static final String DEPLOY_DOS_TASK_NAME = "appengineDeployDos";
  public static final String DEPLOY_INDEX_TASK_NAME = "appengineDeployIndex";
  public static final String DEPLOY_QUEUE_TASK_NAME = "appengineDeployQueue";
  public static final String DEPLOY_ALL_TASK_NAME = "appengineDeployAll";
  public static final String SHOW_CONFIG_TASK_NAME = "appengineShowConfiguration";
  public static final String DOWNLOAD_CLOUD_SDK_TASK_NAME = "downloadCloudSdk";
  public static final String CHECK_CLOUD_SDK_TASK_NAME = "checkCloudSdk";

  public static final String APPENGINE_EXTENSION = "appengine";

  private Project project;
  private DeployExtension deployExtension;
  private ToolsExtension toolsExtension;
  private CloudSdkOperations cloudSdkOperations;
  private ManagedCloudSdk managedCloudSdk;
  private boolean requiresAppEngineJava;
  private String taskGroup;

  /** Configure core tasks for appengine app.yaml and appengine-web.xml based project plugins. */
  public void configureCoreProperties(
      Project project,
      AppEngineCoreExtensionProperties appEngineCoreExtensionProperties,
      String taskGroup,
      boolean requiresAppEngineJava) {
    checkGradleVersion();

    this.project = project;
    this.taskGroup = taskGroup;
    toolsExtension = appEngineCoreExtensionProperties.getTools();
    deployExtension = appEngineCoreExtensionProperties.getDeploy();
    this.requiresAppEngineJava = requiresAppEngineJava;
    configureFactories();

    createDownloadCloudSdkTask();
    createCheckCloudSdkTask();
    createLoginTask();
    createDeployTask();
    createDeployCronTask();
    createDeployDispatchTask();
    createDeployDosTask();
    createDeployIndexTask();
    createDeployQueueTask();
    createDeployAllTask();
    createShowConfigurationTask();
    injectGcloud();
  }

  private void configureFactories() {
    project.afterEvaluate(
        projectAfterEvaluated -> {
          try {
            if (toolsExtension.getCloudSdkHome() == null) {
              managedCloudSdk =
                  new ManagedCloudSdkFactory(toolsExtension.getCloudSdkVersion()).newManagedSdk();
              toolsExtension.setCloudSdkHome(managedCloudSdk.getSdkHome().toFile());
            }
          } catch (UnsupportedOsException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
          } catch (BadCloudSdkVersionException ex) {
            throw new RuntimeException(
                "Failed to auto-configure Cloud Sdk at cloudSdkVersion = '"
                    + toolsExtension.getCloudSdkVersion()
                    + "': "
                    + ex.getMessage(),
                ex);
          }

          try {
            cloudSdkOperations =
                new CloudSdkOperations(
                    toolsExtension.getCloudSdkHome(),
                    toolsExtension.getServiceAccountKeyFile(),
                    toolsExtension.getVerbosity());
          } catch (CloudSdkNotFoundException ex) {
            // this should never happen, not found exception only occurs when auto-discovery fails,
            // but we don't use that mechanism anymore.
            throw new AssertionError("Failed when attempting to discover SDK: ", ex);
          }

          deployExtension.setDeployTargetResolver(new DeployTargetResolver(cloudSdkOperations));
        });
  }

  private void createDownloadCloudSdkTask() {
    TaskProvider<DownloadCloudSdkTask> downloadCloudSdk =
        project
            .getTasks()
            .register(
                DOWNLOAD_CLOUD_SDK_TASK_NAME,
                DownloadCloudSdkTask.class,
                task -> {
                  task.setGroup(taskGroup);
                  task.setDescription("Download the Cloud SDK");

                  // make sure we download our required components
                  if (requiresAppEngineJava) {
                    task.requiresComponent(SdkComponent.APP_ENGINE_JAVA);
                  }
                });

    project.afterEvaluate(
        project -> {
          if (managedCloudSdk != null) {
            downloadCloudSdk.configure(task -> task.setManagedCloudSdk(managedCloudSdk));
            if (project.getGradle().getStartParameter().isOffline()) {
              project.getLogger().debug("Skipping DownloadCloudSdk in --offline mode.");
              return;
            }
            project
                .getTasks()
                .matching(task -> task.getName().startsWith("appengine"))
                .configureEach(appEngineTask -> appEngineTask.dependsOn(downloadCloudSdk));
          }
        });
  }

  private void createCheckCloudSdkTask() {
    TaskProvider<CheckCloudSdkTask> checkCloudSdk =
        project
            .getTasks()
            .register(
                CHECK_CLOUD_SDK_TASK_NAME,
                CheckCloudSdkTask.class,
                task -> {
                  task.setGroup(taskGroup);
                  task.setDescription("Validates the Cloud SDK");
                });

    project.afterEvaluate(
        project -> {
          if (managedCloudSdk == null && toolsExtension.getCloudSdkVersion() != null) {
            checkCloudSdk.configure(
                task -> {
                  task.setVersion(toolsExtension.getCloudSdkVersion());
                  task.setCloudSdk(cloudSdkOperations.getCloudSdk());
                  task.requiresAppEngineJava(requiresAppEngineJava);
                });
            project
                .getTasks()
                .matching(task -> task.getName().startsWith("appengine"))
                .configureEach(appEngineTask -> appEngineTask.dependsOn(checkCloudSdk));
          }
        });
  }

  private void createLoginTask() {
    TaskProvider<CloudSdkLoginTask> cloudSdkLogin =
        project
            .getTasks()
            .register(
                LOGIN_TASK_NAME,
                CloudSdkLoginTask.class,
                task -> {
                  task.setGroup(taskGroup);
                  task.setDescription("Login and set the Cloud SDK common configuration user");
                });

    project.afterEvaluate(
        project -> {
          if (toolsExtension.getServiceAccountKeyFile() != null) {
            String warn =
                "WARNING: ServiceAccountKeyFile is configured and will be used instead of Cloud "
                    + "SDK auth state";
            cloudSdkLogin.configure(
                task -> task.doLast(taskOnLast -> project.getLogger().warn(warn)));
          }
        });
  }

  private void createDeployTask() {
    createDeployTaskHelper(
        DEPLOY_TASK_NAME,
        DeployTask.class,
        "Deploy an App Engine application",
        false); // deployExtension is set in AppEngineStandardPlugin and AppEngineAppYamlPlugin
  }

  private void createDeployCronTask() {
    createDeployTaskHelper(
        DEPLOY_CRON_TASK_NAME, DeployCronTask.class, "Deploy Cron configuration", true);
  }

  private void createDeployDispatchTask() {
    createDeployTaskHelper(
        DEPLOY_DISPATCH_TASK_NAME, DeployDispatchTask.class, "Deploy Dispatch configuration", true);
  }

  private void createDeployDosTask() {
    createDeployTaskHelper(
        DEPLOY_DOS_TASK_NAME, DeployDosTask.class, "Deploy Dos configuration", true);
  }

  private void createDeployIndexTask() {
    createDeployTaskHelper(
        DEPLOY_INDEX_TASK_NAME, DeployIndexTask.class, "Deploy Index configuration", true);
  }

  private void createDeployQueueTask() {
    createDeployTaskHelper(
        DEPLOY_QUEUE_TASK_NAME, DeployQueueTask.class, "Deploy Queue configuration", true);
  }

  private void createDeployAllTask() {
    createDeployTaskHelper(
        DEPLOY_ALL_TASK_NAME,
        DeployAllTask.class,
        "Deploy an App Engine application and all of its config files",
        false); // deployExtension is set in AppEngineStandardPlugin and AppEngineAppYamlPlugin
  }

  private void createDeployTaskHelper(
      String taskName,
      Class<? extends BaseDeployTask> taskClass,
      String taskDescription,
      boolean injectDeployExtension) {
    project
        .getTasks()
        .register(
            taskName,
            taskClass,
            task -> {
              task.setGroup(taskGroup);
              task.setDescription(taskDescription);

              if (injectDeployExtension) {
                task.setDeployExtension(deployExtension);
              }
            });
  }

  private void createShowConfigurationTask() {
    project
        .getTasks()
        .register(
            SHOW_CONFIG_TASK_NAME,
            ShowConfigurationTask.class,
            task -> {
              task.setGroup(taskGroup);
              task.setDescription("Show current App Engine plugin configuration");
              task.setExtensionId(APPENGINE_EXTENSION);
            });
  }

  private void checkGradleVersion() {
    if (GRADLE_MIN_VERSION.compareTo(GradleVersion.current()) > 0) {
      throw new GradleException(
          "Detected "
              + GradleVersion.current()
              + ", but the appengine-gradle-plugin requires "
              + GRADLE_MIN_VERSION
              + " or higher.");
    }
  }

  private void injectGcloud() {
    project.afterEvaluate(
        project -> {
          project
              .getTasks()
              .withType(GcloudTask.class)
              .configureEach(task -> task.setGcloud(cloudSdkOperations.getGcloud()));
        });
  }
}
