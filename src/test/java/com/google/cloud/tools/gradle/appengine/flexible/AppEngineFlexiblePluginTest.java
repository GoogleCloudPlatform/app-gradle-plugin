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

package com.google.cloud.tools.gradle.appengine.flexible;

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePlugin;
import com.google.cloud.tools.gradle.appengine.core.extension.Deploy;
import com.google.cloud.tools.gradle.appengine.flexible.extension.StageFlexible;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
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
import java.util.List;

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

    ExtensionAware ext = (ExtensionAware) p.getExtensions().getByName("appengine");
    Deploy deployExt = new ExtensionUtil(ext).get("deploy");
    StageFlexible stageExt = new ExtensionUtil(ext).get("stage");

    Assert.assertEquals(new File(p.getBuildDir(), "staged-app"), stageExt.getStagingDirectory());
    Assert.assertEquals(new File(testProjectDir.getRoot(), "src/main/appengine"), stageExt.getAppEngineDirectory());
    Assert.assertEquals((((War) p.getProperties().get("war")).getArchivePath()), stageExt.getArtifact());
    Assert.assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    Assert.assertEquals(Collections.singletonList(new File(p.getBuildDir(), "staged-app/app.yaml")), deployExt.getDeployables());
  }

  @Test
  public void testDefaultConfigurationAlternative() {
    File dockerDir = new File(testProjectDir.getRoot(), "src/main/docker");
    dockerDir.mkdirs();

    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(AppEngineFlexiblePlugin.class);
    ((ProjectInternal) p).evaluate();

    ExtensionAware ext = (ExtensionAware) p.getExtensions().getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    StageFlexible stageExt = new ExtensionUtil(ext).get(AppEngineFlexiblePlugin.STAGE_EXTENSION);

    Assert.assertTrue(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    Assert.assertEquals((((Jar) p.getProperties().get("jar")).getArchivePath()), stageExt.getArtifact());
  }
}