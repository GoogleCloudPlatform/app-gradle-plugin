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

package com.google.cloud.tools.gradle.appengine.appyaml;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.TestProject;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.util.GradleCompatibility;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Test App Engine AppYaml Plugin configuration. */
public class AppEngineAppYamlPluginTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private static boolean isJava8Runtime() {
    return System.getProperty("java.version").startsWith("1.8");
  }

  private TestProject createTestProject() throws IOException {
    return new TestProject(testProjectDir.getRoot()).addAppYamlBuildFile();
  }

  private TestProject createTestProjectWithHome() throws IOException {
    return new TestProject(testProjectDir.getRoot()).addAppYamlBuildFileWithHome();
  }

  @Test
  public void testCheckGradleVersion_pass() throws IOException {
    assumeTrue(isJava8Runtime());
    createTestProject()
        .applyGradleRunnerWithGradleVersion(
            GradleCompatibility.getMinimumGradleVersion().getVersion());
    // pass
  }

  @Test
  public void testCheckGradleVersion_fail() throws IOException {
    assumeTrue(isJava8Runtime());
    try {
      createTestProject().applyGradleRunnerWithGradleVersion("2.8");
    } catch (UnexpectedBuildFailure ex) {
      assertThat(
          ex.getMessage(),
          containsString(
              "Detected Gradle 2.8, but the appengine-gradle-plugin requires "
                  + GradleCompatibility.getMinimumGradleVersion()
                  + " or higher."));
    }
  }

  @Test
  public void testLogin_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineCloudSdkLogin", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineCloudSdkLogin");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeploy_taskTree() throws IOException {
    BuildResult buildResult = createTestProject().applyGradleRunner("appengineDeploy", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeploy");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testCheck_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProjectWithHome().applyGradleRunner("appengineDeploy", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":assemble",
            ":checkCloudSdk",
            ":appengineStage",
            ":appengineDeploy");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployCron_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployCron", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineDeployCron");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployDispatch_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployDispatch", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineDeployDispatch");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployDos_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployDos", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineDeployDos");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployIndex_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployIndex", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineDeployIndex");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployQueue_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployQueue", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineDeployQueue");
    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDefaultConfiguration() throws IOException {
    Project p = new TestProject(testProjectDir.getRoot()).applyAppYamlWarProjectBuilder();

    AppEngineAppYamlExtension ext = p.getExtensions().getByType(AppEngineAppYamlExtension.class);
    DeployExtension deployExt = ext.getDeploy();
    StageAppYamlExtension stageExt = ext.getStage();

    assertEquals(new File(p.getBuildDir(), "staged-app"), stageExt.getStagingDirectory());
    assertEquals(
        testProjectDir.getRoot().toPath().toRealPath().resolve("src/main/appengine"),
        stageExt.getAppEngineDirectory().toPath());
    assertEquals(
        testProjectDir.getRoot().toPath().toRealPath().resolve("src/main/appengine"),
        deployExt.getAppEngineDirectory().toPath());
    War war = (War) p.getProperties().get(WarPlugin.WAR_TASK_NAME);
    assertEquals(war.getArchiveFile().get().getAsFile(), stageExt.getArtifact());
    assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());

    assertEquals("test-project", deployExt.getProjectId());
    assertEquals("test-version", deployExt.getVersion());
  }

  @Test
  public void testDefaultConfigurationAlternative() {
    Project p =
        new TestProject(testProjectDir.getRoot()).addDockerDir().applyAppYamlProjectBuilder();

    AppEngineAppYamlExtension ext = p.getExtensions().getByType(AppEngineAppYamlExtension.class);
    StageAppYamlExtension stageExt = ext.getStage();

    assertTrue(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    Jar jar = (Jar) p.getProperties().get(JavaPlugin.JAR_TASK_NAME);
    assertEquals(jar.getArchiveFile().get().getAsFile(), stageExt.getArtifact());
  }

  @Test
  public void testAppEngineTaskGroupAssignment() {
    Project p = new TestProject(testProjectDir.getRoot()).applyAppYamlProjectBuilder();

    p.getTasks()
        .matching(task -> task.getName().startsWith("appengine"))
        .all(
            task ->
                assertEquals(
                    AppEngineAppYamlPlugin.APP_ENGINE_APP_YAML_TASK_GROUP, task.getGroup()));
  }
}
