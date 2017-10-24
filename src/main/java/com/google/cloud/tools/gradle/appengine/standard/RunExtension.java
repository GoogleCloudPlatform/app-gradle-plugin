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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.api.devserver.RunConfiguration;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;

/** Extension element to define Run configurations for App Engine Standard Environments. */
public class RunExtension implements RunConfiguration {

  private final Project project;
  private int startSuccessTimeout;
  private String serverVersion;

  private List<File> services;
  private String host;
  private Integer port;
  private String adminHost;
  private Integer adminPort;
  private String authDomain;
  private File storagePath;
  private String logLevel;
  private Integer maxModuleInstances;
  private Boolean useMtimeFileWatcher;
  private String threadsafeOverride;
  private String pythonStartupScript;
  private String pythonStartupArgs;
  private List<String> jvmFlags;
  private String customEntrypoint;
  private String runtime;
  private Boolean allowSkippedFiles;
  private Integer apiPort;
  private Boolean automaticRestart;
  private String devAppserverLogLevel;
  private Boolean skipSdkUpdateCheck;
  private String defaultGcsBucketName;
  private Boolean clearDatastore;
  private File datastorePath;
  private Map<String, String> environment;
  private List<String> additionalArguments;

  /**
   * Constructor.
   *
   * @param project The gradle project.
   */
  public RunExtension(Project project) {
    this.project = project;
  }

  public int getStartSuccessTimeout() {
    return startSuccessTimeout;
  }

  public void setStartSuccessTimeout(int startSuccessTimeout) {
    this.startSuccessTimeout = startSuccessTimeout;
  }

  public String getServerVersion() {
    return serverVersion;
  }

  public void setServerVersion(String serverVersion) throws ProjectConfigurationException {
    this.serverVersion = serverVersion;
  }

  /** Deprecated in favor of "services". */
  @Deprecated
  public void setAppYamls(Object appYamls) {
    project
        .getLogger()
        .warn(
            "'appYamls' is deprecated, this parameter will set 'services'."
                + " Use 'services' in the future.");
    setServices(appYamls);
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  @Override
  public String getAdminHost() {
    return adminHost;
  }

  public void setAdminHost(String adminHost) {
    this.adminHost = adminHost;
  }

  @Override
  public Integer getAdminPort() {
    return adminPort;
  }

  public void setAdminPort(Integer adminPort) {
    this.adminPort = adminPort;
  }

  @Override
  public String getAuthDomain() {
    return authDomain;
  }

  public void setAuthDomain(String authDomain) {
    this.authDomain = authDomain;
  }

  @Override
  public File getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(File storagePath) {
    this.storagePath = project.file(storagePath);
  }

  @Override
  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  @Override
  public Integer getMaxModuleInstances() {
    return maxModuleInstances;
  }

  public void setMaxModuleInstances(Integer maxModuleInstances) {
    this.maxModuleInstances = maxModuleInstances;
  }

  @Override
  public Boolean getUseMtimeFileWatcher() {
    return useMtimeFileWatcher;
  }

  public void setUseMtimeFileWatcher(Boolean useMtimeFileWatcher) {
    this.useMtimeFileWatcher = useMtimeFileWatcher;
  }

  @Override
  public String getThreadsafeOverride() {
    return threadsafeOverride;
  }

  public void setThreadsafeOverride(String threadsafeOverride) {
    this.threadsafeOverride = threadsafeOverride;
  }

  @Override
  public String getPythonStartupScript() {
    return pythonStartupScript;
  }

  public void setPythonStartupScript(String pythonStartupScript) {
    this.pythonStartupScript = pythonStartupScript;
  }

  @Override
  public String getPythonStartupArgs() {
    return pythonStartupArgs;
  }

  public void setPythonStartupArgs(String pythonStartupArgs) {
    this.pythonStartupArgs = pythonStartupArgs;
  }

  @Override
  public List<String> getJvmFlags() {
    return jvmFlags;
  }

  public void setJvmFlags(List<String> jvmFlags) {
    this.jvmFlags = jvmFlags;
  }

  @Override
  public String getCustomEntrypoint() {
    return customEntrypoint;
  }

  public void setCustomEntrypoint(String customEntrypoint) {
    this.customEntrypoint = customEntrypoint;
  }

  @Override
  public String getRuntime() {
    return runtime;
  }

  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  @Override
  public Boolean getAllowSkippedFiles() {
    return allowSkippedFiles;
  }

  public void setAllowSkippedFiles(Boolean allowSkippedFiles) {
    this.allowSkippedFiles = allowSkippedFiles;
  }

  @Override
  public Integer getApiPort() {
    return apiPort;
  }

  public void setApiPort(Integer apiPort) {
    this.apiPort = apiPort;
  }

  @Override
  public Boolean getAutomaticRestart() {
    return automaticRestart;
  }

  public void setAutomaticRestart(Boolean automaticRestart) {
    this.automaticRestart = automaticRestart;
  }

  @Override
  public String getDevAppserverLogLevel() {
    return devAppserverLogLevel;
  }

  public void setDevAppserverLogLevel(String devAppserverLogLevel) {
    this.devAppserverLogLevel = devAppserverLogLevel;
  }

  @Override
  public Boolean getSkipSdkUpdateCheck() {
    return skipSdkUpdateCheck;
  }

  public void setSkipSdkUpdateCheck(Boolean skipSdkUpdateCheck) {
    this.skipSdkUpdateCheck = skipSdkUpdateCheck;
  }

  @Override
  public String getDefaultGcsBucketName() {
    return defaultGcsBucketName;
  }

  public void setDefaultGcsBucketName(String defaultGcsBucketName) {
    this.defaultGcsBucketName = defaultGcsBucketName;
  }

  @Override
  public Boolean getClearDatastore() {
    return clearDatastore;
  }

  public void setClearDatastore(Boolean clearDatastore) {
    this.clearDatastore = clearDatastore;
  }

  @Override
  public List<File> getServices() {
    return services;
  }

  public void setServices(Object services) {
    this.services = new ArrayList<>(project.files(services).getFiles());
  }

  @Override
  public File getDatastorePath() {
    return datastorePath;
  }

  public void setDatastorePath(Object datastorePath) {
    this.datastorePath = project.file(datastorePath);
  }

  @Override
  public Map<String, String> getEnvironment() {
    return environment;
  }

  public void setEnvironment(Map<String, String> environment) {
    this.environment = environment;
  }

  @Override
  public List<String> getAdditionalArguments() {
    return additionalArguments;
  }

  public void setAdditionalArguments(List<String> additionalArguments) {
    this.additionalArguments =
        additionalArguments != null ? ImmutableList.copyOf(additionalArguments) : null;
  }
}
