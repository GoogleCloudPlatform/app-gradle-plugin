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

package com.google.cloud.tools.gradle.appengine.flexible;

import com.google.cloud.tools.gradle.appengine.core.DeployTargetResolver;
import org.gradle.api.GradleException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FlexibleDeployTargetResolverTest {
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_XML = "version-xml";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void testGetProject_buildConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    String result = deployTargetResolver.getProject("some-project");
    Assert.assertEquals("some-project", result);
  }

  @Test
  public void testGetProject_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    try {
      deployTargetResolver.getProject(DeployTargetResolver.APPENGINE_CONFIG);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "project must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.project = 'my-project-name'\n"
              + "2. set appengine.deploy.project = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to use project from gcloud config.\n"
              + "3. using "
              + DeployTargetResolver.APPENGINE_CONFIG
              + " is not allowed for flexible environment projects",
          ex.getMessage());
    }
  }

  @Test
  public void testGetProject_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    String result = deployTargetResolver.getProject(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetProject_nothingSet() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    try {
      deployTargetResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "project must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.project = 'my-project-name'\n"
              + "2. set appengine.deploy.project = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to use project from gcloud config.\n"
              + "3. using "
              + DeployTargetResolver.APPENGINE_CONFIG
              + " is not allowed for flexible environment projects",
          ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_buildConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    String result = deployTargetResolver.getVersion("some-version");
    Assert.assertEquals("some-version", result);
  }

  @Test
  public void testGetVersion_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    try {
      deployTargetResolver.getVersion(DeployTargetResolver.APPENGINE_CONFIG);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "version must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.version = 'my-version'\n"
              + "2. set appengine.deploy.version = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to have gcloud generate a version for you.\n"
              + "3. using "
              + DeployTargetResolver.APPENGINE_CONFIG
              + " is not allowed for flexible environment projects",
          ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    String result = deployTargetResolver.getVersion(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetVersion_nothingSet() {
    DeployTargetResolver deployTargetResolver = new FlexibleDeployTargetResolver();
    try {
      deployTargetResolver.getVersion(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "version must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.version = 'my-version'\n"
              + "2. set appengine.deploy.version = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to have gcloud generate a version for you.\n"
              + "3. using "
              + DeployTargetResolver.APPENGINE_CONFIG
              + " is not allowed for flexible environment projects",
          ex.getMessage());
    }
  }
}
