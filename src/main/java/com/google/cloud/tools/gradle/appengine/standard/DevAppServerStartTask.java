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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkBuilderFactory;
import java.io.IOException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/** Start the App Engine development server asynchronously. */
public class DevAppServerStartTask extends DefaultTask {

  private RunExtension runConfig;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private DevAppServerHelper serverHelper = new DevAppServerHelper();

  public void setRunConfig(RunExtension runConfig) {
    this.runConfig = runConfig;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  /** Task entrypoint : start the dev appserver (non-blocking). */
  @TaskAction
  public void startAction() throws AppEngineException, IOException {

    CloudSdk sdk =
        cloudSdkBuilderFactory
            .newBuilder(getLogger())
            .async(true)
            .runDevAppServerWait(runConfig.getStartSuccessTimeout())
            .build();

    serverHelper.getAppServer(sdk, runConfig).run(runConfig);
  }
}
