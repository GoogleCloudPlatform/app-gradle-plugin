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

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.configuration.DeployConfiguration;
import com.google.cloud.tools.appengine.operations.Deployment;
import com.google.cloud.tools.appengine.operations.Gcloud;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class DeployAllTask extends GcloudTask {

  private DeployExtension deployExtension;
  private Gcloud gcloud;
  private File stageDirectory;

  public void setDeployExtension(DeployExtension deployExtension) {
    this.deployExtension = deployExtension;
  }

  public void setGcloud(Gcloud gcloud) {
    this.gcloud = gcloud;
  }

  public void setStageDirectory(File stageDirectory) {
    this.stageDirectory = stageDirectory;
  }

  /** Task Entrypoint : Deploys the app and all of its config files. */
  @TaskAction
  public void deployAllAction() throws AppEngineException {
    List<Path> deployables = new ArrayList<>();

    // Look for app.yaml
    Path appYaml = stageDirectory.toPath().resolve("app.yaml");
    if (!Files.isRegularFile(appYaml)) {
      throw new GradleException("Failed to deploy all: app.yaml not found.");
    }
    addDeployable(deployables, appYaml);

    // Look for configuration yamls
    String[] validYamls = {"cron.yaml", "dispatch.yaml", "dos.yaml", "index.yaml", "queue.yaml"};
    for (String yamlName : validYamls) {
      Path yaml = deployExtension.getAppEngineDirectory().toPath().resolve(yamlName);
      if (Files.isRegularFile(yaml)) {
        addDeployable(deployables, yaml);
      }
    }

    // Deploy
    Deployment deploy = gcloud.newDeployment(CloudSdkOperations.getDefaultHandler(getLogger()));

    DeployConfiguration deployConfig = deployExtension.toDeployConfiguration(deployables);
    deploy.deploy(deployConfig);
  }

  private void addDeployable(List<Path> deployables, Path yaml) {
    getLogger().info("appengineDeployAll: Preparing to deploy " + yaml.getFileName());
    deployables.add(yaml);
  }
}
