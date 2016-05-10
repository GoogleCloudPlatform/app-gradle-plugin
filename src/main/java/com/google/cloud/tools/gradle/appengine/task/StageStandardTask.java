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
import com.google.cloud.tools.app.impl.cloudsdk.CloudSdkAppEngineStandardStaging;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;
import com.google.cloud.tools.gradle.appengine.model.StageStandardModel;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Stage App Engine Standard Environment applications for deployment
 */
public class StageStandardTask extends DefaultTask {

  private StageStandardModel stagingConfig;
  private File cloudSdkHome;

  @Nested
  public StageStandardModel getStagingConfig() {
                                               return stagingConfig;
                                                                    }

  public void setStagingConfig(StageStandardModel stagingConfig) {
    this.stagingConfig = stagingConfig;
  }

  public void setCloudSdkHome(File cloudSdkHome) {
                                                 this.cloudSdkHome = cloudSdkHome;
                                                                                  }

  @TaskAction
  public void stageAction() throws AppEngineException {
    getProject().delete(stagingConfig.getStagingDirectory());

    CloudSdk sdk = new CloudSdk.Builder().sdkPath(cloudSdkHome).build();
    CloudSdkAppEngineStandardStaging staging = new CloudSdkAppEngineStandardStaging(sdk);
    staging.stageStandard(stagingConfig);
  }
}
