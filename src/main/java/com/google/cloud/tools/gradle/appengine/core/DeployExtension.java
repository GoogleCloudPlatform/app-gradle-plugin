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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.AppEngineDescriptor;
import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.deploy.DeployConfiguration;
import com.google.cloud.tools.appengine.api.deploy.DeployProjectConfigurationConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.xml.sax.SAXException;

/** Extension element to define Deployable configurations for App Engine. */
public class DeployExtension
    implements DeployConfiguration, DeployProjectConfigurationConfiguration {

  // named gradleProject to disambiguate with deploy parameter "project"
  private final Project gradleProject;

  private String bucket;
  private List<File> deployables;
  private String imageUrl;
  private String project;
  private Boolean promote;
  private String server;
  private Boolean stopPreviousVersion;
  private String version;
  private File appEngineDirectory;
  private File appEngineWebXml;

  public DeployExtension(Project gradleProject) {
    this.gradleProject = gradleProject;
  }

  /** Creates and return a deep copy of the DeployExtension. */
  public DeployExtension(DeployExtension deployExtension) {
    this.gradleProject = deployExtension.gradleProject;
    this.bucket = deployExtension.bucket;
    this.deployables =
        deployExtension.deployables == null ? null : new ArrayList<>(deployExtension.deployables);
    this.imageUrl = deployExtension.imageUrl;
    this.project = deployExtension.project;
    this.promote = deployExtension.promote;
    this.server = deployExtension.server;
    this.stopPreviousVersion = deployExtension.stopPreviousVersion;
    this.version = deployExtension.version;
    this.appEngineDirectory = deployExtension.appEngineDirectory;
    this.appEngineWebXml = deployExtension.appEngineWebXml;
  }

  /**
   * Verifies that project/version properties are pulled correctly from build.gradle and
   * appengine-web.xml and modifies the properties appropriately.
   *
   * @return A copy of the DeployExtension with the corrected properties.
   */
  public DeployExtension withPropertiesFromAppEngineWebXml()
      throws AppEngineException, IOException, SAXException {
    DeployExtension deployExtensionCopy = new DeployExtension(this);

    if (appEngineWebXml == null) {
      return deployExtensionCopy;
    }

    AppEngineDescriptor appengineWebXmlDoc =
        AppEngineDescriptor.parse(new FileInputStream(appEngineWebXml));
    String xmlProject = appengineWebXmlDoc.getProjectId();
    String xmlVersion = appengineWebXmlDoc.getProjectVersion();

    // Verify that project and version are set somewhere
    if (project == null && xmlProject == null || version == null && xmlVersion == null) {
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
      if (project != null && xmlProject != null || version != null && xmlVersion != null) {
        throw new GradleException(
            "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
                + "the project/version properties from your build.gradle, or clear the "
                + "deploy.read.appengine.web.xml system property to read from build.gradle.");
      } else {
        if (xmlProject != null) {
          deployExtensionCopy.setProject(xmlProject);
        }
        if (xmlVersion != null) {
          deployExtensionCopy.setVersion(xmlVersion);
        }
      }
    } else {
      // Make sure properties are set in build.gradle
      if (project == null || version == null) {
        throw new GradleException(
            "appengine-plugin does not use gcloud global project state. If you would like to "
                + "use the state from appengine-web.xml, please set the system property "
                + "deploy.read.appengine.web.xml");
      }
    }

    return deployExtensionCopy;
  }

  @Override
  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  @Override
  public List<File> getDeployables() {
    return deployables;
  }

  public void setDeployables(Object deployables) {
    this.deployables = new ArrayList<>(gradleProject.files(deployables).getFiles());
  }

  @Override
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  @Override
  public Boolean getPromote() {
    return promote;
  }

  public void setPromote(Boolean promote) {
    this.promote = promote;
  }

  @Override
  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  @Override
  public Boolean getStopPreviousVersion() {
    return stopPreviousVersion;
  }

  public void setStopPreviousVersion(Boolean stopPreviousVersion) {
    this.stopPreviousVersion = stopPreviousVersion;
  }

  @Override
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setAppEngineDirectory(Object appEngineDirectory) {
    this.appEngineDirectory = gradleProject.file(appEngineDirectory);
  }

  @Override
  public File getAppEngineDirectory() {
    return appEngineDirectory;
  }

  public void setAppEngineWebXml(File appEngineWebXml) {
    this.appEngineWebXml = appEngineWebXml;
  }
}
