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

import com.google.cloud.tools.appengine.api.deploy.DeployConfiguration;
import com.google.cloud.tools.appengine.api.deploy.DeployProjectConfigurationConfiguration;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.List;
import org.gradle.api.Project;

/** Extension element to define Deployable configurations for App Engine. */
public class DeployExtension
    implements DeployConfiguration, DeployProjectConfigurationConfiguration {

  // named gradleProject to disambiguate with deploy parameter "project"
  private final Project gradleProject;

  private String bucket;
  private String imageUrl;
  private String project;
  private Boolean promote;
  private String server;
  private Boolean stopPreviousVersion;
  private String version;
  private File appEngineDirectory;

  @InternalProperty private final ImmutableList<File> deployables;
  @InternalProperty private DeployTargetResolver deployTargetResolver;

  public DeployExtension(Project gradleProject) {
    this.gradleProject = gradleProject;
    this.deployables = ImmutableList.of();
  }

  /** Creates and returns a copy of the DeployExtension with specified deployables. */
  public DeployExtension(DeployExtension deployExtension, List<File> deployables) {
    this.gradleProject = deployExtension.gradleProject;
    this.bucket = deployExtension.bucket;
    this.imageUrl = deployExtension.imageUrl;
    this.project = deployExtension.project;
    this.promote = deployExtension.promote;
    this.server = deployExtension.server;
    this.stopPreviousVersion = deployExtension.stopPreviousVersion;
    this.version = deployExtension.version;
    this.appEngineDirectory = deployExtension.appEngineDirectory;
    this.deployTargetResolver = deployExtension.deployTargetResolver;
    this.deployables = ImmutableList.copyOf(deployables);
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

  @Override
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String getProject() {
    return deployTargetResolver.getProject(project);
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
    return deployTargetResolver.getVersion(version);
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

  public void setDeployTargetResolver(DeployTargetResolver deployTargetResolver) {
    this.deployTargetResolver = deployTargetResolver;
  }
}
