/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
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

import java.io.File;
import org.gradle.api.Project;

/** Extension element to define the location of cloud sdk tooling. */
public class ToolsExtension {

  private final Project project;

  private File cloudSdkHome;

  public ToolsExtension(Project project) {
    this.project = project;
  }

  public File getCloudSdkHome() {
    return cloudSdkHome;
  }

  public void setCloudSdkHome(Object cloudSdkHome) {
    this.cloudSdkHome = project.file(cloudSdkHome);
  }
}
