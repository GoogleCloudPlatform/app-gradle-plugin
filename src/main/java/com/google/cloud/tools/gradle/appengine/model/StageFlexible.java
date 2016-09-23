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

import com.google.cloud.tools.appengine.api.deploy.StageFlexibleConfiguration;

import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;

/**
 * Extension element to define Stage configurations for App Engine Flexible Environments
 */
public class StageFlexible implements StageFlexibleConfiguration {

  private File appEngineDirectory;
  private File dockerDirectory;
  private File artifact;
  private File stagingDirectory;

  @Override
  @InputDirectory
  public File getAppEngineDirectory() {
    return appEngineDirectory;
  }

  public void setAppEngineDirectory(File appEngineDirectory) {
    this.appEngineDirectory = appEngineDirectory;
  }

  @Override
  @Optional
  @InputDirectory
  public File getDockerDirectory() {
    return dockerDirectory;
  }

  public void setDockerDirectory(File dockerDirectory) {
    this.dockerDirectory = dockerDirectory;
  }

  @Override
  @InputFile
  public File getArtifact() {
    return artifact;
  }

  public void setArtifact(File artifact) {
    this.artifact = artifact;
  }

  @Override
  @OutputDirectory
  public File getStagingDirectory() {
    return stagingDirectory;
  }

  public void setStagingDirectory(File stagingDirectory) {
    this.stagingDirectory = stagingDirectory;
  }
}
