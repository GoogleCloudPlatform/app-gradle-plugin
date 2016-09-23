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

import com.google.cloud.tools.gradle.appengine.model.AppEngineFlexibleExtension;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test App Engine Flexible Plugin configuration
 */
public class AppEngineFlexiblePluginTest {

  @Rule
  public final TemporaryFolder  testProjectDir = new TemporaryFolder();

  @Test
  public void testDeploy_taskTree() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    InputStream buildFileContent = getClass().getClassLoader()
        .getResourceAsStream("projects/AppEnginePluginTest/build.gradle");
    Files.copy(buildFileContent, buildFile);

    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineDeploy", "--dry-run", "--stacktrace")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":assemble",
            ":appengineStage", ":appengineDeploy");
    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDefaultConfiguration() {
    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();

    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(WarPlugin.class);
    p.getPluginManager().apply(AppEngineFlexiblePlugin.class);
    ((ProjectInternal) p).evaluate();

    Object ext = p.getExtensions().getByName("appengine");
    Assert.assertThat(ext, Matchers.instanceOf(AppEngineFlexibleExtension.class));

    AppEngineFlexibleExtension extension = (AppEngineFlexibleExtension) ext;
    Assert.assertEquals(new File(p.getBuildDir(), "staged-app"),
        extension.getStage().getStagingDirectory());
    Assert.assertEquals(Collections.singletonList(new File(p.getBuildDir(), "staged-app/app.yaml")),
        extension.getDeploy().getDeployables());
    Assert.assertEquals(new File(testProjectDir.getRoot(), "src/main/appengine").toPath(),
        extension.getStage().getAppEngineDirectory().toPath());
    Assert.assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    Assert.assertEquals((((War) p.getProperties().get("war")).getArchivePath()),
        extension.getStage().getArtifact());
  }

  @Test
  public void testDefaultConfigurationAlternative() {
    File dockerDir = new File(testProjectDir.getRoot(), "src/main/docker");
    dockerDir.mkdirs();

    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(AppEngineFlexiblePlugin.class);
    ((ProjectInternal) p).evaluate();
    Object ext = p.getExtensions().getByType(AppEngineFlexibleExtension.class);

    AppEngineFlexibleExtension extension = (AppEngineFlexibleExtension) ext;
    Assert.assertTrue(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    Assert.assertEquals((((Jar) p.getProperties().get("jar")).getArchivePath()),
        extension.getStage().getArtifact());
  }
}