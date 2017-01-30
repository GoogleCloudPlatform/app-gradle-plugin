/*
 * Copyright (c) 2016 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine.core.extension;

import com.google.cloud.tools.appengine.api.deploy.DeployConfiguration;

import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension element to define Deployable configurations for App Engine
 */
public class Deploy implements DeployConfiguration {

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

  public Deploy(Project gradleProject) {
    this.gradleProject = gradleProject;
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
}
