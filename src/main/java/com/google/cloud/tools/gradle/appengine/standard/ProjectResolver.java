/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.AppEngineDescriptor;
import com.google.cloud.tools.appengine.api.AppEngineException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.xml.sax.SAXException;

public class ProjectResolver {

  private AppEngineDescriptor appEngineDescriptor;

  ProjectResolver(File appengineWebXml) {
    try (FileInputStream stream = new FileInputStream(appengineWebXml)) {
      appEngineDescriptor = AppEngineDescriptor.parse(stream);
    } catch (IOException | SAXException ex) {
      throw new GradleException(ex.getMessage());
    }
  }

  /**
   * Verifies that the project property is pulled correctly from build.gradle and appengine-web.xml.
   *
   * @return The appropriate project ID
   * @throws GradleException if the property is missing, if the property is set in the build config
   *     and the "deploy.read.appengine.web.xml" system property is set to true, or if the system
   *     property is not set but the property only exists in appengine-web.xml
   */
  public String getProject(String userDefinedProject) {
    try {
      // Verify that project is set somewhere
      if (userDefinedProject == null && appEngineDescriptor.getProjectId() == null) {
        throw new GradleException(
            "appengine-plugin does not use gcloud global project state. Please configure the "
                + "application ID in your build.gradle or appengine-web.xml.");
      }
      return validatedProperty(userDefinedProject, appEngineDescriptor.getProjectId());
    } catch (AppEngineException ex) {
      throw new GradleException(ex.getMessage());
    }
  }

  /**
   * Verifies that the version property is pulled correctly from build.gradle and appengine-web.xml.
   *
   * @return The appropriate version
   * @throws GradleException if the property is set in the build config and the
   *     "deploy.read.appengine.web.xml" system property is set to true, or if the system property
   *     is not set but the property only exists in appengine-web.xml
   */
  public String getVersion(String userDefinedVersion) {
    try {
      return validatedProperty(userDefinedVersion, appEngineDescriptor.getProjectVersion());
    } catch (AppEngineException ex) {
      throw new GradleException(ex.getMessage());
    }
  }

  private String validatedProperty(String buildProperty, String xmlProperty) {
    // Determine whether or not to read from appengine-web.xml using system property
    boolean readAppEngineWebXml =
        System.getProperty("deploy.read.appengine.web.xml") != null
            && System.getProperty("deploy.read.appengine.web.xml").equals("true");
    if (readAppEngineWebXml && buildProperty != null) {
      // Should be reading from appengine-web.xml, but property is configured in build.gradle
      throw new GradleException(
          "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
              + "the project/version properties from your build.gradle, or clear the "
              + "deploy.read.appengine.web.xml system property to read from build.gradle.");
    } else if (!readAppEngineWebXml && buildProperty == null && xmlProperty != null) {
      // Should be reading from build file, but it's only configured in appengine-web.xml
      throw new GradleException(
          "appengine-plugin does not use gcloud global project state. If you would like to "
              + "use the state from appengine-web.xml, please set the system property "
              + "deploy.read.appengine.web.xml.");
    }

    return buildProperty == null ? xmlProperty : buildProperty;
  }
}
