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

package com.google.cloud.tools.gradle.appengine.standard;

import java.io.File;
import org.gradle.api.tasks.Sync;

/** Expand a war. */
public class ExplodeWarTask extends Sync {

  private File explodedAppDirectory;

  public void setWarFile(File warFile) {
    from(getProject().zipTree(warFile));
  }

  /**
   * Sets the output directory of Sync Task and preserves the setting so it can be recovered later
   * via getter.
   */
  public void setExplodedAppDirectory(File explodedAppDirectory) {
    this.explodedAppDirectory = explodedAppDirectory;
    into(explodedAppDirectory);
    preserve(
        patternFilterable ->
            patternFilterable.include("WEB-INF/appengine-generated/datastore-indexes-auto.xml"));
  }

  public File getExplodedAppDirectory() {
    return explodedAppDirectory;
  }
}
