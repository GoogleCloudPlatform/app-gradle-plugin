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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlPlugin;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import java.io.IOException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for the AppEnginePluginTest. */
public class AppEnginePluginTest {

  @Rule public TemporaryFolder testProjectRoot = new TemporaryFolder();

  @Test
  public void testCheckGradleVersion_pass() {
    new TestProject(testProjectRoot.getRoot())
        .applyGradleRunnerWithGradleVersion(
            AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION.getVersion());
    // pass
  }

  @Test
  public void testCheckGradleVersion_fail() throws IOException {
    try {
      new TestProject(testProjectRoot.getRoot())
          .addAutoDownloadingBuildFile()
          .applyGradleRunnerWithGradleVersion("2.8");
    } catch (UnexpectedBuildFailure ex) {
      assertThat(
          ex.getMessage(),
          containsString(
              "Detected Gradle 2.8, but the appengine-gradle-plugin requires "
                  + AppEngineCorePluginConfiguration.GRADLE_MIN_VERSION
                  + " or higher."));
    }
    JavaVersion.current().isJava9Compatible();
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
  public void testDetectAppYaml_withProjectBuilder() throws IOException {
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
  public void testDetectAppYaml_withFallbackNegative() throws IOException {
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
