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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePlugin;
import com.google.cloud.tools.gradle.appengine.core.extension.Deploy;
import com.google.cloud.tools.gradle.appengine.standard.extension.Run;
import com.google.cloud.tools.gradle.appengine.standard.extension.StageStandard;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import com.google.common.base.Charsets;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
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
 * Test App Engine Standard Plugin configuration
 */
public class AppEngineStandardPluginTest {

  @Rule
  public final TemporaryFolder testProjectDir = new TemporaryFolder();

  public void setUpTestProject() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    InputStream buildFileContent = getClass().getClassLoader()
        .getResourceAsStream("projects/AppEnginePluginTest/build.gradle");
    Files.copy(buildFileContent, buildFile);

    Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));
  }

  @Test
  public void testDeploy_taskTree() throws IOException {
    setUpTestProject();
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
  public void testRun_taskTree() throws IOException {
    setUpTestProject();
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineRun", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":explodeWar", ":assemble",
            ":appengineRun");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testStart_taskTree() throws IOException {
    setUpTestProject();
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStart", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":compileJava", ":processResources", ":classes", ":war", ":explodeWar", ":assemble",
            ":appengineStart");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));

  }

  @Test
  public void testStop_taskTree() throws IOException {
    setUpTestProject();
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStop", "--dry-run")
        .build();

    final List<String> expected = Collections.singletonList(":appengineStop");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));

  }

  @Test
  public void testDefaultConfiguration() throws IOException {
    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();

    File appengineWebXml = new File(testProjectDir.getRoot(),
        "src/main/webapp/WEB-INF/appengine-web.xml");
    appengineWebXml.getParentFile().mkdirs();
    appengineWebXml.createNewFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes());

    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(WarPlugin.class);
    p.getPluginManager().apply(AppEngineStandardPlugin.class);
    ((ProjectInternal) p).evaluate();

    ExtensionAware ext = (ExtensionAware) p.getExtensions()
        .getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    Deploy deployExt = new ExtensionUtil(ext).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    StageStandard stageExt = new ExtensionUtil(ext).get(AppEngineStandardPlugin.STAGE_EXTENSION);
    Run run = new ExtensionUtil(ext).get(AppEngineStandardPlugin.RUN_EXTENSION);

    Assert.assertEquals(new File(p.getBuildDir(), "exploded-app"), stageExt.getSourceDirectory());
    Assert.assertEquals(new File(p.getBuildDir(), "staged-app"), stageExt.getStagingDirectory());
    Assert.assertEquals(Collections.singletonList(new File(p.getBuildDir(), "staged-app/app.yaml")),
        deployExt.getDeployables());
    Assert.assertEquals(Collections.singletonList(new File(p.getBuildDir(), "exploded-app")),
        run.getAppYamls());
    Assert.assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());
  }

}