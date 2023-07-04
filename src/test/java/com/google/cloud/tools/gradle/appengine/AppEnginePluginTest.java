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

package com.google.cloud.tools.gradle.appengine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.cloud.tools.gradle.appengine.util.GradleCompatibility;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for the AppEnginePluginTest. */
public class AppEnginePluginTest {

  @Rule public TemporaryFolder testProjectRoot = new TemporaryFolder();

  private boolean isJava8Runtime() {
    return System.getProperty("java.version").startsWith("1.8");
  }

  @Test
  public void testCheckGradleVersion_pass() {
    assumeTrue(isJava8Runtime());
    new TestProject(testProjectRoot.getRoot())
        .applyGradleRunnerWithGradleVersion(
            GradleCompatibility.getMinimumGradleVersion().getVersion());
    // pass
  }

  @Test
  public void testCheckGradleVersion_fail() throws IOException {
    assumeTrue(isJava8Runtime());
    try {
      new TestProject(testProjectRoot.getRoot())
          .addAutoDownloadingBuildFile()
          .applyGradleRunnerWithGradleVersion("2.8");
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
  public void testDetectStandard_withGradleRunner() throws IOException {
    BuildResult buildResult =
        new TestProject(testProjectRoot.getRoot())
            .addAutoDownloadingBuildFile()
            .addAppEngineWebXml()
            .applyGradleRunner("tasks");

    assertThat(
        buildResult.getOutput(),
        containsString(AppEngineStandardPlugin.APP_ENGINE_STANDARD_TASK_GROUP));
    assertThat(
        buildResult.getOutput(),
        not(containsString(AppEngineAppYamlPlugin.APP_ENGINE_APP_YAML_TASK_GROUP)));
  }

  @Test
  public void testDetectAppYaml_withGradleRunner() throws IOException {
    BuildResult buildResult =
        new TestProject(testProjectRoot.getRoot())
            .addAutoDownloadingBuildFile()
            .applyGradleRunner("tasks");

    assertThat(
        buildResult.getOutput(),
        containsString(AppEngineAppYamlPlugin.APP_ENGINE_APP_YAML_TASK_GROUP));
    assertThat(
        buildResult.getOutput(),
        not(containsString(AppEngineStandardPlugin.APP_ENGINE_STANDARD_TASK_GROUP)));
  }

  @Test
  public void testDetectStandard_withProjectBuilder() throws IOException {
    Project p =
        new TestProject(testProjectRoot.getRoot())
            .addAppEngineWebXml()
            .applyAutoDetectingProjectBuilder();

    assertStandard(p);
  }

  @Test
  public void testDetectAppYaml_withProjectBuilder() {
    Project p = new TestProject(testProjectRoot.getRoot()).applyAutoDetectingProjectBuilder();

    assertAppYaml(p);
  }

  @Test
  public void testDetectStandard_withFallbackMechanism() throws IOException {
    Project p =
        new TestProject(testProjectRoot.getRoot())
            .addAppEngineWebXml()
            .applyAutoDetectingProjectBuilderWithFallbackTrigger();

    assertStandard(p);
  }

  @Test
  public void testDetectAppYaml_withFallbackNegative() {
    Project p =
        new TestProject(testProjectRoot.getRoot())
            .applyAutoDetectingProjectBuilderWithFallbackTrigger();

    assertAppYaml(p);
  }

  private void assertStandard(Project p) {
    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine"));

    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-standard"));
    assertFalse(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-appyaml"));
  }

  private void assertAppYaml(Project p) {
    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine"));

    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-appyaml"));
    assertFalse(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-standard"));
  }
}
