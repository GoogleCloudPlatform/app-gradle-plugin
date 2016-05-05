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

import com.google.cloud.tools.app.api.devserver.RunConfiguration;
import com.google.cloud.tools.app.api.devserver.StopConfiguration;

import org.gradle.model.Managed;

import java.io.File;
import java.util.List;

/**
 * Model element to define Run configurations for App Engine Standard Environments
 */
@Managed
public interface RunModel extends RunConfiguration, StopConfiguration {

  @Override
  List<File> getAppYamls();
  void setAppYamls(List<File> appYamls);

  @Override
  String getHost();
  void setHost(String host);

  @Override
  Integer getPort();
  void setPort(Integer port);

  @Override
  String getAdminHost();
  void setAdminHost(String adminHost);

  @Override
  Integer getAdminPort();
  void setAdminPort(Integer adminPort);

  @Override
  String getAuthDomain();
  void setAuthDomain(String authDomain);

  @Override
  String getStoragePath();
  void setStoragePath(String storagePath);

  @Override
  String getLogLevel();
  void setLogLevel(String logLevel);

  @Override
  Integer getMaxModuleInstances();
  void setMaxModuleInstances(Integer maxModuleInstances);

  @Override
  Boolean getUseMtimeFileWatcher();
  void setUseMtimeFileWatcher(Boolean useMtimeFileWatcher);

  @Override
  String getThreadsafeOverride();
  void setThreadsafeOverride(String threadsafeOverride);

  @Override
  String getPythonStartupScript();
  void setPythonStartupScript(String pythonStartupScript);

  @Override
  String getPythonStartupArgs();
  void setPythonStartupArgs(String pythonStartupArgs);

  @Override
  List<String> getJvmFlags();
  void setJvmFlags(List<String> jvmFlags);

  @Override
  String getCustomEntrypoint();
  void setCustomEntrypoint(String customEntrypoint);

  @Override
  String getRuntime();
  void setRuntime(String runtime);

  @Override
  Boolean getAllowSkippedFiles();
  void setAllowSkippedFiles(Boolean allowSkippedFiles);

  @Override
  Integer getApiPort();
  void setApiPort(Integer apiPort);

  @Override
  Boolean getAutomaticRestart();
  void setAutomaticRestart(Boolean automaticRestart);

  @Override
  String getDevAppserverLogLevel();
  void setDevAppserverLogLevel(String devAppserverLogLevel);

  @Override
  Boolean getSkipSdkUpdateCheck();
  void setSkipSdkUpdateCheck(Boolean skipSdkUpdateCheck);

  @Override
  String getDefaultGcsBucketName();
  void setDefaultGcsBucketName(String defaultGcsBucketName);
}

