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
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** End to end tests for standard projects. */
public class AppEngineStandardPluginIntegrationTest {

  /** Parameterize the project source for the test. */
  // @Parameters
  // public static Object[] data() {
  //   return new Object[][] {
  //     {"src/integTest/resources/projects/standard-project", "Dev App Server is now running"},
  //     {"src/integTest/resources/projects/standard-project-java8", "INFO:oejs.Server:main:
  // Started"}
  //   };
  // }

  @Rule public TemporaryFolder testProjectDir = new TemporaryFolder();

  private String testProjectSrcDirectory =
      "src/integTest/resources/projects/standard-project-java8";
  private String devAppServerStartedString = "INFO:oejs.Server:main: Started";

  // Used to fail tests when assertion fails outside of main thread
  private volatile Throwable threadException;

  /** Setup AppEngineStandardPluginIntegrationTest. */
  @Before
  public void setUp() throws IOException {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    FileUtils.copyDirectory(new File(testProjectSrcDirectory), testProjectDir.getRoot());
    threadException = null;
  }

  /** Cleanup AppEngineStandardPluginIntegrationTest. */
  @After
  public void cleanup() {
    System.clearProperty("deploy.read.appengine.web.xml");
  }

  @Test
  public void testDevAppServer_sync() throws IOException, InterruptedException {
    Thread.UncaughtExceptionHandler handler = (thread, throwable) -> threadException = throwable;
    Thread thread =
        new Thread(
            () -> {
              try {
                // Attempt to connect to server for 60 seconds
                AssertConnection.assertResponseWithRetries(
                    "http://localhost:8080",
                    200,
                    "Hello from the App Engine Standard project.",
                    60000);
              } catch (InterruptedException ex) {
                Assert.fail(ex.getMessage());
              } finally {
                // stop server
                try {
                  GradleRunner.create()
                      .withProjectDir(testProjectDir.getRoot())
                      .withPluginClasspath()
                      .withArguments("appengineStop")
                      .build();
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            });
    thread.setUncaughtExceptionHandler(handler);
    thread.setDaemon(true);
    thread.start();

    GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("appengineRun")
        .build();

    thread.join();

    if (threadException != null) {
      Assert.fail(threadException.getMessage());
    }

    AssertConnection.assertUnreachable("http://localhost:8080", 8000);
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
      throws CloudSdkNotFoundException, IOException, ProcessHandlerException,
          UnsupportedOsException {
    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withArguments("appengineDeploy", "--stacktrace")
            .build();

    Assert.assertThat(
        buildResult.getOutput(),
        CoreMatchers.containsString("Deployed service [standard-project]"));

    deleteProject();
  }

  @Test
  public void testDeployAll()
      throws CloudSdkNotFoundException, UnsupportedOsException, IOException,
          ProcessHandlerException {
    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withArguments("appengineDeployAll")
            .build();

    Assert.assertThat(
        buildResult.getOutput(),
        CoreMatchers.containsString("Deployed service [standard-project]"));
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
        .runCommand(Arrays.asList("app", "services", "delete", "standard-project", "--quiet"));
  }
}
