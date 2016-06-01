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

package com.google.cloud.tools.gradle.appengine.model.internal;

import com.google.cloud.tools.app.impl.cloudsdk.internal.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.app.impl.cloudsdk.internal.sdk.CloudSdk;

import java.io.File;

/**
 * Created by appu on 5/31/16.
 */
public class CloudSdkBuilderProvider {
  private final File cloudSdkHome;

  public CloudSdkBuilderProvider(File cloudSdkHome) {
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
