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

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.configuration.DeployConfiguration;
import com.google.cloud.tools.appengine.operations.Gcloud;
import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import org.gradle.api.tasks.TaskAction;

/** Task to deploy App Engine applications. */
public class DeployTask extends GcloudTask {

  private DeployExtension deployExtension;
  private Path appYaml;
  private Gcloud gcloud;

  public void setDeployConfig(DeployExtension deployExtension) {
    this.deployExtension = deployExtension;
  }

  public void setAppYaml(Path appYaml) {
    this.appYaml = appYaml;
  }

  public void setGcloud(Gcloud gcloud) {
    this.gcloud = gcloud;
  }

  /** Task Entrypoint : DeployExtension application (via app.yaml). */
  @TaskAction
  public void deployAction() throws AppEngineException {
    DeployConfiguration deployConfig =
        deployExtension.toDeployConfiguration(ImmutableList.of(appYaml));
    gcloud.newDeployment(CloudSdkOperations.getDefaultHandler(getLogger())).deploy(deployConfig);
  }
}
