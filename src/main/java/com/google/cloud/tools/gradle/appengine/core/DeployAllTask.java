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
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class DeployAllTask extends DefaultTask {

  private DeployExtension deployConfig;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private File stageDirectory;

  public void setDeployConfig(DeployExtension deployConfig) {
    this.deployConfig = deployConfig;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  public void setStageDirectory(File stageDirectory) {
    this.stageDirectory = stageDirectory;
  }

  /** Task Entrypoint : Deploys the app and all of its config files. */
  @TaskAction
  public void deployAllAction() throws AppEngineException {
    List<File> deployables = new ArrayList<>();

    // Look for app.yaml
    File appYaml = stageDirectory.toPath().resolve("app.yaml").toFile();
    if (!appYaml.exists()) {
      throw new GradleException("Failed to deploy all: app.yaml not found.");
    }
    addDeployable(deployables, appYaml);

    // Look for configuration yamls
    String[] validYamls = {"cron.yaml", "dispatch.yaml", "dos.yaml", "index.yaml", "queue.yaml"};
    for (String yamlName : validYamls) {
      File yaml = deployConfig.getAppEngineDirectory().toPath().resolve(yamlName).toFile();
      if (yaml.exists()) {
        addDeployable(deployables, yaml);
      }
    }

    // Deploy
    CloudSdk sdk = cloudSdkBuilderFactory.newBuilder(getLogger()).build();
    AppEngineDeployment deploy = cloudSdkBuilderFactory.newAppEngineDeployment(sdk);
    deploy.deploy(new DeployExtension(deployConfig, deployables));
  }

  private void addDeployable(List<File> deployables, File yaml) {
    getLogger().info("appengineDeployAll: Preparing to deploy " + yaml.getName());
    deployables.add(yaml);
  }
}
