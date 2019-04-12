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

package com.google.cloud.tools.gradle.appengine.standard;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

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
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Test App Engine Standard Plugin configuration. */
public class AppEngineStandardPluginTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private static boolean isJava8Runtime() {
    return System.getProperty("java.version").startsWith("1.8");
  }

  private TestProject createTestProject() throws IOException {
    return new TestProject(testProjectDir.getRoot()).addStandardBuildFile().addAppEngineWebXml();
  }

  private TestProject createTestProjectWithHome() throws IOException {
    return new TestProject(testProjectDir.getRoot())
        .addStandardBuildFileWithHome()
        .addAppEngineWebXml();
  }

  private TestProject createTestProjectWithSdkVersion() throws IOException {
    return new TestProject(testProjectDir.getRoot())
        .addStandardBuildFileWithSdkVersion()
        .addAppEngineWebXml();
  }

  @Test
  public void testCheckGradleVersion_pass() throws IOException {
    assumeTrue(isJava8Runtime());
    createTestProject()
        .applyGradleRunnerWithGradleVersion(
            AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION.getVersion());
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
                  + AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION
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
            ":explodeWar",
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

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeployCron");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployDispatch_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployDispatch", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeployDispatch");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployDos_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployDos", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeployDos");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployIndex_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployIndex", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeployIndex");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDeployQueue_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineDeployQueue", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStage",
            ":appengineDeployQueue");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testRun_taskTree() throws IOException {
    BuildResult buildResult = createTestProject().applyGradleRunner("appengineRun", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineRun");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testStart_taskTree() throws IOException {
    BuildResult buildResult = createTestProject().applyGradleRunner("appengineStart", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk",
            ":appengineStart");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDownloadAVersion_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProjectWithSdkVersion().applyGradleRunner("appengineDeploy", "--dry-run");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            ":downloadCloudSdk", // this should NOT run checkCloudSdk
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
            ":explodeWar",
            ":assemble",
            ":checkCloudSdk",
            ":appengineStage",
            ":appengineDeploy");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testOffline_taskTree() throws IOException {
    BuildResult buildResult =
        createTestProject().applyGradleRunner("appengineStage", "--dry-run", "--offline");

    final List<String> expected =
        ImmutableList.of(
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble",
            // ":downloadCloudSdk", this is not included because --offline
            ":appengineStage");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testStop_taskTree() throws IOException {
    BuildResult buildResult = createTestProject().applyGradleRunner("appengineStop", "--dry-run");

    final List<String> expected = ImmutableList.of(":downloadCloudSdk", ":appengineStop");

    assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDefaultConfiguration() throws IOException {
    Project p =
        new TestProject(testProjectDir.getRoot())
            .addAppEngineWebXml()
            .applyStandardProjectBuilder();

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    final RunExtension run = ext.getRun();
    final DeployExtension deployExt = ext.getDeploy();
    final StageStandardExtension stageExt = ext.getStage();

    assertEquals(
        new File(p.getBuildDir(), "exploded-" + p.getName()), stageExt.getSourceDirectory());
    assertEquals(new File(p.getBuildDir(), "staged-app"), stageExt.getStagingDirectory());
    assertEquals(
        new File(p.getBuildDir(), "staged-app/WEB-INF/appengine-generated"),
        deployExt.getAppEngineDirectory());
    assertEquals(
        Collections.singletonList(new File(p.getBuildDir(), "exploded-" + p.getName())),
        run.getServices());
    assertFalse(new File(testProjectDir.getRoot(), "src/main/docker").exists());
    assertEquals(20, run.getStartSuccessTimeout());

    assertEquals("test-project", deployExt.getProjectId());
    assertEquals("test-version", deployExt.getVersion());
  }

  @Test
  public void testAppEngineTaskGroupAssignment() throws IOException {
    Project p =
        new TestProject(testProjectDir.getRoot())
            .addAppEngineWebXml()
            .applyStandardProjectBuilder();

    p.getTasks()
        .matching(task -> task.getName().startsWith("appengine"))
        .all(
            task ->
                assertEquals(
                    AppEngineStandardPlugin.APP_ENGINE_STANDARD_TASK_GROUP, task.getGroup()));
  }
}
