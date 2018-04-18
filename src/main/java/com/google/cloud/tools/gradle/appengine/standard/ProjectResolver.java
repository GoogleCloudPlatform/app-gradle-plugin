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

  private File appengineWebXml;

  public ProjectResolver(File appengineWebXml) {
    this.appengineWebXml = appengineWebXml;
  }

  /**
   * Verifies that project/version properties are pulled correctly from build.gradle and
   * appengine-web.xml and returns the appropriate project.
   *
   * @return The project ID used by the
   */
  public String getProject(String userDefinedProject) {
    try {
      AppEngineDescriptor appengineWebXmlDoc =
          AppEngineDescriptor.parse(new FileInputStream(appengineWebXml));
      String xmlProject = appengineWebXmlDoc.getProjectId();

      // Verify that project and version are set somewhere
      if (userDefinedProject == null && xmlProject == null) {
        throw new GradleException(
            "appengine-plugin does not use gcloud global project state. Please configure the "
                + "application ID and version in your build.gradle or appengine-web.xml.");
      }

      // Check system property
      boolean readAppEngineWebXml =
          System.getProperty("deploy.read.appengine.web.xml") != null
              && System.getProperty("deploy.read.appengine.web.xml").equals("true");
      if (readAppEngineWebXml) {
        // Use properties from appengine-web.xml if not also set in build.gradle
        if (userDefinedProject != null && xmlProject != null) {
          throw new GradleException(
              "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
                  + "the project/version properties from your build.gradle, or clear the "
                  + "deploy.read.appengine.web.xml system property to read from build.gradle.");
        } else {
          if (xmlProject != null) {
            return xmlProject;
          }
        }
      } else {
        // Make sure properties are set in build.gradle
        if (userDefinedProject == null) {
          throw new GradleException(
              "appengine-plugin does not use gcloud global project state. If you would like to "
                  + "use the state from appengine-web.xml, please set the system property "
                  + "deploy.read.appengine.web.xml");
        }
      }

      return userDefinedProject;
    } catch (AppEngineException | IOException | SAXException ex) {
      throw new GradleException(ex.getMessage());
    }
  }
}
