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

import com.google.cloud.tools.appengine.configuration.DeployConfiguration;
import com.google.cloud.tools.appengine.configuration.DeployProjectConfigurationConfiguration;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

/** Extension element to define Deployable configurations for App Engine. */
public class DeployExtension {

  @InternalProperty private DeployTargetResolver deployTargetResolver;

  // named gradleProject to disambiguate with deploy parameter "project"
  private final Project gradleProject;

  private String bucket;
  private String imageUrl;
  private String projectId;
  @Deprecated private String project;
  private Boolean promote;
  private String server;
  private Boolean stopPreviousVersion;
  private String version;
  private File appEngineDirectory;

  public DeployExtension(Project gradleProject) {
    this.gradleProject = gradleProject;
  }

  public void setDeployTargetResolver(DeployTargetResolver deployTargetResolver) {
    this.deployTargetResolver = deployTargetResolver;
  }

  public DeployTargetResolver getDeployTargetResolver() {
    return deployTargetResolver;
  }

  DeployConfiguration toDeployConfiguration(List<Path> deployables) {
    String processedProjectId = deployTargetResolver.getProject(projectId);
    String processedVersion = deployTargetResolver.getVersion(version);
    return DeployConfiguration.builder(deployables)
        .bucket(bucket)
        .imageUrl(imageUrl)
        .projectId(processedProjectId)
        .promote(promote)
        .server(server)
        .stopPreviousVersion(stopPreviousVersion)
        .version(processedVersion)
        .build();
  }

  DeployProjectConfigurationConfiguration toDeployProjectConfigurationConfiguration() {
    String processedProjectId = deployTargetResolver.getProject(projectId);
    return DeployProjectConfigurationConfiguration.builder(appEngineDirectory.toPath())
        .projectId(processedProjectId)
        .server(server)
        .build();
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public Boolean getPromote() {
    return promote;
  }

  public void setPromote(Boolean promote) {
    this.promote = promote;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public Boolean getStopPreviousVersion() {
    return stopPreviousVersion;
  }

  public void setStopPreviousVersion(Boolean stopPreviousVersion) {
    this.stopPreviousVersion = stopPreviousVersion;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setAppEngineDirectory(Object appEngineDirectory) {
    this.appEngineDirectory = gradleProject.file(appEngineDirectory);
  }

  public File getAppEngineDirectory() {
    return appEngineDirectory;
  }

  public String getProject() {
    throw new GradleException(
        "Use of appengine.deploy.project is deprecated, use appengine.deploy.projectId");
  }

  public void setProject(String project) {
    throw new GradleException(
        "Use of appengine.deploy.project is deprecated, use appengine.deploy.projectId");
  }
}
