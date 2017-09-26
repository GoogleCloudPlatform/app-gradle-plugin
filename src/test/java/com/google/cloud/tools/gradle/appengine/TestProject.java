/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.flexible.AppEngineFlexiblePlugin;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

/** Create test projects for plugin test runs. */
public class TestProject {

  private final File projectRoot;

  /** Initialize empty test project at projectRoot. */
  public TestProject(File projectRoot) {
    this.projectRoot = projectRoot;
  }

  /** Add a standard appengine-gradle-plugin build file. */
  public TestProject addStandardBuildFile() throws IOException {
    addBuildFile("projects/AppEnginePluginTest/build-standard.gradle");
    return this;
  }

  /** Add a flexible appengine-gradle-plugin build file. */
  public TestProject addFlexibleBuildFile() throws IOException {
    addBuildFile("projects/AppEnginePluginTest/build-flexible.gradle");
    return this;
  }

  /** Add an generic appengine-gradle-plugin build file (for auto detection cases). */
  public TestProject addAutoDetectingBuildFile() throws IOException {
    addBuildFile("projects/AppEnginePluginTest/build-auto.gradle");
    return this;
  }

  private void addBuildFile(String pathInResources) throws IOException {
    Path buildFile = projectRoot.toPath().resolve("build.gradle");
    InputStream buildFileContent = getClass().getClassLoader().getResourceAsStream(pathInResources);
    Files.copy(buildFileContent, buildFile);
  }

  /** Add a minimal appengine-web.xml file in the standard location. */
  public TestProject addAppEngineWebXml() throws IOException {
    Path webInf = projectRoot.toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));

    return this;
  }

  /** Add an empty docker directory. */
  public TestProject addDockerDir() throws IOException {
    File dockerDir = new File(projectRoot, "src/main/docker");
    if (!dockerDir.mkdirs()) {
      throw new RuntimeException("Test failed due to directory creation error");
    }
    return this;
  }

  /** Run the gradle runner (build) and return the build result. */
  public BuildResult applyGradleRunner(String... arguments) {
    return GradleRunner.create()
        .withProjectDir(projectRoot)
        .withPluginClasspath()
        .withArguments(arguments)
        .build();
  }

  /** Run the gradle runner (build) with a specific gradle version and return the build result. */
  public BuildResult applyGradleRunnerWithVersion(String version) {
    return GradleRunner.create()
        .withProjectDir(projectRoot)
        .withPluginClasspath()
        .withGradleVersion(version)
        .build();
  }

  /** Run the project builder and return an evaluated project. */
  public Project applyStandardProjectBuilder() throws IOException {
    return applyProjectBuilder(JavaPlugin.class, WarPlugin.class, AppEngineStandardPlugin.class);
  }

  /** Run the project builder and return an evaluated project. */
  public Project applyFlexibleProjectBuilder() throws IOException {
    return applyProjectBuilder(JavaPlugin.class, AppEngineFlexiblePlugin.class);
  }

  /** Run the project builder and return an evaluated project. */
  public Project applyFlexibleWarProjectBuilder() throws IOException {
    return applyProjectBuilder(JavaPlugin.class, WarPlugin.class, AppEngineFlexiblePlugin.class);
  }

  /** Run the project builder and return an evaluated project. */
  public Project applyAutoDetectingProjectBuilder() throws IOException {
    return applyProjectBuilder(JavaPlugin.class, WarPlugin.class, AppEnginePlugin.class);
  }

  /** Run the project builder and return an evaluated project. */
  public Project applyAutoDetectingProjectBuilderWithFallbackTrigger() throws IOException {
    return applyProjectBuilder(JavaPlugin.class, AppEnginePlugin.class, WarPlugin.class);
  }

  private Project applyProjectBuilder(Class<?>... plugins) throws IOException {
    Project p = ProjectBuilder.builder().withProjectDir(projectRoot).build();
    for (Class<?> clazz : plugins) {
      p.getPluginManager().apply(clazz);
    }

    ((ProjectInternal) p).evaluate();

    return p;
  }
}
