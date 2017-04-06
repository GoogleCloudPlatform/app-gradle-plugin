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

package com.google.cloud.tools.gradle.appengine.flexible.extension;

import com.google.cloud.tools.appengine.api.deploy.StageFlexibleConfiguration;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

/** Extension element to define Stage configurations for App Engine Flexible Environments. */
public class StageFlexible implements StageFlexibleConfiguration {

  private final Project project;

  private File appEngineDirectory;
  private File dockerDirectory;
  private File artifact;
  private File stagingDirectory;

  /** Constructor. */
  public StageFlexible(Project project, File stagingDirectory) {
    this.project = project;
    File projectDir = project.getProjectDir();

    this.stagingDirectory = stagingDirectory;
    this.appEngineDirectory = new File(projectDir, "src/main/appengine");
    File dockerOptionalDir = new File(projectDir, "src/main/docker");
    if (dockerOptionalDir.exists()) {
      dockerDirectory = dockerOptionalDir;
    }
  }

  @Override
  @InputDirectory
  public File getAppEngineDirectory() {
    return appEngineDirectory;
  }

  public void setAppEngineDirectory(Object appEngineDirectory) {
    this.appEngineDirectory = project.file(appEngineDirectory);
  }

  @Override
  @Optional
  @InputDirectory
  public File getDockerDirectory() {
    return dockerDirectory;
  }

  public void setDockerDirectory(Object dockerDirectory) {
    this.dockerDirectory = project.file(dockerDirectory);
  }

  @Override
  @InputFile
  public File getArtifact() {
    return artifact;
  }

  public void setArtifact(Object artifact) {
    this.artifact = project.file(artifact);
  }

  @Override
  @OutputDirectory
  public File getStagingDirectory() {
    return stagingDirectory;
  }

  public void setStagingDirectory(Object stagingDirectory) {
    this.stagingDirectory = project.file(stagingDirectory);
  }
}
