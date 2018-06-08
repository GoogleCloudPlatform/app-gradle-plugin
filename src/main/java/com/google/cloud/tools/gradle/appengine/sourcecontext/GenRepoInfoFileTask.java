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

package com.google.cloud.tools.gradle.appengine.sourcecontext;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkGenRepoInfoFile;
import com.google.cloud.tools.appengine.cloudsdk.Gcloud;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkOperations;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

/** Generate source context information. */
public class GenRepoInfoFileTask extends DefaultTask {

  private GenRepoInfoFileExtension configuration;
  private Gcloud gcloud;

  @Nested
  public GenRepoInfoFileExtension getConfiguration() {
    return configuration;
  }

  public void setConfiguration(GenRepoInfoFileExtension configuration) {
    this.configuration = configuration;
  }

  public void setGcloud(Gcloud gcloud) {
    this.gcloud = gcloud;
  }

  /** Task entrypoint : generate source context file. */
  @TaskAction
  public void generateRepositoryInfoFile() throws AppEngineException {
    gcloud.newGenRepoInfo(CloudSdkOperations.getDefaultHandler(getLogger())).generate(configuration);
  }
}
