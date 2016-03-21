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

import com.google.cloud.tools.app.api.deploy.StageStandardConfiguration;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.model.Managed;

import java.io.File;

/**
 * Model element to define Stage configurations for App Engine Standard Environments
 */
@Managed
public interface StageStandardModel extends StageStandardConfiguration {

  @Override
  @Input
  @Optional
  String getJarSplittingExcludes();
  void setJarSplittingExcludes(String jarSplittingExludes);

  @Override
  @Input
  @Optional
  String getCompileEncoding();
  void setCompileEncoding(String compileEncoding);

  @Override
  @InputDirectory
  File getSourceDirectory();
  void setSourceDirectory(File sourceDirectory);

  @Override
  @OutputDirectory
  File getStagingDirectory();
  void setStagingDirectory(File stagingDirectory);

  @Override
  @InputFile
  @Optional
  File getDockerfile();
  void setDockerfile(File dockerfile);

  @Override
  @Input
  @Optional
  boolean isEnableQuickstart();
  void setEnableQuickstart(boolean enableQuickstart);

  @Override
  @Input
  @Optional
  boolean isDisableUpdateCheck();
  void setDisableUpdateCheck(boolean disableUpdateCheck);

  @Override
  @Input
  @Optional
  String getVersion();
  void setVersion(String version);

  @Override
  @Input
  @Optional
  boolean isEnableJarSplitting();
  void setEnableJarSplitting(boolean enableJarSplitting);

  @Override
  @Input
  @Optional
  boolean isDeleteJsps();
  void setDeleteJsps(boolean deleteJsps);

  @Override
  @Input
  @Optional
  boolean isEnableJarClasses();
  void setEnableJarClasses(boolean enableJarClasses);
}
