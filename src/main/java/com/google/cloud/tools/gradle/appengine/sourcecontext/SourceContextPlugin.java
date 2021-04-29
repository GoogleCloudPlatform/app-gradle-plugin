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

package com.google.cloud.tools.gradle.appengine.sourcecontext;

import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCoreExtensionProperties;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkOperations;
import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import java.io.File;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

/** Plugin for adding source context into App Engine project. */
public class SourceContextPlugin implements Plugin<Project> {

  private Project project;
  private GenRepoInfoFileExtension extension;
  private CloudSdkOperations cloudSdkOperations;

  public static final String SOURCE_CONTEXT_EXTENSION = "sourceContext";

  @Override
  public void apply(Project project) {
    this.project = project;

    createExtension();
    createSourceContextTask();
  }

  private void createExtension() {
    // obtain extensions defined by core plugin.
    ExtensionAware appengine = new ExtensionUtil(project).get("appengine");
    final ToolsExtension tools = ((AppEngineCoreExtensionProperties) appengine).getTools();

    // create source context extension and set defaults
    extension =
        appengine
            .getExtensions()
            .create(SOURCE_CONTEXT_EXTENSION, GenRepoInfoFileExtension.class, project);
    extension.setOutputDirectory(new File(project.getBuildDir(), "sourceContext"));
    extension.setSourceDirectory(new File(project.getProjectDir(), "src"));

    // wait to read the cloudSdkHome till after project evaluation
    project.afterEvaluate(
        project -> {
          try {
            cloudSdkOperations =
                new CloudSdkOperations(tools.getCloudSdkHome(), null, tools.getVerbosity());
          } catch (CloudSdkNotFoundException ex) {
            // this should be caught in AppEngineCorePluginConfig before it can ever reach here.
            throw new GradleException("Could not find CloudSDK: ", ex);
          }
        });
  }

  private void createSourceContextTask() {
    TaskProvider<GenRepoInfoFileTask> genRepoInfoFile =
        project
            .getTasks()
            .register(
                "_createSourceContext",
                GenRepoInfoFileTask.class,
                genRepoInfoFileTask -> genRepoInfoFileTask.setDescription("_internal"));

    project.afterEvaluate(
        project ->
            genRepoInfoFile.configure(
                task -> {
                  task.setConfiguration(extension);
                  task.setGcloud(cloudSdkOperations.getGcloud());
                }));

    configureArchiveTask("war");
    configureArchiveTask("jar");
  }

  // inject source-context into the META-INF directory of a jar or war
  private void configureArchiveTask(String archiveTaskName) {
    try {
      TaskContainer tasks = project.getTasks();
      tasks
          .withType(AbstractArchiveTask.class)
          .named(archiveTaskName)
          .configure(
              task -> {
                task.dependsOn(tasks.named("_createSourceContext"));
                task.from(
                    extension.getOutputDirectory(), copySpec -> copySpec.into("WEB-INF/classes"));
              });

    } catch (UnknownTaskException ex) {
      // Do nothing if the task doesn't exist.
    }
  }
}
