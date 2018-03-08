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

import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.ConsoleListener;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVerificationException;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVersionMismatchException;
import com.google.cloud.tools.managedcloudsdk.ProgressListener;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExecutionException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExitException;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponentInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstallerException;
import com.google.cloud.tools.managedcloudsdk.update.SdkUpdater;
import java.io.IOException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DownloadCloudSdkTask extends DefaultTask {

  private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  private ManagedCloudSdkFactory managedCloudSdkFactory;

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  public void setManagedCloudSdkFactory(ManagedCloudSdkFactory managedCloudSdkFactory) {
    this.managedCloudSdkFactory = managedCloudSdkFactory;
  }

  /** Task entrypoint : Download/update/verify Cloud SDK installation. */
  @TaskAction
  public void downloadCloudSdkAction()
      throws UnsupportedOsException, BadCloudSdkVersionException, ManagedSdkVerificationException,
          ManagedSdkVersionMismatchException, InterruptedException, CommandExecutionException,
          SdkInstallerException, CommandExitException, IOException {
    ManagedCloudSdk managedCloudSdk = managedCloudSdkFactory.newManagedSdk();

    ProgressListener progressListener =
        new ProgressListener() {
          @Override
          public void start(String message, long totalWork) {
            getLogger().lifecycle(message);
          }

          @Override
          public void update(long workDone) {
            // TODO: Show progress
          }

          @Override
          public void update(String message) {
            getLogger().lifecycle(message);
          }

          @Override
          public void done() {}

          @Override
          public ProgressListener newChild(long allocation) {
            return new ProgressListener() {
              @Override
              public void start(String message, long totalWork) {}

              @Override
              public void update(long workDone) {}

              @Override
              public void update(String message) {}

              @Override
              public void done() {}

              @Override
              public ProgressListener newChild(long allocation) {
                return null;
              }
            };
          }
        };

    ConsoleListener consoleListener =
        new ConsoleListener() {
          @Override
          public void console(String rawString) {}
        };

    // Install sdk if not installed
    if (!managedCloudSdk.isInstalled()) {
      SdkInstaller installer = managedCloudSdk.newInstaller();
      installer.install(progressListener, consoleListener);
    }

    // Install app engine component
    if (!managedCloudSdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)) {
      SdkComponentInstaller componentInstaller = managedCloudSdk.newComponentInstaller();
      componentInstaller.installComponent(
          SdkComponent.APP_ENGINE_JAVA, progressListener, consoleListener);
    }

    // If version is set to LATEST, update Cloud SDK
    if (!managedCloudSdk.isUpToDate()) {
      SdkUpdater updater = managedCloudSdk.newUpdater();
      updater.update(progressListener, consoleListener);
    }

    cloudSdkBuilderFactory.setCloudSdkHome(managedCloudSdk.getSdkHome().toFile());
  }
}
