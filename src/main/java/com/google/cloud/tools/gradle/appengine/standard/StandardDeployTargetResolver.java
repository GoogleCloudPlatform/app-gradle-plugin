/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

import static com.google.cloud.tools.gradle.appengine.core.ConfigReader.APPENGINE_CONFIG;
import static com.google.cloud.tools.gradle.appengine.core.ConfigReader.GCLOUD_CONFIG;

import com.google.cloud.tools.appengine.cloudsdk.Gcloud;
import com.google.cloud.tools.gradle.appengine.core.ConfigReader;
import java.io.File;
import org.gradle.api.GradleException;

public class StandardDeployTargetResolver {

  static final String PROJECT_ERROR =
      "Deployment projectId must be defined or configured to read from system state\n"
          + "1. Set appengine.deploy.projectId = 'my-project-id'\n"
          + "2. Set appengine.deploy.projectId = '"
          + APPENGINE_CONFIG
          + "' to use <application> from appengine-web.xml\n"
          + "3. Set appengine.deploy.projectId = '"
          + GCLOUD_CONFIG
          + "' to use project from gcloud config";

  static final String VERSION_ERROR =
      "Deployment version must be defined or configured to read from system state\n"
          + "1. Set appengine.deploy.version = 'my-version'\n"
          + "2. Set appengine.deploy.version = '"
          + APPENGINE_CONFIG
          + "' to use <version> from appengine-web.xml\n"
          + "3. Set appengine.deploy.version = '"
          + GCLOUD_CONFIG
          + "' to have gcloud generate a version for you.";

  private final File appengineWebXml;
  private final Gcloud gcloud;

  public StandardDeployTargetResolver(File appengineWebXml, Gcloud gcloud) {
    this.appengineWebXml = appengineWebXml;
    this.gcloud = gcloud;
  }

  /**
   * Process user configuration of "projectId". If not configured, show usage. If set to
   * APPENGINE_CONFIG then read from appengine-web.xml. If set to GCLOUD_CONFIG then read from
   * gcloud's config state. If set but not a keyword then just return the set value.
   */
  public String getProject(String configString) {
    if (configString == null || configString.trim().isEmpty()) {
      throw new GradleException(PROJECT_ERROR);
    } else if (configString.equals(APPENGINE_CONFIG)) {
      return ConfigReader.getProject(appengineWebXml);
    } else if (configString.equals(GCLOUD_CONFIG)) {
      return ConfigReader.getProject(gcloud);
    } else {
      return configString;
    }
  }

  /**
   * Process user configuration of "version". If not configured, show usage. If set to
   * APPENGINE_CONFIG then read from appengine-web.xml. If set to GCLOUD_CONFIG then allow gcloud to
   * generate a version. If set but not a keyword then just return the set value.
   */
  public String getVersion(String configString) {
    if (configString == null || configString.trim().isEmpty()) {
      throw new GradleException(VERSION_ERROR);
    } else if (configString.equals(APPENGINE_CONFIG)) {
      return ConfigReader.getVersion(appengineWebXml);
    } else if (configString.equals(GCLOUD_CONFIG)) {
      // can be null to allow gcloud to generate this
      return null;
    } else {
      return configString;
    }
  }
}
