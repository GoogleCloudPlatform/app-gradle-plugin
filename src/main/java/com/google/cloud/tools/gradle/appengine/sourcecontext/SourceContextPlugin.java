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

package com.google.cloud.tools.gradle.appengine.sourcecontext;

import com.google.cloud.tools.gradle.appengine.core.extension.Tools;
import com.google.cloud.tools.gradle.appengine.core.task.CloudSdkBuilderFactory;
import com.google.cloud.tools.gradle.appengine.sourcecontext.extension.GenRepoInfoFileExtension;
import com.google.cloud.tools.gradle.appengine.sourcecontext.task.GenRepoInfoFileTask;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;

import javax.annotation.Nullable;

/**
 * Plugin for adding source context into App Engine project
 */
public class SourceContextPlugin implements Plugin<Project> {

  private Project project;
  private GenRepoInfoFileExtension extension;
  private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Override
  public void apply(Project project) {
    this.project = project;

    createExtension();
    createSourceContextTask();
  }

  private void createExtension() {
    ExtensionAware appengine = new ExtensionUtil(project).get("appengine");
    final Tools tools = new ExtensionUtil(appengine).get("tools");

    // create our extension under the root appengine extension
    extension = appengine.getExtensions()
        .create("sourceContext", GenRepoInfoFileExtension.class, project.getBuildDir(),
            project.getRootDir());

    // wait to read the cloudSdkHome till after project evaluation
    project.afterEvaluate(new Action<Project>() {
      @Override
      public void execute(Project project) {
        cloudSdkBuilderFactory = new CloudSdkBuilderFactory(tools.getCloudSdkHome());
      }
    });
  }

  private void createSourceContextTask() {
    project.getTasks().create("_createSourceContext", GenRepoInfoFileTask.class,
        new Action<GenRepoInfoFileTask>() {
          @Override
          public void execute(final GenRepoInfoFileTask genRepoInfoFile) {
            genRepoInfoFile.setDescription("_internal");

            project.afterEvaluate(new Action<Project>() {
              @Override
              public void execute(Project project) {
                genRepoInfoFile.setConfiguration(extension);
                genRepoInfoFile.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
              }
            });
          }
        });
    configureArchiveTask(project.getTasks().withType(War.class).findByName("war"));
    configureArchiveTask(project.getTasks().withType(Jar.class).findByName("jar"));
  }

  // inject source-context into the META-INF directory of a jar or war
  private void configureArchiveTask(@Nullable AbstractArchiveTask task) {
    if (task == null) {
      return;
    }
    task.dependsOn("_createSourceContext");
    task.from(extension.getOutputDirectory(), new Action<CopySpec>() {
      @Override
      public void execute(CopySpec copySpec) {
        copySpec.into("META-INF");
      }
    });
  }
}
