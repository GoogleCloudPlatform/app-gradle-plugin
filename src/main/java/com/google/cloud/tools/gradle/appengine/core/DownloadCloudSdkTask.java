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

import com.google.common.base.Strings;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DownloadCloudSdkTask extends DefaultTask {

  private ToolsExtension toolsExtension;

  public void setToolsConfig(ToolsExtension toolsExtension) {
    this.toolsExtension = toolsExtension;
  }

  /** Task entrypoint : Download/update/verify Cloud SDK installation. */
  @TaskAction
  public void downloadCloudSdkAction() {
    getProject().getLogger().lifecycle("Running managedSdkTask.");

    getProject().getLogger().lifecycle("Cloud SDK Version: " + toolsExtension.getCloudSdkVersion());
    getProject().getLogger().lifecycle("Cloud SDK Home: " + toolsExtension.getCloudSdkHome());

    String sdkVersion = toolsExtension.getCloudSdkVersion();
    File sdkHome = toolsExtension.getCloudSdkHome();

    if (sdkHome == null) {
      if (Strings.isNullOrEmpty(sdkVersion)) {
        // Wants to download, but version isn't specified; assume latest version
        // TODO: Download sdk at latest version
      } else {
        // Wants to download specific version
        // TODO: Download sdk at specified version
      }
    } else {
      if (!Strings.isNullOrEmpty(sdkVersion)) {
        // SDK home not specified; try to find installation
        // TODO: Validate installation
      } else {
        // No validation required
      }
    }
  }
}
