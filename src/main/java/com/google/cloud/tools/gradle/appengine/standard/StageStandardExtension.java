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

import com.google.cloud.tools.appengine.configuration.AppEngineWebXmlProjectStageConfiguration;
import com.google.cloud.tools.gradle.appengine.util.NullSafe;
import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

/** Extension element to define Stage configurations for App Engine Standard Environments. */
public class StageStandardExtension {

  private final Project project;

  private File sourceDirectory;
  private File stagingDirectory;
  private File dockerfile;
  private Boolean enableQuickstart;
  private Boolean disableUpdateCheck;
  private Boolean enableJarSplitting;
  private String jarSplittingExcludes;
  private String compileEncoding;
  private Boolean deleteJsps;
  private Boolean enableJarClasses;
  private Boolean disableJarJsps;
  private String runtime;

  /** Constuctor. */
  public StageStandardExtension(Project project) {
    this.project = project;
  }

  @InputDirectory
  public File getSourceDirectory() {
    return sourceDirectory;
  }

  public void setSourceDirectory(Object sourceDirectory) {
    this.sourceDirectory = project.file(sourceDirectory);
  }

  @OutputDirectory
  public File getStagingDirectory() {
    return stagingDirectory;
  }

  public void setStagingDirectory(Object stagingDirectory) {
    this.stagingDirectory = project.file(stagingDirectory);
  }

  @InputFile
  @Optional
  public File getDockerfile() {
    return dockerfile;
  }

  public void setDockerfile(Object dockerfile) {
    this.dockerfile = project.file(dockerfile);
  }

  @Input
  @Optional
  public Boolean getEnableQuickstart() {
    return enableQuickstart;
  }

  public void setEnableQuickstart(Boolean enableQuickstart) {
    this.enableQuickstart = enableQuickstart;
  }

  @Input
  @Optional
  public Boolean getDisableUpdateCheck() {
    return disableUpdateCheck;
  }

  public void setDisableUpdateCheck(Boolean disableUpdateCheck) {
    this.disableUpdateCheck = disableUpdateCheck;
  }

  @Input
  @Optional
  public Boolean getEnableJarSplitting() {
    return enableJarSplitting;
  }

  public void setEnableJarSplitting(Boolean enableJarSplitting) {
    this.enableJarSplitting = enableJarSplitting;
  }

  @Input
  @Optional
  public String getJarSplittingExcludes() {
    return jarSplittingExcludes;
  }

  public void setJarSplittingExcludes(String jarSplittingExcludes) {
    this.jarSplittingExcludes = jarSplittingExcludes;
  }

  @Input
  @Optional
  public String getCompileEncoding() {
    return compileEncoding;
  }

  public void setCompileEncoding(String compileEncoding) {
    this.compileEncoding = compileEncoding;
  }

  @Input
  @Optional
  public Boolean getDeleteJsps() {
    return deleteJsps;
  }

  public void setDeleteJsps(Boolean deleteJsps) {
    this.deleteJsps = deleteJsps;
  }

  @Input
  @Optional
  public Boolean getEnableJarClasses() {
    return enableJarClasses;
  }

  public void setEnableJarClasses(Boolean enableJarClasses) {
    this.enableJarClasses = enableJarClasses;
  }

  @Input
  @Optional
  public Boolean getDisableJarJsps() {
    return disableJarJsps;
  }

  public void setDisableJarJsps(Boolean disableJarJsps) {
    this.disableJarJsps = disableJarJsps;
  }

  @Input
  @Optional
  public String getRuntime() {
    return runtime;
  }

  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  AppEngineWebXmlProjectStageConfiguration toStageStandardConfiguration() {
    return AppEngineWebXmlProjectStageConfiguration.builder(
            sourceDirectory.toPath(), stagingDirectory.toPath())
        .compileEncoding(compileEncoding)
        .deleteJsps(deleteJsps)
        .disableJarJsps(disableJarJsps)
        .dockerfile(NullSafe.convert(dockerfile, File::toPath))
        .disableUpdateCheck(disableUpdateCheck)
        .enableJarClasses(enableJarClasses)
        .enableJarSplitting(enableJarSplitting)
        .enableQuickstart(enableQuickstart)
        .jarSplittingExcludes(jarSplittingExcludes)
        .runtime(runtime)
        .build();
  }
}
