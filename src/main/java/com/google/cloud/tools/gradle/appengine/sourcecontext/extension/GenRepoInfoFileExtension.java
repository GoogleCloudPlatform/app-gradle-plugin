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

package com.google.cloud.tools.gradle.appengine.sourcecontext.extension;

import com.google.cloud.tools.appengine.api.debug.GenRepoInfoFileConfiguration;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;

/** Extension element to define Source Context configurations. */
public class GenRepoInfoFileExtension implements GenRepoInfoFileConfiguration {

  private final Project project;

  private final File outputDirectory;
  private File sourceDirectory;

  /** Constructor. */
  public GenRepoInfoFileExtension(Project project) {
    this.project = project;
    outputDirectory = new File(project.getBuildDir(), "sourceContext");
    sourceDirectory = new File(project.getProjectDir(), "src");
  }

  @OutputDirectory
  @Override
  public File getOutputDirectory() {
    return outputDirectory;
  }

  @InputDirectory
  @Override
  public File getSourceDirectory() {
    return sourceDirectory;
  }

  public void setSourceDirectory(Object sourceDirectory) {
    this.sourceDirectory = project.file(sourceDirectory);
  }
}
