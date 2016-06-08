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

import com.google.cloud.tools.appengine.api.deploy.StageStandardConfiguration;

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
  Boolean getEnableQuickstart();
  void setEnableQuickstart(Boolean enableQuickstart);

  @Override
  @Input
  @Optional
  Boolean getDisableUpdateCheck();
  void setDisableUpdateCheck(Boolean disableUpdateCheck);

  @Override
  @Input
  @Optional
  Boolean getEnableJarSplitting();
  void setEnableJarSplitting(Boolean enableJarSplitting);

  @Override
  @Input
  @Optional
  Boolean getDeleteJsps();
  void setDeleteJsps(Boolean deleteJsps);

  @Override
  @Input
  @Optional
  Boolean getEnableJarClasses();
  void setEnableJarClasses(Boolean enableJarClasses);

  @Override
  @Input
  @Optional
  Boolean getDisableJarJsps();
  void setDisableJarJsps(Boolean disableJarJsps);

}
