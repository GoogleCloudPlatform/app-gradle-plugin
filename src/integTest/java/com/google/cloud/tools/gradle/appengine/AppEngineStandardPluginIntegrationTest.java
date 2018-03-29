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

import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.cloudsdk.InvalidJavaSdkException;
import com.google.cloud.tools.appengine.cloudsdk.internal.process.ProcessRunnerException;
import com.google.cloud.tools.appengine.cloudsdk.process.NonZeroExceptionExitListener;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/** End to end tests for standard projects. */
@RunWith(Parameterized.class)
public class AppEngineStandardPluginIntegrationTest {

  /** Parameterize the project source for the test. */
  @Parameters
  public static Object[] data() {
    return new Object[][] {
      {"src/integTest/resources/projects/standard-project", "Dev App Server is now running"},
      {"src/integTest/resources/projects/standard-project-java8", "INFO:oejs.Server:main: Started"}
    };
  }

  @Rule public Timeout globalTimeout = Timeout.seconds(180);

  @Rule public TemporaryFolder testProjectDir = new TemporaryFolder();

  @Parameter(0)
  public String testProjectSrcDirectory;

  @Parameter(1)
  public String devAppServerStartedString;

  @Before
  public void setUp() throws IOException {
    FileUtils.copyDirectory(new File(testProjectSrcDirectory), testProjectDir.getRoot());
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

    File expectedLogFileDir =
        new File(
            testProjectDir.getRoot(),
            "/build/" + AppEngineStandardPlugin.DEV_APP_SERVER_OUTPUT_DIR_NAME);

    Assert.assertEquals(1, expectedLogFileDir.listFiles().length);
    File devAppserverLogFile = new File(expectedLogFileDir, "dev_appserver.out");
    String devAppServerOutput = FileUtils.readFileToString(devAppserverLogFile);
    Assert.assertTrue(devAppServerOutput.contains(devAppServerStartedString));

    AssertConnection.assertResponse(
        "http://localhost:8080", 200, "Hello from the App Engine Standard project.");

    GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineStop")
        .build();

    AssertConnection.assertUnreachable("http://localhost:8080", 8000);
  }

  @Test
  public void testDeploy()
      throws ProcessRunnerException, IOException, CloudSdkNotFoundException,
          InvalidJavaSdkException, CloudSdkVersionFileException, CloudSdkOutOfDateException {
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
