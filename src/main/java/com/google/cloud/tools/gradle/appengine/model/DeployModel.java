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

package com.google.cloud.tools.gradle.appengine.model;

import com.google.cloud.tools.app.api.deploy.DeployConfiguration;

import org.gradle.model.Managed;

import java.io.File;
import java.util.List;

/**
 * Model element to define Deployable configurations for App Engine
 */
@Managed
public interface DeployModel extends DeployConfiguration {

  @Override
  String getDockerBuild();
  void setDockerBuild(String dockerBuild);

  @Override
  String getImageUrl();

  void setImageUrl(String imageUrl);

  @Override
  String getVersion();
  void setVersion(String version);

  @Override
  List<File> getDeployables();
  void setDeployables(List<File> deployables);

  @Override
  String getBucket();
  void setBucket(String bucket);

  @Override
  boolean isForce();
  void setForce(boolean force);

  @Override
  String getProject();
  void setProject(String project);

  @Override
  boolean isPromote();
  void setPromote(boolean promote);

  @Override
  String getServer();
  void setServer(String server);

  @Override
  boolean isStopPreviousVersion();
  void setStopPreviousVersion(boolean stopPreviousVersion);
}
