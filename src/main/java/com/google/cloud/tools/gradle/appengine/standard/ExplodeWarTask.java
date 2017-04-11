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

package com.google.cloud.tools.gradle.appengine.standard;

import java.io.File;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/** Expand a war. */
public class ExplodeWarTask extends DefaultTask {

  private File warFile;
  private File explodedAppDirectory;

  @InputFile
  public File getWarFile() {
    return warFile;
  }

  @OutputDirectory
  public File getExplodedAppDirectory() {
    return explodedAppDirectory;
  }

  public void setWarFile(File warFile) {
    this.warFile = warFile;
  }

  public void setExplodedAppDirectory(File explodedAppDirectory) {
    this.explodedAppDirectory = explodedAppDirectory;
  }

  /** Task entrypoint : expand the war. */
  @TaskAction
  public void explodeApp() {
    getProject().delete(explodedAppDirectory);
    getProject()
        .copy(
            new Action<CopySpec>() {
              @Override
              public void execute(CopySpec copySpec) {
                copySpec.from(getProject().zipTree(warFile));
                copySpec.into(explodedAppDirectory);
              }
            });
  }
}
