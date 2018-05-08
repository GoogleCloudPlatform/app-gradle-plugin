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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.gradle.appengine.core.DeployTargetResolver;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class StandardDeployTargetResolverTest {
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_XML = "version-xml";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File appengineWebXml;

  /** Setup PropertyResolverTest. */
  @Before
  public void setup() throws IOException {
    appengineWebXml = new File(temporaryFolder.newFolder("source", "WEB-INF"), "appengine-web.xml");
    appengineWebXml.createNewFile();
    Files.write(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\"><application>"
            + PROJECT_XML
            + "</application><version>"
            + VERSION_XML
            + "</version></appengine-web-app>",
        appengineWebXml,
        Charsets.UTF_8);
  }

  @Test
  public void testGetProject_buildConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getProject("some-project");
    Assert.assertEquals("some-project", result);
  }

  @Test
  public void testGetProject_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getProject(DeployTargetResolver.APPENGINE_CONFIG);
    Assert.assertEquals(PROJECT_XML, result);
  }

  @Test
  public void testGetProject_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getProject(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetProject_nothingSet() throws IOException {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    try {
      String result = deployTargetResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "project must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.project = 'my-project-name'\n"
              + "2. set appengine.deploy.project = '"
              + DeployTargetResolver.APPENGINE_CONFIG
              + "' to use <application> from appengine-web.xml\n"
              + "3. set appengine.deploy.project = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to use project from gcloud config.",
          ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_buildConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getVersion("some-version");
    Assert.assertEquals("some-version", result);
  }

  @Test
  public void testGetVersion_appengineConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getVersion(DeployTargetResolver.APPENGINE_CONFIG);
    Assert.assertEquals(VERSION_XML, result);
  }

  @Test
  public void testGetVersion_gcloudConfig() {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    String result = deployTargetResolver.getVersion(DeployTargetResolver.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetVersion_nothingSet() throws IOException {
    DeployTargetResolver deployTargetResolver = new StandardDeployTargetResolver(appengineWebXml);
    try {
      String result = deployTargetResolver.getVersion(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "version must be defined or configured to read from system state\n"
              + "1. set appengine.deploy.version = 'my-version'\n"
              + "2. set appengine.deploy.version = '"
              + DeployTargetResolver.APPENGINE_CONFIG
              + "' to use <version> from appengine-web.xml\n"
              + "3. set appengine.deploy.version = '"
              + DeployTargetResolver.GCLOUD_CONFIG
              + "' to have gcloud generate a version for you.",
          ex.getMessage());
    }
  }
}
