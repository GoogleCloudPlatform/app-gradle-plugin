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

package com.google.cloud.tools.gradle.appengine.model;

import org.gradle.model.Managed;
import org.gradle.model.Unmanaged;
import org.gradle.model.internal.core.Hidden;

import com.google.cloud.tools.gradle.appengine.model.hidden.CloudSdkBuilderFactory;

import java.io.File;

/**
 * Model interface to define the location of tooling
 */
@Managed
public interface Tools {

  void setCloudSdkHome(File cloudSdkHome);
  File getCloudSdkHome();

  @Hidden
  @Unmanaged
  CloudSdkBuilderFactory getCloudSdkBuilderFactory();
  void setCloudSdkBuilderFactory(CloudSdkBuilderFactory factory);

}
