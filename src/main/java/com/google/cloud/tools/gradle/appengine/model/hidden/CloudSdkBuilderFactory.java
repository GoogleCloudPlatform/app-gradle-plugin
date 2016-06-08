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

package com.google.cloud.tools.gradle.appengine.model.hidden;

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.process.NonZeroExceptionExitListener;

import java.io.File;

/**
 * Factory for generating Cloud Sdk Builder with all common configuration
 */
public class CloudSdkBuilderFactory {
  private final File cloudSdkHome;

  public CloudSdkBuilderFactory(File cloudSdkHome) {
    this.cloudSdkHome = cloudSdkHome;
  }

  public CloudSdk.Builder newBuilder() {
    return new CloudSdk.Builder()
        .sdkPath(cloudSdkHome)
        .exitListener(new NonZeroExceptionExitListener())
        .appCommandMetricsEnvironment(getClass().getPackage().getImplementationTitle())
        .appCommandMetricsEnvironmentVersion(getClass().getPackage().getImplementationVersion());
  }
}
