/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine.core;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.appengine.cloudsdk.AppEngineJavaComponentsNotInstalledException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.cloudsdk.serialization.CloudSdkVersion;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckCloudSdkTaskTest {

  @Mock private ToolsExtension toolsExtension;
  @Mock private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Mock private CloudSdk.Builder builder;
  @Mock private CloudSdk sdk;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private CheckCloudSdkTask checkCloudSdkTask;

  /** Setup CheckCloudSdkTaskTest. */
  @Before
  public void setup() {
    Project tempProject = ProjectBuilder.builder().build();
    checkCloudSdkTask = tempProject.getTasks().create("tempCheckCloudSdk", CheckCloudSdkTask.class);
    checkCloudSdkTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
    checkCloudSdkTask.setToolsExtension(toolsExtension);

    when(cloudSdkBuilderFactory.newBuilder()).thenReturn(builder);
    when(builder.build()).thenReturn(sdk);
  }

  @Test
  public void testCheckCloudSdkAction_nullHome() {
    when(toolsExtension.getCloudSdkHome()).thenReturn(null);

    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals("SDK home directory must be specified for validation.", ex.getMessage());
    }
  }

  @Test
  public void testCheckCloudSdkAction_versionMismatch() throws IOException {
    when(toolsExtension.getCloudSdkHome()).thenReturn(temporaryFolder.newFolder());

    when(toolsExtension.getCloudSdkVersion()).thenReturn("191.0.0");
    when(sdk.getVersion()).thenReturn(new CloudSdkVersion("190.0.0"));

    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Specified Cloud SDK version (191.0.0) does not match installed version (190.0.0).",
          ex.getMessage());
    }
  }

  @Test
  public void testCheckCloudSdkAction_sdkInstallationExceptions() throws IOException {
    when(toolsExtension.getCloudSdkHome()).thenReturn(temporaryFolder.newFolder());
    when(toolsExtension.getCloudSdkVersion()).thenReturn("LATEST");

    doThrow(CloudSdkNotFoundException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      // Pass
    }

    doThrow(CloudSdkOutOfDateException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      // Pass
    }

    doThrow(CloudSdkVersionFileException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      // Pass
    }
  }

  @Test
  public void testCheckCloudSdkAction_appEngineInstallationExceptions() throws IOException {
    when(toolsExtension.getCloudSdkHome()).thenReturn(temporaryFolder.newFolder());
    when(toolsExtension.getCloudSdkVersion()).thenReturn("LATEST");

    doThrow(AppEngineJavaComponentsNotInstalledException.class)
        .when(sdk)
        .validateAppEngineJavaComponents();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      // Pass
    }
  }
}
