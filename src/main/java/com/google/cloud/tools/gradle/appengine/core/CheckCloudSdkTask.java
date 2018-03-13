/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

import com.google.cloud.tools.appengine.cloudsdk.AppEngineJavaComponentsNotInstalledException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkVersionFileException;
import com.google.common.base.Strings;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

public class CheckCloudSdkTask extends DefaultTask {

  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private String version;

  public void setVersion(String version) {
    this.version = version;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  /** Task entrypoint : Verify Cloud SDK installation. */
  @TaskAction
  public void checkCloudSdkAction() {
    if (Strings.isNullOrEmpty(version) || cloudSdkBuilderFactory == null) {
      throw new GradleException(
          "Cloud SDK home path and version must be configured in order to run this task.");
    }

    CloudSdk cloudSdk = cloudSdkBuilderFactory.newBuilder().build();
    if (!version.equals(cloudSdk.getVersion().toString())) {
      throw new GradleException(
          "Specified Cloud SDK version ("
              + version
              + ") does not match installed version ("
              + cloudSdk.getVersion()
              + ").");
    }

    try {
      cloudSdk.validateCloudSdk();
      cloudSdk.validateAppEngineJavaComponents();
    } catch (CloudSdkNotFoundException
        | CloudSdkOutOfDateException
        | CloudSdkVersionFileException
        | AppEngineJavaComponentsNotInstalledException ex) {
      throw new TaskExecutionException(this, ex);
    }
  }
}
