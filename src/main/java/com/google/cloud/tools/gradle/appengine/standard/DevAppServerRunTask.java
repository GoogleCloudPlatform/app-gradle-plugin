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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.LocalRun;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkOperations;
import org.gradle.api.DefaultTask;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.tasks.TaskAction;

/** RunExtension App Engine Standard Environment applications locally. */
public class DevAppServerRunTask extends DefaultTask {

  private RunExtension runConfig;
  private LocalRun localRun;
  private DevAppServerHelper serverHelper = new DevAppServerHelper();

  public void setRunConfig(RunExtension runConfig) {
    this.runConfig = runConfig;
  }

  public void setLocalRun(LocalRun localRun) {
    this.localRun = localRun;
  }

  /** Task entrypoint : run the devappserver (blocking). */
  @TaskAction
  public void runAction() throws AppEngineException, ProjectConfigurationException {
    serverHelper.getAppServer(localRun, runConfig, CloudSdkOperations.getDefaultHandler(getLogger())).run(runConfig);
  }
}
