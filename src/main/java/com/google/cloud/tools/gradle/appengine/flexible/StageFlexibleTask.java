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

package com.google.cloud.tools.gradle.appengine.flexible;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.deploy.AppEngineArchiveStaging;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkAppEngineArchiveStaging;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

/** Stage App Engine Flexible Environment applications for deployment. */
public class StageFlexibleTask extends DefaultTask {

  private StageFlexibleExtension flexibleExtension;

  @Nested
  public StageFlexibleExtension getStagingExtension() {
    return flexibleExtension;
  }

  public void setStagingConfig(StageFlexibleExtension stagingConfig) {
    this.flexibleExtension = stagingConfig;
  }

  /** Task entrypoint : Stage the flexible application. */
  @TaskAction
  public void stageAction() throws AppEngineException {
    getProject().delete(flexibleExtension.getStagingDirectory());
    getProject().mkdir(flexibleExtension.getStagingDirectory().getAbsolutePath());

    AppEngineArchiveStaging staging = new CloudSdkAppEngineArchiveStaging();
    staging.stageArchive(flexibleExtension.toStageArchiveConfiguration());
  }
}
