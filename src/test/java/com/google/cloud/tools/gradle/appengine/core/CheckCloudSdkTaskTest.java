/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckCloudSdkTaskTest {

  @Mock private CloudSdkBuilderFactory cloudSdkBuilderFactory;

  @Mock private CloudSdk.Builder builder;
  @Mock private CloudSdk sdk;

  private CheckCloudSdkTask checkCloudSdkTask;

  /** Setup CheckCloudSdkTaskTest. */
  @Before
  public void setup() {
    Project tempProject = ProjectBuilder.builder().build();
    checkCloudSdkTask = tempProject.getTasks().create("tempCheckCloudSdk", CheckCloudSdkTask.class);
    checkCloudSdkTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);

    when(cloudSdkBuilderFactory.newBuilder(Mockito.any(Logger.class))).thenReturn(builder);
    when(builder.build()).thenReturn(sdk);
  }

  @Test
  public void testCheckCloudSdkAction_nullVersion() {
    checkCloudSdkTask.setVersion(null);
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Cloud SDK home path and version must be configured in order to run this task.",
          ex.getMessage());
    }
  }

  @Test
  public void testCheckCloudSdkAction_versionMismatch() {
    checkCloudSdkTask.setVersion("191.0.0");
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
  public void testCheckCloudSdkAction_sdkInstallationException() {
    checkCloudSdkTask.setVersion("192.0.0");
    when(sdk.getVersion()).thenReturn(new CloudSdkVersion("192.0.0"));

    doThrow(CloudSdkNotFoundException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      Assert.assertEquals(ex.getCause().getClass(), CloudSdkNotFoundException.class);
    }
  }

  @Test
  public void testCheckCloudSdkAction_outOfDateException() {
    checkCloudSdkTask.setVersion("192.0.0");
    when(sdk.getVersion()).thenReturn(new CloudSdkVersion("192.0.0"));

    doThrow(CloudSdkOutOfDateException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      Assert.assertEquals(ex.getCause().getClass(), CloudSdkOutOfDateException.class);
    }
  }

  @Test
  public void testCheckCloudSdkAction_versionFileException() {
    checkCloudSdkTask.setVersion("192.0.0");
    when(sdk.getVersion()).thenReturn(new CloudSdkVersion("192.0.0"));

    doThrow(CloudSdkVersionFileException.class).when(sdk).validateCloudSdk();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      Assert.assertEquals(ex.getCause().getClass(), CloudSdkVersionFileException.class);
    }
  }

  @Test
  public void testCheckCloudSdkAction_appEngineInstallationExceptions() {
    checkCloudSdkTask.setVersion("192.0.0");
    when(sdk.getVersion()).thenReturn(new CloudSdkVersion("192.0.0"));

    doThrow(AppEngineJavaComponentsNotInstalledException.class)
        .when(sdk)
        .validateAppEngineJavaComponents();
    try {
      checkCloudSdkTask.checkCloudSdkAction();
      Assert.fail();
    } catch (TaskExecutionException ex) {
      Assert.assertEquals(
          ex.getCause().getClass(), AppEngineJavaComponentsNotInstalledException.class);
    }
  }
}
