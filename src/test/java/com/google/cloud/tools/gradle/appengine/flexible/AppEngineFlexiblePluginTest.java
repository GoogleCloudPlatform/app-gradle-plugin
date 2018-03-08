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

package com.google.cloud.tools.gradle.appengine.flexible;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.TestProject;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Test App Engine Flexible Plugin configuration. */
public class AppEngineFlexiblePluginTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private TestProject createTestProject() throws IOException {
    return new TestProject(testProjectDir.getRoot()).addFlexibleBuildFile();
  }

  @Test
  public void testCheckGradleVersion_pass() throws IOException {
    createTestProject()
        .applyGradleRunnerWithGradleVersion(
            AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION.getVersion());
    // pass
  }

  @Test
  public void testCheckGradleVersion_fail() throws IOException {
    try {
      createTestProject().applyGradleRunnerWithGradleVersion("2.8");
    } catch (UnexpectedBuildFailure ex) {
      assertThat(
          ex.getMessage(),
          containsString(
              "Detected Gradle 2.8, but the appengine-gradle-plugin requires "
                  + AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION
                  + " or higher."));
    }
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
    Project p = new TestProject(testProjectDir.getRoot()).applyFlexibleWarProjectBuilder();

    AppEngineFlexibleExtension ext = p.getExtensions().getByType(AppEngineFlexibleExtension.class);
    DeployExtension deployExt = ext.getDeploy();
    StageFlexibleExtension stageExt = ext.getStage();

    assertEquals(new File(p.getBuildDir(), "staged-app"), stageExt.getStagingDirectory());
    assertEquals(
        testProjectDir.getRoot().toPath().toRealPath().resolve("src/main/appengine"),
        stageExt.getAppEngineDirectory().toPath());
    assertEquals(
        testProjectDir.getRoot().toPath().toRealPath().resolve("src/main/appengine"),
        deployExt.getAppEngineDirectory().toPath());
    assertEquals((((War) p.getProperties().get("war")).getArchivePath()), stageExt.getArtifact());
    assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    assertEquals(
        Collections.singletonList(new File(p.getBuildDir(), "staged-app/app.yaml")),
        deployExt.getDeployables());
  }

  @Test
  public void testDefaultConfigurationAlternative() throws IOException {
    Project p =
        new TestProject(testProjectDir.getRoot()).addDockerDir().applyFlexibleProjectBuilder();

    AppEngineFlexibleExtension ext = p.getExtensions().getByType(AppEngineFlexibleExtension.class);
    StageFlexibleExtension stageExt = ext.getStage();

    assertTrue(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    assertEquals((((Jar) p.getProperties().get("jar")).getArchivePath()), stageExt.getArtifact());
  }

  @Test
  public void testAppEngineTaskGroupAssignment() throws IOException {
    Project p = new TestProject(testProjectDir.getRoot()).applyFlexibleProjectBuilder();

    p.getTasks()
        .matching(task -> task.getName().startsWith("appengine"))
        .all(
            task ->
                assertEquals(
                    AppEngineFlexiblePlugin.APP_ENGINE_FLEXIBLE_TASK_GROUP, task.getGroup()));
  }
}
