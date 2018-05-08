/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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

import com.google.cloud.tools.gradle.appengine.core.DeployTargetResolver;
import org.gradle.api.GradleException;

public class FlexibleDeployTargetResolver implements DeployTargetResolver {

  @Override
  public String getProject(String configString) {
    if (configString == null
        || configString.trim().isEmpty()
        || configString.equals(APPENGINE_CONFIG)) {
      throw new GradleException(
          "Deployment project must be defined or configured to read from system state\n"
              + "1. Set appengine.deploy.project = 'my-project-name'\n"
              + "2. Set appengine.deploy.project = '"
              + GCLOUD_CONFIG
              + "' to use project from gcloud config.\n"
              + "3. Using "
              + APPENGINE_CONFIG
              + " is not allowed for flexible environment projects");
    } else if (configString.equals(GCLOUD_CONFIG)) {
      return null;
    } else {
      return configString;
    }
  }

  @Override
  public String getVersion(String configString) {
    if (configString == null
        || configString.trim().isEmpty()
        || configString.equals(APPENGINE_CONFIG)) {
      throw new GradleException(
          "Deployment version must be defined or configured to read from system state\n"
              + "1. Set appengine.deploy.version = 'my-version'\n"
              + "2. Set appengine.deploy.version = '"
              + GCLOUD_CONFIG
              + "' to have gcloud generate a version for you.\n"
              + "3. Using "
              + APPENGINE_CONFIG
              + " is not allowed for flexible environment projects");
    } else if (configString.equals(GCLOUD_CONFIG)) {
      return null;
    } else {
      return configString;
    }
  }
}
