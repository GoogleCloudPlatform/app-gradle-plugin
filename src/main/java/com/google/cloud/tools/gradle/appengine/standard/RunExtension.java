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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.api.devserver.RunConfiguration;
import com.google.cloud.tools.gradle.appengine.util.NullSafe;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.plugins.BasePlugin;

/** Extension element to define Run configurations for App Engine Standard Environments. */
public class RunExtension {

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
  private String projectId;

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

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getAdminHost() {
    return adminHost;
  }

  public void setAdminHost(String adminHost) {
    this.adminHost = adminHost;
  }

  public Integer getAdminPort() {
    return adminPort;
  }

  public void setAdminPort(Integer adminPort) {
    this.adminPort = adminPort;
  }

  public String getAuthDomain() {
    return authDomain;
  }

  public void setAuthDomain(String authDomain) {
    this.authDomain = authDomain;
  }

  public File getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(File storagePath) {
    this.storagePath = project.file(storagePath);
  }

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public Integer getMaxModuleInstances() {
    return maxModuleInstances;
  }

  public void setMaxModuleInstances(Integer maxModuleInstances) {
    this.maxModuleInstances = maxModuleInstances;
  }

  public Boolean getUseMtimeFileWatcher() {
    return useMtimeFileWatcher;
  }

  public void setUseMtimeFileWatcher(Boolean useMtimeFileWatcher) {
    this.useMtimeFileWatcher = useMtimeFileWatcher;
  }

  public String getThreadsafeOverride() {
    return threadsafeOverride;
  }

  public void setThreadsafeOverride(String threadsafeOverride) {
    this.threadsafeOverride = threadsafeOverride;
  }

  public String getPythonStartupScript() {
    return pythonStartupScript;
  }

  public void setPythonStartupScript(String pythonStartupScript) {
    this.pythonStartupScript = pythonStartupScript;
  }

  public String getPythonStartupArgs() {
    return pythonStartupArgs;
  }

  public void setPythonStartupArgs(String pythonStartupArgs) {
    this.pythonStartupArgs = pythonStartupArgs;
  }

  public List<String> getJvmFlags() {
    return jvmFlags;
  }

  public void setJvmFlags(List<String> jvmFlags) {
    this.jvmFlags = jvmFlags;
  }

  public String getCustomEntrypoint() {
    return customEntrypoint;
  }

  public void setCustomEntrypoint(String customEntrypoint) {
    this.customEntrypoint = customEntrypoint;
  }

  public String getRuntime() {
    return runtime;
  }

  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  public Boolean getAllowSkippedFiles() {
    return allowSkippedFiles;
  }

  public void setAllowSkippedFiles(Boolean allowSkippedFiles) {
    this.allowSkippedFiles = allowSkippedFiles;
  }

  public Integer getApiPort() {
    return apiPort;
  }

  public void setApiPort(Integer apiPort) {
    this.apiPort = apiPort;
  }

  public Boolean getAutomaticRestart() {
    return automaticRestart;
  }

  public void setAutomaticRestart(Boolean automaticRestart) {
    this.automaticRestart = automaticRestart;
  }

  public String getDevAppserverLogLevel() {
    return devAppserverLogLevel;
  }

  public void setDevAppserverLogLevel(String devAppserverLogLevel) {
    this.devAppserverLogLevel = devAppserverLogLevel;
  }

  public Boolean getSkipSdkUpdateCheck() {
    return skipSdkUpdateCheck;
  }

  public void setSkipSdkUpdateCheck(Boolean skipSdkUpdateCheck) {
    this.skipSdkUpdateCheck = skipSdkUpdateCheck;
  }

  public String getDefaultGcsBucketName() {
    return defaultGcsBucketName;
  }

  public void setDefaultGcsBucketName(String defaultGcsBucketName) {
    this.defaultGcsBucketName = defaultGcsBucketName;
  }

  public Boolean getClearDatastore() {
    return clearDatastore;
  }

  public void setClearDatastore(Boolean clearDatastore) {
    this.clearDatastore = clearDatastore;
  }

  public List<File> getServices() {
    return services;
  }

  public void setServices(Object services) {
    this.services = new ArrayList<>(project.files(services).getFiles());
  }

  /**
   * Returns the appengine service directory for this project and modifies the task dependencies of
   * run/start to ensure {@code serviceProject} is built first.
   */
  public File projectAsService(String serviceProject) {
    return projectAsService(project.getRootProject().project(serviceProject));
  }

  /**
   * Returns the appengine service directory for this project and modifies the task dependencies of
   * run/start to ensure {@code serviceProject} is built first.
   */
  public File projectAsService(Project serviceProject) {
    if (!serviceProject.equals(project)) {
      project.evaluationDependsOn(serviceProject.getPath());
    }
    project
        .getTasks()
        .findByName(AppEngineStandardPlugin.RUN_TASK_NAME)
        .dependsOn(serviceProject.getTasks().findByPath(BasePlugin.ASSEMBLE_TASK_NAME));
    project
        .getTasks()
        .findByName(AppEngineStandardPlugin.START_TASK_NAME)
        .dependsOn(serviceProject.getTasks().findByPath(BasePlugin.ASSEMBLE_TASK_NAME));
    return serviceProject
        .getTasks()
        .findByName(AppEngineStandardPlugin.EXPLODE_WAR_TASK_NAME)
        .getOutputs()
        .getFiles()
        .getSingleFile();
  }

  public File getDatastorePath() {
    return datastorePath;
  }

  public void setDatastorePath(Object datastorePath) {
    this.datastorePath = project.file(datastorePath);
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public void setEnvironment(Map<String, String> environment) {
    this.environment = environment;
  }

  public List<String> getAdditionalArguments() {
    return additionalArguments;
  }

  public void setAdditionalArguments(List<String> additionalArguments) {
    this.additionalArguments =
        additionalArguments != null ? ImmutableList.copyOf(additionalArguments) : null;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  RunConfiguration toRunConfiguration() {
    return RunConfiguration.builder(
            services.stream().map(File::toPath).collect(Collectors.toList()))
        .additionalArguments(additionalArguments)
        .adminHost(adminHost)
        .adminPort(adminPort)
        .authDomain(authDomain)
        .allowSkippedFiles(allowSkippedFiles)
        .apiPort(apiPort)
        .automaticRestart(automaticRestart)
        .clearDatastore(clearDatastore)
        .customEntrypoint(customEntrypoint)
        .datastorePath(NullSafe.convert(datastorePath, File::toPath))
        .defaultGcsBucketName(defaultGcsBucketName)
        .devAppserverLogLevel(devAppserverLogLevel)
        .environment(environment)
        .host(host)
        .jvmFlags(jvmFlags)
        .logLevel(logLevel)
        .maxModuleInstances(maxModuleInstances)
        .port(port)
        .projectId(projectId)
        .pythonStartupArgs(pythonStartupArgs)
        .pythonStartupScript(pythonStartupScript)
        .runtime(runtime)
        .skipSdkUpdateCheck(skipSdkUpdateCheck)
        .storagePath(NullSafe.convert(storagePath, File::toPath))
        .threadsafeOverride(threadsafeOverride)
        .useMtimeFileWatcher(useMtimeFileWatcher)
        .build();
  }
}
