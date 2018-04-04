/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.deploy.AppEngineDeployment;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DeployAllTask extends DefaultTask {

  private DeployExtension deployConfig;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  public void setDeployConfig(DeployExtension deployConfig) {
    this.deployConfig = deployConfig;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  /** Task Entrypoint : Deploys the app and all of its config files. */
  @TaskAction
  public void deployAllAction() throws AppEngineException {
    if (!deployConfig.getDeployables().isEmpty()) {
      getLogger().warn("appengineDeployAll: Ignoring configured deployables.");
      deployConfig.getDeployables().clear();
    }

    String[] validYamls = {
      "app.yaml", "cron.yaml", "dispatch.yaml", "dos.yaml", "index.yaml", "queue.yaml"
    };
    for (String yamlName : validYamls) {
      File yaml = deployConfig.getAppEngineDirectory().toPath().resolve(yamlName).toFile();
      if (yaml.exists()) {
        getLogger().info("appengineDeployAll: Preparing to deploy " + yamlName);
        deployConfig.getDeployables().add(yaml);
      }
    }

    CloudSdk sdk = cloudSdkBuilderFactory.newBuilder(getLogger()).build();
    AppEngineDeployment deploy = cloudSdkBuilderFactory.newAppEngineDeployment(sdk);
    deploy.deploy(deployConfig);
  }
}
