/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

public class DownloadCloudSdkTask extends DefaultTask {

  private CloudSdkDownloader downloader;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private ToolsExtension toolsExtension;

  public void setToolsExtension(ToolsExtension toolsExtension) {
    this.toolsExtension = toolsExtension;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  public void setSdkDownloader(CloudSdkDownloader downloader) {
    this.downloader = downloader;
  }

  /** Task entrypoint : Download/update/verify Cloud SDK installation. */
  @TaskAction
  public void downloadCloudSdkAction() {
    String sdkVersion = toolsExtension.getCloudSdkVersion();
    File sdkHome = toolsExtension.getCloudSdkHome();

    if (sdkHome == null) {
      if (Strings.isNullOrEmpty(sdkVersion)) {
        // Wants to download, but version isn't specified; assume latest version
        sdkVersion = "LATEST";
      }

      sdkHome = downloader.downloadSdk(sdkVersion);
    } else {
      if (!Strings.isNullOrEmpty(sdkVersion)) {
        // Sdk home and version specified; validate installation
        getLogger().lifecycle("\tdownloadCloudSdk: Validating Cloud SDK installation.");
        if (!downloader.isSdkValid(sdkVersion, sdkHome)) {
          throw new GradleException(
              "Specified Cloud SDK version and actual version of the SDK installed in the "
                  + "specified directory do not match. You must either specify the correct "
                  + "cloudSdkHome and cloudSdkVersion, or you can remove the cloudSdkHome field "
                  + "to download the version you want.");
        }
      }
    }

    cloudSdkBuilderFactory.setCloudSdkHome(sdkHome);
  }
}
