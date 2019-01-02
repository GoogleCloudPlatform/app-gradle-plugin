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

import com.google.cloud.tools.appengine.operations.CloudSdk;
import com.google.cloud.tools.appengine.operations.cloudsdk.AppEngineJavaComponentsNotInstalledException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkVersionFileException;
import com.google.common.base.Strings;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class CheckCloudSdkTask extends DefaultTask {

  private CloudSdk cloudSdk;
  private String version;
  private boolean requiresAppEngineJava;

  public void setVersion(String version) {
    this.version = version;
  }

  public void setCloudSdk(CloudSdk cloudSdk) {
    this.cloudSdk = cloudSdk;
  }

  public void requiresAppEngineJava(boolean requiresAppEngineJava) {
    this.requiresAppEngineJava = requiresAppEngineJava;
  }

  /** Task entrypoint : Verify Cloud SDK installation. */
  @TaskAction
  public void checkCloudSdkAction()
      throws CloudSdkNotFoundException, CloudSdkVersionFileException, CloudSdkOutOfDateException,
          AppEngineJavaComponentsNotInstalledException {
    // These properties are only set by AppEngineCorePluginConfiguration if the correct config
    // params are set in the tools extension.
    if (Strings.isNullOrEmpty(version) || cloudSdk == null) {
      throw new GradleException(
          "Cloud SDK home path and version must be configured in order to run this task.");
    }

    if (!version.equals(cloudSdk.getVersion().toString())) {
      throw new GradleException(
          "Specified Cloud SDK version ("
              + version
              + ") does not match installed version ("
              + cloudSdk.getVersion()
              + ").");
    }

    cloudSdk.validateCloudSdk();
    if (requiresAppEngineJava) {
      cloudSdk.validateAppEngineJavaComponents();
    }
  }
}
