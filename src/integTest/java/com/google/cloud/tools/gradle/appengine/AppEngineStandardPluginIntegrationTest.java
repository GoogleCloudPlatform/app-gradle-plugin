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

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.internal.process.ProcessRunnerException;
import com.google.cloud.tools.appengine.cloudsdk.process.NonZeroExceptionExitListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

/** End to end tests for standard projects. */
public class AppEngineStandardPluginIntegrationTest {

  @Rule public Timeout globalTimeout = Timeout.seconds(180);

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    FileUtils.copyDirectory(
        new File("src/integTest/resources/projects/standard-project"), testProjectDir.getRoot());
  }

  @Ignore
  @Test
  public void testDevAppServer_sync() {
    // TODO : write test for devapp server running in synchronous mode
  }

  /**
   * If this test is failing, make sure you've set JAVA_HOME=some-jdk7, it might have something to
   * do with the way dev_appserver.py is launching java.
   */
  @Test
  public void testDevAppServer_async() throws InterruptedException, IOException {
    GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStart")
        .build();

    AssertConnection.assertResponse(
        "http://localhost:8080", 200, "Hello from the App Engine Standard project.");

    GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStop")
        .build();

    // give the server a couple seconds to come down
    Thread.sleep(8000);

    AssertConnection.assertUnreachable("http://localhost:8080");
  }

  @Test
  public void testDeploy() throws ProcessRunnerException {
    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withDebug(true)
            .withArguments("appengineDeploy")
            .build();

    Assert.assertThat(
        buildResult.getOutput(),
        CoreMatchers.containsString("Deployed service [standard-project]"));

    CloudSdk cloudSdk =
        new CloudSdk.Builder().exitListener(new NonZeroExceptionExitListener()).build();
    cloudSdk.runAppCommand(Arrays.asList("services", "delete", "standard-project"));
  }
}
