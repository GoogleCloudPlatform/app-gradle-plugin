/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.operations.Gcloud;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandlerException;
import com.google.cloud.tools.appengine.operations.cloudsdk.serialization.CloudSdkConfig;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeployTargetResolverTest {
  private static final String PROJECT_GCLOUD = "project-gcloud";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Mock Gcloud gcloud;
  @Mock CloudSdkOperations cloudSdkOperations;
  @Mock CloudSdkConfig cloudSdkConfig;

  @Before
  public void setup()
      throws CloudSdkNotFoundException, ProcessHandlerException, CloudSdkOutOfDateException,
          CloudSdkVersionFileException, IOException {
    Mockito.when(cloudSdkOperations.getGcloud()).thenReturn(gcloud);
    Mockito.when(gcloud.getConfig()).thenReturn(cloudSdkConfig);
    Mockito.when(cloudSdkConfig.getProject()).thenReturn(PROJECT_GCLOUD);
  }

  @Test
  public void testGetProject_buildConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    String result = deployTargetResolver.getProject("some-project");
    Assert.assertEquals("some-project", result);
  }

  @Test
  public void testGetProject_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    try {
      deployTargetResolver.getProject(DeployTargetResolver.APPENGINE_CONFIG);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(DeployTargetResolver.PROJECT_ERROR, ex.getMessage());
    }
  }

  @Test
  public void testGetProject_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    String result = deployTargetResolver.getProject(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertEquals(PROJECT_GCLOUD, result);
  }

  @Test
  public void testGetProject_nothingSet() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    try {
      deployTargetResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(DeployTargetResolver.PROJECT_ERROR, ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_buildConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    String result = deployTargetResolver.getVersion("some-version");
    Assert.assertEquals("some-version", result);
  }

  @Test
  public void testGetVersion_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    try {
      deployTargetResolver.getVersion(DeployTargetResolver.APPENGINE_CONFIG);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(DeployTargetResolver.VERSION_ERROR, ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    String result = deployTargetResolver.getVersion(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetVersion_nothingSet() {
    DeployTargetResolver deployTargetResolver = new DeployTargetResolver(cloudSdkOperations);
    try {
      deployTargetResolver.getVersion(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(DeployTargetResolver.VERSION_ERROR, ex.getMessage());
    }
  }
}
