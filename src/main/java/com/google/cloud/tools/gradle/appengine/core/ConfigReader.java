/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

import com.google.cloud.tools.appengine.AppEngineDescriptor;
import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.cloudsdk.Gcloud;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessHandlerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.xml.sax.SAXException;

public final class ConfigReader {
  public static final String GCLOUD_CONFIG = "GCLOUD_CONFIG";
  public static final String APPENGINE_CONFIG = "APPENGINE_CONFIG";

  private ConfigReader() {}

  /** Return gcloud config property for project, or error out if not found. */
  public static String getProject(Gcloud gcloud) {
    try {
      String gcloudProject = gcloud.getConfig().getProject();
      if (gcloudProject == null || gcloudProject.trim().isEmpty()) {
        throw new GradleException("Project was not found in gcloud config");
      }
      return gcloudProject;
    } catch (CloudSdkNotFoundException
        | CloudSdkOutOfDateException
        | CloudSdkVersionFileException
        | IOException
        | ProcessHandlerException ex) {
      throw new GradleException("Failed to read project from gcloud config", ex);
    }
  }

  /** Return "application" tag from appengine-web.xml or error out if could not read. */
  public static String getProject(File appengineWebXml) {
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
  }

  /** Return "version" tag from appengine-web.xml or error out if could not read. */
  public static String getVersion(File appengineWebXml) {
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
  }
}
