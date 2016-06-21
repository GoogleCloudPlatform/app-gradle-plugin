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

package com.google.cloud.tools.gradle.appengine;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test App Engine Standard Plugin configuration
 */
public class AppEngineStandardPluginTest {

  @Rule
  public final TemporaryFolder  testProjectDir = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    InputStream buildFileContent = getClass().getClassLoader()
        .getResourceAsStream("projects/AppEnginePluginTest/build.gradle");
    Files.copy(buildFileContent, buildFile);

    Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    Files.createFile(webInf.resolve("appengine-web.xml"));
  }

  @Test
  public void testDeploy_taskTree() {
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineDeploy", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":explodeWar", ":assemble",
            ":appengineStage", ":appengineDeploy");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testRun_taskTree() {
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineRun", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":explodeWar", ":assemble",
            ":appengineStage", ":appengineRun");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testStart_taskTree() {
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStart", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":explodeWar", ":assemble",
            ":appengineStage", ":appengineStart");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));

  }

  @Test
  public void testStop_taskTree() {
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStop", "--dry-run")
        .build();

    final List<String> expected = Collections.singletonList(":appengineStop");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));

  }
}