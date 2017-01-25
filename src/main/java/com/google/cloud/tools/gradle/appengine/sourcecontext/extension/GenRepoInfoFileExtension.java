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

import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;

/**
 * Extension element to define Source Context configurations.
 */
public class GenRepoInfoFileExtension implements GenRepoInfoFileConfiguration {

  private final File outputDirectory;
  private File sourceDirectory;

  public GenRepoInfoFileExtension(File buildDir, File sourceRoot) {
    outputDirectory = new File(buildDir, "sourceContext");
    sourceDirectory = sourceRoot;
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

  public void setSourceDirectory(File sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }
}
