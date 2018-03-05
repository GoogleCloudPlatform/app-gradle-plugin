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

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.ConsoleListener;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVerificationException;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVersionMismatchException;
import com.google.cloud.tools.managedcloudsdk.ProgressListener;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import com.google.cloud.tools.managedcloudsdk.Version;
import com.google.cloud.tools.managedcloudsdk.command.CommandExecutionException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExitException;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponentInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstallerException;
import com.google.cloud.tools.managedcloudsdk.update.SdkUpdater;
import java.io.File;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;

public class CloudSdkDownloader {

  private Logger logger;

  private SdkInstaller installer;
  private SdkComponentInstaller componentInstaller;
  private SdkUpdater updater;

  public CloudSdkDownloader() {
    this.logger = null;
  }

  public CloudSdkDownloader(Logger logger) {
    this.logger = logger;
  }

  public void setInstaller(SdkInstaller installer) {
    this.installer = installer;
  }

  public void setComponentInstaller(SdkComponentInstaller componentInstaller) {
    this.componentInstaller = componentInstaller;
  }

  public void setUpdater(SdkUpdater updater) {
    this.updater = updater;
  }

  /**
   * Download the SDK at specified version, or update existing version.
   *
   * @return the downloaded SDK's home directory.
   * @throws GradleException when installation fails.
   */
  public File downloadSdk(String version) {
    // Create ManagedCloudSdk from given version
    ManagedCloudSdk sdk;
    try {
      if (version.equals(Version.LATEST.getVersion())) {
        sdk = ManagedCloudSdk.newManagedSdk();
      } else {
        sdk = ManagedCloudSdk.newManagedSdk(new Version(version));
      }
    } catch (UnsupportedOsException | BadCloudSdkVersionException e) {
      throw new GradleException(e.getMessage());
    }

    ProgressListener progressListener =
        new ProgressListener() {
          @Override
          public void start(String message, long totalWork) {
            if (logger != null) {
              logger.lifecycle(message);
            }
          }

          @Override
          public void update(long workDone) {
            // TODO: Show progress
          }

          @Override
          public void update(String message) {
            if (logger != null) {
              logger.lifecycle(message);
            }
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

    try {
      // Install sdk if not installed
      if (!sdk.isInstalled()) {
        if (installer == null) {
          // Use managed sdk to create SdkInstaller if one isn't provided
          installer = sdk.newInstaller();
        }
        installer.install(progressListener, consoleListener);
      }

      // Install app engine component
      if (!sdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)) {
        if (componentInstaller == null) {
          componentInstaller = sdk.newComponentInstaller();
        }
        componentInstaller.installComponent(
            SdkComponent.APP_ENGINE_JAVA, progressListener, consoleListener);
      }

      // If version is set to LATEST, update Cloud SDK
      if (version.equals(Version.LATEST.getVersion()) && !sdk.isUpToDate()) {
        if (updater == null) {
          updater = sdk.newUpdater();
        }
        updater.update(progressListener, consoleListener);
      }
    } catch (IOException
        | SdkInstallerException
        | ManagedSdkVersionMismatchException
        | InterruptedException
        | CommandExecutionException
        | CommandExitException
        | ManagedSdkVerificationException e) {
      throw new GradleException(e.getMessage());
    }

    return sdk.getSdkHome().toFile();
  }

  /**
   * Validates the Cloud SDK.
   *
   * @return true if the sdk in specified home directory is valid.
   */
  public boolean isSdkValid(String version, File home) {
    CloudSdk cloudSdk = new CloudSdk.Builder().sdkPath(home.toPath()).build();
    if (!version.equals("LATEST") && !version.equals(cloudSdk.getVersion().toString())) {
      if (logger != null) {
        logger.warn(
            "Cloud SDK validate: Specified version ("
                + version.toString()
                + ") does not match installed version ("
                + cloudSdk.getVersion().toString()
                + ").");
      }
      return false;
    }

    try {
      cloudSdk.validateCloudSdk();
    } catch (CloudSdkNotFoundException
        | CloudSdkOutOfDateException
        | CloudSdkVersionFileException e) {
      if (logger != null) {
        logger.warn("Cloud SDK validate: Exception thrown. " + e.getMessage());
      }
      return false;
    }

    return true;
  }
}
