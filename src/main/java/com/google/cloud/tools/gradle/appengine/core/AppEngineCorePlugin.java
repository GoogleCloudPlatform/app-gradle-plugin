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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.gradle.appengine.core.extension.AppEngine;
import com.google.cloud.tools.gradle.appengine.core.extension.Deploy;
import com.google.cloud.tools.gradle.appengine.core.extension.Tools;
import com.google.cloud.tools.gradle.appengine.core.task.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.core.task.DeployCronTask;
import com.google.cloud.tools.gradle.appengine.core.task.DeployDispatchTask;
import com.google.cloud.tools.gradle.appengine.core.task.DeployDosTask;
import com.google.cloud.tools.gradle.appengine.core.task.DeployIndexTask;
import com.google.cloud.tools.gradle.appengine.core.task.DeployQueueTask;
import com.google.cloud.tools.gradle.appengine.core.task.DeployTask;
import com.google.cloud.tools.gradle.appengine.core.task.ShowConfigurationTask;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.plugins.ExtensionAware;

import java.util.concurrent.Callable;

/**
 * Core plugin for App Engine, contains common tasks like deploy and show configuration
 * Also instantiates the "tools" extension to specify the cloud sdk path
 */
public class AppEngineCorePlugin implements Plugin<Project> {

  public static final String APP_ENGINE_TASK_GROUP = "appengine";

  public static final String DEPLOY_TASK_NAME = "appengineDeploy";
  public static final String DEPLOY_CRON_TASK_NAME = "appengineDeployCron";
  public static final String DEPLOY_DISPATCH_TASK_NAME = "appengineDeployDispatch";
  public static final String DEPLOY_DOS_TASK_NAME = "appengineDeployDos";
  public static final String DEPLOY_INDEX_TASK_NAME = "appengineDeployIndex";
  public static final String DEPLOY_QUEUE_TASK_NAME = "appengineDeployQueue";
  public static final String SHOW_CONFIG_TASK_NAME = "appengineShowConfiguration";

  public static final String APPENGINE_EXTENSION = "appengine";
  public static final String DEPLOY_EXTENSION = "deploy";
  public static final String TOOLS_EXTENSION = "tools";

  private Project project;
  private AppEngine extension;
  private Deploy deployExtension;
  private Tools toolsExtension;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Override
  public void apply(Project project) {
    this.project = project;
    createExtensions();

    createDeployTask();
    createDeployCronTask();
    createDeployDispatchTask();
    createDeployDosTask();
    createDeployIndexTask();
    createDeployQueueTask();
    createShowConfigurationTask();
  }

  private void createExtensions() {
    extension = project.getExtensions().create(APPENGINE_EXTENSION, AppEngine.class);
    deployExtension = ((ExtensionAware) extension).getExtensions().create(DEPLOY_EXTENSION, Deploy.class, project);
    toolsExtension = ((ExtensionAware) extension).getExtensions().create(TOOLS_EXTENSION, Tools.class, project);

    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        // create the sdk builder factory after we know the location of the sdk
        cloudSdkBuilderFactory = new CloudSdkBuilderFactory(toolsExtension.getCloudSdkHome());
      }
    });
  }

  private void createDeployTask() {
    project.getTasks().create(DEPLOY_TASK_NAME, DeployTask.class, new Action<DeployTask>() {
      @Override
      public void execute(final DeployTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy an App Engine application");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createDeployCronTask() {
    project.getTasks().create(DEPLOY_CRON_TASK_NAME, DeployCronTask.class, new Action<DeployCronTask>() {
      @Override
      public void execute(final DeployCronTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy Cron configuration");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createDeployDispatchTask() {
    project.getTasks().create(DEPLOY_DISPATCH_TASK_NAME, DeployDispatchTask.class, new Action<DeployDispatchTask>() {
      @Override
      public void execute(final DeployDispatchTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy Dispatch configuration");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createDeployDosTask() {
    project.getTasks().create(DEPLOY_DOS_TASK_NAME, DeployDosTask.class, new Action<DeployDosTask>() {
      @Override
      public void execute(final DeployDosTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy Dos configuration");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createDeployIndexTask() {
    project.getTasks().create(DEPLOY_INDEX_TASK_NAME, DeployIndexTask.class, new Action<DeployIndexTask>() {
      @Override
      public void execute(final DeployIndexTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy Index configuration");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createDeployQueueTask() {
    project.getTasks().create(DEPLOY_QUEUE_TASK_NAME, DeployQueueTask.class, new Action<DeployQueueTask>() {
      @Override
      public void execute(final DeployQueueTask deployTask) {
        deployTask.setGroup(APP_ENGINE_TASK_GROUP);
        deployTask.setDescription("Deploy Queue configuration");

        project.afterEvaluate(new Action<Project>() {
          @Override
          public void execute(Project project) {
            deployTask.setDeployConfig(deployExtension);
            deployTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
          }
        });
      }
    });
  }

  private void createShowConfigurationTask() {
    project.getTasks().create(SHOW_CONFIG_TASK_NAME, ShowConfigurationTask.class,
        new Action<ShowConfigurationTask>() {
          @Override
          public void execute(final ShowConfigurationTask showConfigurationTask) {
            showConfigurationTask.setGroup(APP_ENGINE_TASK_GROUP);
            showConfigurationTask.setDescription("Show current App Engine plugin configuration");

            showConfigurationTask.setExtensionId(APPENGINE_EXTENSION);
          }
        });
  }
}
