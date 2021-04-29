/*
 * Copyright 2017 Google LLC. All Rights Reserved.
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

import com.google.cloud.tools.gradle.appengine.MultiModuleTestProject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RunExtensionTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  @Test
  public void testProjectAsService_multiModuleBuilds() throws IOException {
    Project p =
        new MultiModuleTestProject(tmpDir.getRoot())
            .addModule("frontend")
            .addModule("backend")
            .build();

    Project frontend = p.project("frontend");
    Project backend = p.project("backend");

    // verify server tasks only depend on frontend:assemble
    Set<String> assemblesBefore = createAssembleTaskNames(frontend);
    Assert.assertEquals(
        assemblesBefore, getAssembleDependencies(frontend, AppEngineStandardPlugin.RUN_TASK_NAME));
    Assert.assertEquals(
        assemblesBefore,
        getAssembleDependencies(frontend, AppEngineStandardPlugin.START_TASK_NAME));

    File frontendServicePath =
        frontend
            .getExtensions()
            .findByType(AppEngineStandardExtension.class)
            .getRun()
            .projectAsService(frontend); // use the Project object representation
    File backendServicePath =
        frontend
            .getExtensions()
            .findByType(AppEngineStandardExtension.class)
            .getRun()
            .projectAsService(backend.getPath()); // use the String representation

    Assert.assertEquals(getExplodedAppDirectory(frontend), frontendServicePath);
    Assert.assertEquals(getExplodedAppDirectory(backend), backendServicePath);

    // verify server tasks now depend on backend:assemble as well
    Set<String> assemblesAfter = createAssembleTaskNames(frontend, backend);
    Assert.assertEquals(
        assemblesAfter, getAssembleDependencies(frontend, AppEngineStandardPlugin.RUN_TASK_NAME));
    Assert.assertEquals(
        assemblesAfter, getAssembleDependencies(frontend, AppEngineStandardPlugin.START_TASK_NAME));
  }

  private Set<String> getAssembleDependencies(Project project, String taskName) {
    Set<String> assembleDependencies = new HashSet<>();
    Set<Object> dependents = project.getTasks().findByPath(taskName).getDependsOn();
    for (Object dependent : dependents) {
      Task task = null;
      if (dependent instanceof Task) {
        task = (Task) dependent;
      } else if (dependent instanceof TaskProvider) {
        task = ((TaskProvider<?>) dependent).get();
      }

      if (task != null && task.getName().equals(BasePlugin.ASSEMBLE_TASK_NAME)) {
        assembleDependencies.add(task.getPath());
      }
    }
    return assembleDependencies;
  }

  private Set<String> createAssembleTaskNames(Project... projects) {
    return Arrays.stream(projects).map(p -> p.getPath() + ":assemble").collect(Collectors.toSet());
  }

  private File getExplodedAppDirectory(Project project) {
    return new File(project.getBuildDir(), "exploded-" + project.getName());
  }
}
