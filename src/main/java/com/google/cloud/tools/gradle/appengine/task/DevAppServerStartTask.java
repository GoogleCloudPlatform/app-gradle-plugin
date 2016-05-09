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
    final CountDownLatch semaphore = new CountDownLatch(1);

    DefaultProcessRunner processRunner = new DefaultProcessRunner(
        new ProcessBuilder().redirectErrorStream(true));
    processRunner.setAsync(true);

    ProcessOutputLineListener lineListener = new ProcessOutputLineListener() {
      final PrintStream logFilePrinter;

      {
        File logFile = File
            .createTempFile("server", "log", new File(getProject().getBuildDir(), "tmp"));
        logFilePrinter = new PrintStream(logFile);
        getLogger().lifecycle("Dev App Server output written to : " + logFile.getAbsolutePath());
      }

      @Override
      public void outputLine(String line) {
        logFilePrinter.println(line);
        if (semaphore.getCount() == 0L) {
          return;
        }
        if (line.contains("Dev App Server is now running")) {
          semaphore.countDown();
        }
      }
    };

    processRunner.setStdOutLineListener(lineListener);

    CloudSdkAppEngineDevServer server = new CloudSdkAppEngineDevServer(
        new CloudSdk(cloudSdkHome, processRunner));
    server.run(runConfig);

    try {
      semaphore.await(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      // do we care that the wait was interrupted?
    }
  }

}
