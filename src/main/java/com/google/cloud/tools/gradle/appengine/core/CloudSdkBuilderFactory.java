/*
 * Copyright 2016 Google LLC. All Right Reserved.
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
import com.google.cloud.tools.appengine.cloudsdk.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessOutputLineListener;
import com.google.cloud.tools.gradle.appengine.util.io.GradleLoggerOutputListener;
import java.io.File;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;

/** Factory for generating Cloud Sdk Builder with all common configuration. */
public class CloudSdkBuilderFactory {

  private File cloudSdkHome;

  public CloudSdkBuilderFactory(File cloudSdkHome) {
    this.cloudSdkHome = cloudSdkHome;
  }

  public void setCloudSdkHome(File cloudSdkHome) {
    this.cloudSdkHome = cloudSdkHome;
  }

  /** Create a empty builder with auto-configured metrics and failure on non-zero exit. */
  public CloudSdk.Builder newBuilder() {
    return new CloudSdk.Builder()
        .sdkPath(cloudSdkHome != null ? cloudSdkHome.toPath() : null)
        .exitListener(new NonZeroExceptionExitListener())
        .appCommandMetricsEnvironment(getClass().getPackage().getImplementationTitle())
        .appCommandMetricsEnvironmentVersion(getClass().getPackage().getImplementationVersion());
  }

  /**
   * Create a builder with auto-configured metrics, output handlers and failure on non-zero exit.
   */
  public CloudSdk.Builder newBuilder(Logger logger) {
    ProcessOutputLineListener listener = new GradleLoggerOutputListener(logger, LogLevel.LIFECYCLE);

    return new CloudSdk.Builder()
        .sdkPath(cloudSdkHome != null ? cloudSdkHome.toPath() : null)
        .exitListener(new NonZeroExceptionExitListener())
        .appCommandMetricsEnvironment(getClass().getPackage().getImplementationTitle())
        .appCommandMetricsEnvironmentVersion(getClass().getPackage().getImplementationVersion())
        .addStdOutLineListener(listener)
        .addStdErrLineListener(listener);
  }
}
