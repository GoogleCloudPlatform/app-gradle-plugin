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

package com.google.cloud.tools.gradle.appengine.task;

import com.google.cloud.tools.app.api.AppEngineException;
import com.google.cloud.tools.app.api.deploy.AppEngineDeployment;
import com.google.cloud.tools.app.impl.cloudsdk.CloudSdkAppEngineDeployment;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;
import com.google.cloud.tools.gradle.appengine.model.internal.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.model.DeployModel;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Deploy App Engine applications
 */
public class DeployTask extends DefaultTask {

  private DeployModel deployConfig;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  public void setDeployConfig(DeployModel deployConfig) {
    this.deployConfig = deployConfig;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  @TaskAction
  public void deployAction() throws AppEngineException {
    CloudSdk sdk = cloudSdkBuilderFactory.newBuilder().build();
    AppEngineDeployment deploy = new CloudSdkAppEngineDeployment(sdk);
    deploy.deploy(deployConfig);
  }

}
