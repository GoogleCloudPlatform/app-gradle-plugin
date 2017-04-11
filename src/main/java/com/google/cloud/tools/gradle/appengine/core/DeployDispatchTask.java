/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
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

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.deploy.AppEngineDeployment;
import com.google.cloud.tools.appengine.api.deploy.DeployProjectConfigurationConfiguration;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkAppEngineDeployment;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/** Task to deploy dispatch configuration. */
public class DeployDispatchTask extends DefaultTask {

  private DeployProjectConfigurationConfiguration config;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  public void setDeployConfig(DeployProjectConfigurationConfiguration config) {
    this.config = config;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  /** Task entrypoint : deploy dispatch.yaml. */
  @TaskAction
  public void deployAction() throws AppEngineException {
    CloudSdk sdk = cloudSdkBuilderFactory.newBuilder(getLogger()).build();
    AppEngineDeployment deploy = new CloudSdkAppEngineDeployment(sdk);
    deploy.deployDispatch(config);
  }
}
