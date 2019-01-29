/*
 * Copyright 2019 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandlerException;
import java.io.IOException;
import org.gradle.api.GradleException;

/** Used for processing user configured project/version when generating config objects. */
public class DeployTargetResolver {
  static final String GCLOUD_CONFIG = "GCLOUD_CONFIG";
  static final String APPENGINE_CONFIG = "APPENGINE_CONFIG";

  static final String PROJECT_ERROR =
      "Deployment projectId must be defined or configured to read from system state\n"
          + "1. Set appengine.deploy.projectId = 'my-project-id'\n"
          + "2. Set appengine.deploy.projectId = '"
          + GCLOUD_CONFIG
          + "' to use project from gcloud config.\n"
          + "3. Using '"
          + APPENGINE_CONFIG
          + "' has been deprecated.";

  static final String VERSION_ERROR =
      "Deployment version must be defined or configured to read from system state\n"
          + "1. Set appengine.deploy.version = 'my-version'\n"
          + "2. Set appengine.deploy.version = '"
          + GCLOUD_CONFIG
          + "' to have gcloud generate a version for you.\n"
          + "3. Using '"
          + APPENGINE_CONFIG
          + "' has been deprecated";

  private final CloudSdkOperations cloudSdkOperations;

  DeployTargetResolver(CloudSdkOperations cloudSdkOperations) {
    this.cloudSdkOperations = cloudSdkOperations;
  }

  /**
   * Process user configuration of "projectId". If set to GCLOUD_CONFIG then read from gcloud's
   * global state. If set but not a keyword then just return the set value.
   */
  public String getProject(String configString) {
    if (configString == null
        || configString.trim().isEmpty()
        || configString.equals(APPENGINE_CONFIG)) {
      throw new GradleException(PROJECT_ERROR);
    }
    if (configString.equals(GCLOUD_CONFIG)) {
      try {
        String gcloudProject = cloudSdkOperations.getGcloud().getConfig().getProject();
        if (gcloudProject == null || gcloudProject.trim().isEmpty()) {
          throw new GradleException("Project was not found in gcloud config");
        }
        return gcloudProject;
      } catch (IOException
          | CloudSdkOutOfDateException
          | ProcessHandlerException
          | CloudSdkNotFoundException
          | CloudSdkVersionFileException ex) {
        throw new GradleException("Failed to read project from gcloud config", ex);
      }
    }
    return configString;
  }

  /**
   * Process user configuration of "version". If set to GCLOUD_CONFIG then allow gcloud to generate
   * a version. If set but not a keyword then just return the set value.
   */
  public String getVersion(String configString) {
    if (configString == null
        || configString.trim().isEmpty()
        || configString.equals(APPENGINE_CONFIG)) {
      throw new GradleException(VERSION_ERROR);
    }
    if (configString.equals(GCLOUD_CONFIG)) {
      // can be null to allow gcloud to generate this
      return null;
    }
    return configString;
  }
}
