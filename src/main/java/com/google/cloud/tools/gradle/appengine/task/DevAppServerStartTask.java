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
import com.google.cloud.tools.app.impl.cloudsdk.internal.process.DefaultProcessRunner;
import com.google.cloud.tools.app.impl.cloudsdk.internal.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.app.impl.cloudsdk.internal.process.ProcessOutputLineListener;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;
import com.google.cloud.tools.gradle.appengine.model.RunModel;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Start the App Engine development server asynchronously
 */
public class DevAppServerStartTask extends DefaultTask {

  private RunModel runConfig;
  private File cloudSdkHome;

  public void setRunConfig(RunModel runConfig) {
    this.runConfig = runConfig;
  }

  public void setCloudSdkHome(File cloudSdkHome) {
    this.cloudSdkHome = cloudSdkHome;
  }

  @TaskAction
  public void startAction() throws AppEngineException, IOException {
    File logFile = File
        .createTempFile("server", "log", new File(getProject().getBuildDir(), "tmp"));
    getLogger().lifecycle("Dev App Server output written to : " + logFile.getAbsolutePath());

    ProcessOutputLineListener lineListener = new FileOutputLineListener(logFile);
    CloudSdk sdk = new CloudSdk.Builder()
        .sdkPath(cloudSdkHome)
        .appCommandMetricsEnvironment("app-gradle-plugin")
        .async(true)
        .runDevAppServerWait(10)
        .addStdErrLineListener(lineListener)
        .addStdOutLineListener(lineListener)
        .exitListener(new NonZeroExceptionExitListener())
        .build();
    CloudSdkAppEngineDevServer server = new CloudSdkAppEngineDevServer(sdk);
    server.run(runConfig);
  }

  private static class FileOutputLineListener implements  ProcessOutputLineListener {
    final PrintStream logFilePrinter;

    public FileOutputLineListener(File logFile) throws IOException {
      logFilePrinter = new PrintStream(logFile);
    }

    @Override
    public void outputLine(String line) {
      logFilePrinter.println(line);
    }
  }

}
