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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.cloudsdk.AppCfg;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.Gcloud;
import com.google.cloud.tools.appengine.cloudsdk.LocalRun;
import com.google.cloud.tools.appengine.cloudsdk.process.LegacyProcessHandler;
import com.google.cloud.tools.appengine.cloudsdk.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessHandler;
import com.google.cloud.tools.gradle.appengine.util.NullSafe;
import java.io.File;
import org.gradle.api.logging.Logger;

/** Cloud Sdk Operations with all common configuration. */
public class CloudSdkOperations {

  private final CloudSdk cloudSdk;
  private final Gcloud gcloud;
  private final LocalRun localRun;
  private final AppCfg appcfg;

  /**
   * Operations factory for Cloud Sdk based actions.
   *
   * @param cloudSdkHome path to cloud sdk
   * @param credentialFile optional path to a credential file
   * @throws CloudSdkNotFoundException when cloud sdk path cannot be validated
   */
  public CloudSdkOperations(File cloudSdkHome, File credentialFile)
      throws CloudSdkNotFoundException {
    cloudSdk = new CloudSdk.Builder().sdkPath(cloudSdkHome.toPath()).build();
    localRun = LocalRun.builder(cloudSdk).build();
    gcloud =
        Gcloud.builder(cloudSdk)
            .setCredentialFile(NullSafe.convert(credentialFile, File::toPath))
            .setMetricsEnvironment(
                getClass().getPackage().getImplementationTitle(),
                getClass().getPackage().getImplementationVersion())
            .build();
    appcfg = AppCfg.builder(cloudSdk).build();
  }

  public CloudSdk getCloudSdk() {
    return cloudSdk;
  }

  public Gcloud getGcloud() {
    return gcloud;
  }

  public LocalRun getLocalRun() {
    return localRun;
  }

  public AppCfg getAppcfg() {
    return appcfg;
  }

  /** Create a return a new default configured process handler. */
  public static ProcessHandler getDefaultHandler(Logger logger) {
    return LegacyProcessHandler.builder()
        .addStdErrLineListener(logger::lifecycle)
        .addStdOutLineListener(logger::lifecycle)
        .setExitListener(new NonZeroExceptionExitListener())
        .build();
  }
}
