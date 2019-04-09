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

import com.google.cloud.tools.appengine.configuration.RunConfiguration;
import com.google.cloud.tools.appengine.configuration.StopConfiguration;
import com.google.cloud.tools.gradle.appengine.core.DeployTargetResolver;
import com.google.cloud.tools.gradle.appengine.core.InternalProperty;
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

  @InternalProperty private DeployTargetResolver deployTargetResolver;

  private final Project project;
  private int startSuccessTimeout;
  private String serverVersion;

  private List<File> services;
  private String host;
  private Integer port;
  private List<String> jvmFlags;
  private Boolean automaticRestart;
  private String defaultGcsBucketName;
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

  public void setDeployTargetResolver(DeployTargetResolver deployTargetResolver) {
    this.deployTargetResolver = deployTargetResolver;
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

  public List<String> getJvmFlags() {
    return jvmFlags;
  }

  public void setJvmFlags(List<String> jvmFlags) {
    this.jvmFlags = jvmFlags;
  }

  public Boolean getAutomaticRestart() {
    return automaticRestart;
  }

  public void setAutomaticRestart(Boolean automaticRestart) {
    this.automaticRestart = automaticRestart;
  }

  public String getDefaultGcsBucketName() {
    return defaultGcsBucketName;
  }

  public void setDefaultGcsBucketName(String defaultGcsBucketName) {
    this.defaultGcsBucketName = defaultGcsBucketName;
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
    String processedProjectId = deployTargetResolver.getProject(projectId);
    return RunConfiguration.builder(
            services.stream().map(File::toPath).collect(Collectors.toList()))
        .additionalArguments(additionalArguments)
        .automaticRestart(automaticRestart)
        .defaultGcsBucketName(defaultGcsBucketName)
        .environment(environment)
        .host(host)
        .jvmFlags(jvmFlags)
        .port(port)
        .projectId(processedProjectId)
        .build();
  }

  StopConfiguration toStopConfiguration() {
    return StopConfiguration.builder().host(host).port(port).build();
  }
}
