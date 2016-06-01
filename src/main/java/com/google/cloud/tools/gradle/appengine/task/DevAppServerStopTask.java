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
import com.google.cloud.tools.app.impl.cloudsdk.CloudSdkAppEngineDevServer;
import com.google.cloud.tools.gradle.appengine.model.RunModel;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Stop the App Engine development server
 */
public class DevAppServerStopTask extends DefaultTask {

  private RunModel runConfig;

  public void setRunConfig(RunModel runConfig) {
    this.runConfig = runConfig;
  }

  @TaskAction
  public void stopAction() throws AppEngineException {
    CloudSdkAppEngineDevServer server = new CloudSdkAppEngineDevServer(null);
    server.stop(runConfig);
  }

}
