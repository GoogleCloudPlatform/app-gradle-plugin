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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.AppEngineDescriptor;
import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.gradle.appengine.core.DeployTargetResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.xml.sax.SAXException;

public class StandardDeployTargetResolver implements DeployTargetResolver {

  private final File appengineWebXml;

  public StandardDeployTargetResolver(File appengineWebXml) {
    this.appengineWebXml = appengineWebXml;
  }

  @Override
  public String getProject(String configString) {
    if (configString == null || configString.trim().isEmpty()) {
      throw new GradleException(
          "Deployment projectId must be defined or configured to read from system state\n"
              + "1. Set appengine.deploy.projectId = 'my-project-id'\n"
              + "2. Set appengine.deploy.projectId = '"
              + APPENGINE_CONFIG
              + "' to use <application> from appengine-web.xml\n"
              + "3. Set appengine.deploy.projectId = '"
              + GCLOUD_CONFIG
              + "' to use project from gcloud config");
    } else if (configString.equals(APPENGINE_CONFIG)) {
      try {
        AppEngineDescriptor appEngineDescriptor =
            AppEngineDescriptor.parse(new FileInputStream(appengineWebXml));
        String appengineWebXmlProject = appEngineDescriptor.getProjectId();
        if (appengineWebXmlProject == null || appengineWebXmlProject.trim().isEmpty()) {
          throw new GradleException("<application> was not found in appengine-web.xml");
        }
        return appengineWebXmlProject;
      } catch (IOException | SAXException | AppEngineException ex) {
        throw new GradleException("Failed to read project from appengine-web.xml", ex);
      }
    } else if (configString.equals(GCLOUD_CONFIG)) {
      return null;
    } else {
      return configString;
    }
  }

  @Override
  public String getVersion(String configString) {
    if (configString == null || configString.trim().isEmpty()) {
      throw new GradleException(
          "Deployment version must be defined or configured to read from system state\n"
              + "1. Set appengine.deploy.version = 'my-version'\n"
              + "2. Set appengine.deploy.version = '"
              + APPENGINE_CONFIG
              + "' to use <version> from appengine-web.xml\n"
              + "3. Set appengine.deploy.version = '"
              + GCLOUD_CONFIG
              + "' to have gcloud generate a version for you.");
    } else if (configString.equals(APPENGINE_CONFIG)) {
      try {
        AppEngineDescriptor appEngineDescriptor =
            AppEngineDescriptor.parse(new FileInputStream(appengineWebXml));
        String appengineWebXmlVersion = appEngineDescriptor.getProjectVersion();
        if (appengineWebXmlVersion == null || appengineWebXmlVersion.trim().isEmpty()) {
          throw new GradleException("<version> was not found in appengine-web.xml");
        }
        return appengineWebXmlVersion;
      } catch (IOException | SAXException | AppEngineException ex) {
        throw new GradleException("Failed to read version from appengine-web.xml", ex);
      }
    } else if (configString.equals(GCLOUD_CONFIG)) {
      return null;
    } else {
      return configString;
    }
  }
}
