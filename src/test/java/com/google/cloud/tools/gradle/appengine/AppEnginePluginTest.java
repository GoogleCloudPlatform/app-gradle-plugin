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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.cloud.tools.gradle.appengine.flexible.AppEngineFlexiblePlugin;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for the AppEnginePluginTest. */
public class AppEnginePluginTest {

  @Rule public TemporaryFolder testProjectRoot = new TemporaryFolder();

  @Test
  public void testDetectStandard_withGradleRunner() throws IOException {
    BuildResult buildResult =
        new TestProject(testProjectRoot.getRoot())
            .addAutoDetectingBuildFile()
            .addAppEngineWebXml()
            .applyGradleRunner("tasks");

    assertThat(
        buildResult.getOutput(),
        containsString(AppEngineStandardPlugin.APP_ENGINE_STANDARD_TASK_GROUP));
    assertThat(
        buildResult.getOutput(),
        not(containsString(AppEngineFlexiblePlugin.APP_ENGINE_FLEXIBLE_TASK_GROUP)));
  }

  @Test
  public void testDetectFlexible_withGradleRunner() throws IOException {
    BuildResult buildResult =
        new TestProject(testProjectRoot.getRoot())
            .addAutoDetectingBuildFile()
            .applyGradleRunner("tasks");

    assertThat(
        buildResult.getOutput(),
        containsString(AppEngineFlexiblePlugin.APP_ENGINE_FLEXIBLE_TASK_GROUP));
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

    // we applied this
    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine"));

    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-standard"));
    assertFalse(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-flexible"));
  }

  @Test
  public void testDetectFlexible_withProjectBuilder() throws IOException {
    Project p = new TestProject(testProjectRoot.getRoot()).applyAutoDetectingProjectBuilder();

    // we applied this
    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine"));

    assertTrue(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-flexible"));
    assertFalse(p.getPluginManager().hasPlugin("com.google.cloud.tools.appengine-standard"));
  }
}
