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

package com.google.cloud.tools.gradle.appengine.sourcecontext.task;

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkGenRepoInfoFile;
import com.google.cloud.tools.gradle.appengine.core.task.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.sourcecontext.extension.GenRepoInfoFileExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

/** Generate source context information. */
public class GenRepoInfoFileTask extends DefaultTask {

  private GenRepoInfoFileExtension configuration;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Nested
  public GenRepoInfoFileExtension getConfiguration() {
    return configuration;
  }

  public void setConfiguration(GenRepoInfoFileExtension configuration) {
    this.configuration = configuration;
  }

  public void setCloudSdkBuilderFactory(CloudSdkBuilderFactory cloudSdkBuilderFactory) {
    this.cloudSdkBuilderFactory = cloudSdkBuilderFactory;
  }

  /** Task entrypoint : generate source context file. */
  @TaskAction
  public void generateRepositoryInfoFile() {
    CloudSdk sdk = cloudSdkBuilderFactory.newBuilder(getLogger()).build();
    CloudSdkGenRepoInfoFile generator = new CloudSdkGenRepoInfoFile(sdk);
    generator.generate(configuration);
  }
}
