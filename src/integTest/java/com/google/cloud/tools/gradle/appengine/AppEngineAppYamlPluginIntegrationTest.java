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

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.appengine.operations.CloudSdk;
import com.google.cloud.tools.appengine.operations.Gcloud;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandlerException;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

/** End to end tests for app.yaml based projects. */
public class AppEngineAppYamlPluginIntegrationTest {

  @Rule public Timeout globalTimeout = Timeout.seconds(900);

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    FileUtils.copyDirectory(
        new File("src/integTest/resources/projects/appyaml-project"), testProjectDir.getRoot());
  }

  @Test
  public void testDeploy()
      throws CloudSdkNotFoundException, IOException, ProcessHandlerException,
          UnsupportedOsException {

    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withDebug(true)
            .withArguments("appengineDeploy")
            .build();

    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("Deployed service [appyaml-project]"));

    deleteProject();
  }

  @Test
  public void testDeployAll()
      throws CloudSdkNotFoundException, IOException, ProcessHandlerException,
          UnsupportedOsException {

    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withDebug(true)
            .withArguments("appengineDeployAll")
            .build();

    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("Deployed service [appyaml-project]"));
    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("Custom routings have been updated."));
    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("DoS protection has been updated."));
    Assert.assertThat(
        buildResult.getOutput(),
        CoreMatchers.containsString("Indexes are being rebuilt. This may take a moment."));
    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("Cron jobs have been updated."));
    Assert.assertThat(
        buildResult.getOutput(), CoreMatchers.containsString("Task queues have been updated."));

    deleteProject();
  }

  private static void deleteProject()
      throws UnsupportedOsException, CloudSdkNotFoundException, IOException,
          ProcessHandlerException {
    Path sdkHome = ManagedCloudSdk.newManagedSdk().getSdkHome();
    CloudSdk cloudSdk = new CloudSdk.Builder().sdkPath(sdkHome).build();
    Gcloud.builder(cloudSdk)
        .build()
        .runCommand(Arrays.asList("app", "services", "delete", "appyaml-project", "--quiet"));
  }
}
