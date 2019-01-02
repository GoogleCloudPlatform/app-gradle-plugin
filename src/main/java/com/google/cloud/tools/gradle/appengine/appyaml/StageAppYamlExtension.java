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

package com.google.cloud.tools.gradle.appengine.appyaml;

import com.google.cloud.tools.appengine.configuration.AppYamlProjectStageConfiguration;
import com.google.cloud.tools.gradle.appengine.util.NullSafe;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

/** Extension element to define Stage configurations for app.yaml base projects. */
public class StageAppYamlExtension {

  private final Project project;

  private File appEngineDirectory;
  private File dockerDirectory;
  private File artifact;
  private File stagingDirectory;
  private List<File> extraFilesDirectories;

  public StageAppYamlExtension(Project project) {
    this.project = project;
  }

  @InputDirectory
  public File getAppEngineDirectory() {
    return appEngineDirectory;
  }

  public void setAppEngineDirectory(Object appEngineDirectory) {
    this.appEngineDirectory = project.file(appEngineDirectory);
  }

  @Optional
  @InputDirectory
  public File getDockerDirectory() {
    return dockerDirectory;
  }

  public void setDockerDirectory(Object dockerDirectory) {
    this.dockerDirectory = project.file(dockerDirectory);
  }

  @InputFile
  public File getArtifact() {
    return artifact;
  }

  public void setArtifact(Object artifact) {
    this.artifact = project.file(artifact);
  }

  @OutputDirectory
  public File getStagingDirectory() {
    return stagingDirectory;
  }

  public void setStagingDirectory(Object stagingDirectory) {
    this.stagingDirectory = project.file(stagingDirectory);
  }

  /** This method is purely for incremental build calculations. */
  @Optional
  @InputFiles
  private FileCollection convertExtraFilesDirectoriesToInputFiles() {
    if (extraFilesDirectories == null) {
      return null;
    }
    FileCollection files = project.files();
    for (File directory : extraFilesDirectories) {
      files = files.plus(project.fileTree(directory));
    }
    return files;
  }

  /**
   * extraFilesDirectory accessor, with {@code @InputFiles} for incremental builds configured on
   * {@link StageAppYamlExtension#convertExtraFilesDirectoriesToInputFiles()}.
   */
  public List<File> getExtraFilesDirectories() {
    return extraFilesDirectories;
  }

  public void setExtraFilesDirectories(Object extraFilesDirectories) {
    this.extraFilesDirectories = new ArrayList<>(project.files(extraFilesDirectories).getFiles());
  }

  AppYamlProjectStageConfiguration toStageArchiveConfiguration() {
    return AppYamlProjectStageConfiguration.builder(
            appEngineDirectory.toPath(), artifact.toPath(), stagingDirectory.toPath())
        .dockerDirectory(NullSafe.convert(dockerDirectory, File::toPath))
        .extraFilesDirectories(NullSafe.convert(extraFilesDirectories, File::toPath))
        .build();
  }
}
