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

import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;

/**
 * Core plugin for App Engine, contains common tasks like deploy and show configuration Also
 * instantiates the "tools" extension to specify the cloud sdk path.
 */
public class AppEngineCorePluginConfiguration {

  public static final GradleVersion GRADLE_MIN_VERSION = GradleVersion.version("3.4.1");

  public static final String LOGIN_TASK_NAME = "appengineCloudSdkLogin";
  public static final String DEPLOY_TASK_NAME = "appengineDeploy";
  public static final String DEPLOY_CRON_TASK_NAME = "appengineDeployCron";
  public static final String DEPLOY_DISPATCH_TASK_NAME = "appengineDeployDispatch";
  public static final String DEPLOY_DOS_TASK_NAME = "appengineDeployDos";
  public static final String DEPLOY_INDEX_TASK_NAME = "appengineDeployIndex";
  public static final String DEPLOY_QUEUE_TASK_NAME = "appengineDeployQueue";
  public static final String SHOW_CONFIG_TASK_NAME = "appengineShowConfiguration";
  public static final String DOWNLOAD_CLOUD_SDK_TASK_NAME = "downloadCloudSdk";
  public static final String CHECK_CLOUD_SDK_TASK_NAME = "checkCloudSdk";

  public static final String APPENGINE_EXTENSION = "appengine";

  private Project project;
  private DeployExtension deployExtension;
  private ToolsExtension toolsExtension;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private ManagedCloudSdk managedCloudSdk;
  private String taskGroup;

  /** Configure core tasks for appengine flexible and standard project plugins. */
  public void configureCoreProperties(
      Project project,
      AppEngineCoreExtensionProperties appEngineCoreExtensionProperties,
      String taskGroup) {
    checkGradleVersion();

    this.project = project;
    this.taskGroup = taskGroup;
    this.toolsExtension = appEngineCoreExtensionProperties.getTools();
    this.deployExtension = appEngineCoreExtensionProperties.getDeploy();
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
    createShowConfigurationTask();
  }

  private void configureFactories() {
    project.afterEvaluate(
        project -> {
          try {
            if (toolsExtension.getCloudSdkHome() == null) {
              managedCloudSdk =
                  new ManagedCloudSdkFactory(toolsExtension.getCloudSdkVersion()).newManagedSdk();
            }
          } catch (UnsupportedOsException | BadCloudSdkVersionException ex) {
            throw new GradleException("Configuring... ", ex);
          }

          cloudSdkBuilderFactory =
              new CloudSdkBuilderFactory(
                  managedCloudSdk != null
                      ? managedCloudSdk.getSdkHome().toFile()
                      : toolsExtension.getCloudSdkHome(),
                  toolsExtension.getServiceAccountKeyFile());
        });
  }

  private void createDownloadCloudSdkTask() {
    project
        .getTasks()
        .create(
            DOWNLOAD_CLOUD_SDK_TASK_NAME,
            DownloadCloudSdkTask.class,
            downloadCloudSdkTask -> {
              downloadCloudSdkTask.setGroup(taskGroup);
              downloadCloudSdkTask.setDescription("Download the Cloud SDK");

              project.afterEvaluate(
                  p -> {
                    if (toolsExtension.getCloudSdkHome() == null) {
                      downloadCloudSdkTask.setManagedCloudSdk(managedCloudSdk);
                      p.getTasks()
                          .matching(task -> task.getName().startsWith("appengine"))
                          .forEach(task -> task.dependsOn(downloadCloudSdkTask));
                    }
                  });
            });
  }

  private void createCheckCloudSdkTask() {
    project
        .getTasks()
        .create(
            CHECK_CLOUD_SDK_TASK_NAME,
            CheckCloudSdkTask.class,
            checkCloudSdkTask -> {
              checkCloudSdkTask.setGroup(taskGroup);
              checkCloudSdkTask.setDescription("Validates the Cloud SDK");

              project.afterEvaluate(
                  p -> {
                    if (toolsExtension.getCloudSdkHome() != null
                        && toolsExtension.getCloudSdkVersion() != null) {
                      checkCloudSdkTask.setVersion(toolsExtension.getCloudSdkVersion());
                      checkCloudSdkTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                      p.getTasks()
                          .matching(task -> task.getName().startsWith("appengine"))
                          .forEach(task -> task.dependsOn(checkCloudSdkTask));
                    }
                  });
            });
  }

  private void createLoginTask() {
    project
        .getTasks()
        .create(
            LOGIN_TASK_NAME,
            CloudSdkLoginTask.class,
            loginTask -> {
              loginTask.setGroup(taskGroup);
              loginTask.setDescription("Login and set the Cloud SDK common configuration user");

              project.afterEvaluate(
                  project -> {
                    loginTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                    if (toolsExtension.getServiceAccountKeyFile() != null) {
                      loginTask.doLast(
                          task ->
                              project
                                  .getLogger()
                                  .warn(
                                      "WARNING: ServiceAccountKeyFile is configured and will be used instead of Cloud SDK auth state"));
                    }
                  });
            });
  }

  private void createDeployTask() {
    project
        .getTasks()
        .create(
            DEPLOY_TASK_NAME,
            DeployTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy an App Engine application");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createDeployCronTask() {
    project
        .getTasks()
        .create(
            DEPLOY_CRON_TASK_NAME,
            DeployCronTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy Cron configuration");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createDeployDispatchTask() {
    project
        .getTasks()
        .create(
            DEPLOY_DISPATCH_TASK_NAME,
            DeployDispatchTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy Dispatch configuration");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createDeployDosTask() {
    project
        .getTasks()
        .create(
            DEPLOY_DOS_TASK_NAME,
            DeployDosTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy Dos configuration");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createDeployIndexTask() {
    project
        .getTasks()
        .create(
            DEPLOY_INDEX_TASK_NAME,
            DeployIndexTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy Index configuration");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createDeployQueueTask() {
    project
        .getTasks()
        .create(
            DEPLOY_QUEUE_TASK_NAME,
            DeployQueueTask.class,
            deployTask -> {
              deployTask.setGroup(taskGroup);
              deployTask.setDescription("Deploy Queue configuration");

              project.afterEvaluate(
                  project -> {
                    deployTask.setDeployConfig(deployExtension);
                    deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
                  });
            });
  }

  private void createShowConfigurationTask() {
    project
        .getTasks()
        .create(
            SHOW_CONFIG_TASK_NAME,
            ShowConfigurationTask.class,
            showConfigurationTask -> {
              showConfigurationTask.setGroup(taskGroup);
              showConfigurationTask.setDescription("Show current App Engine plugin configuration");

              showConfigurationTask.setExtensionId(APPENGINE_EXTENSION);
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
}
