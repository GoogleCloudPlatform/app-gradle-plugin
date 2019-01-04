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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.operations.Gcloud;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkNotFoundException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkOutOfDateException;
import com.google.cloud.tools.appengine.operations.cloudsdk.CloudSdkVersionFileException;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandlerException;
import com.google.cloud.tools.appengine.operations.cloudsdk.serialization.CloudSdkConfig;
import com.google.cloud.tools.gradle.appengine.core.CloudSdkOperations;
import com.google.cloud.tools.gradle.appengine.core.ConfigReader;
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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardDeployTargetResolverTest {
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_XML = "version-xml";
  private static final String PROJECT_GCLOUD = "project-gcloud";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File appengineWebXml;

  @Mock Gcloud gcloud;
  @Mock CloudSdkOperations cloudSdkOperations;
  @Mock CloudSdkConfig cloudSdkConfig;

  /** Setup PropertyResolverTest. */
  @Before
  public void setup()
      throws IOException, CloudSdkNotFoundException, ProcessHandlerException,
          CloudSdkOutOfDateException, CloudSdkVersionFileException {
    appengineWebXml = new File(temporaryFolder.newFolder("source", "WEB-INF"), "appengine-web.xml");
    Mockito.when(cloudSdkOperations.getGcloud()).thenReturn(gcloud);
    Mockito.when(gcloud.getConfig()).thenReturn(cloudSdkConfig);
    Mockito.when(cloudSdkConfig.getProject()).thenReturn(PROJECT_GCLOUD);

    appengineWebXml.createNewFile();
    Files.asCharSink(appengineWebXml, Charsets.UTF_8)
        .write(
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\"><application>"
                + PROJECT_XML
                + "</application><version>"
                + VERSION_XML
                + "</version></appengine-web-app>");
  }

  @Test
  public void testGetProject_buildConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getProject("some-project");
    Assert.assertEquals("some-project", result);
  }

  @Test
  public void testGetProject_appengineConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getProject(ConfigReader.APPENGINE_CONFIG);
    Assert.assertEquals(PROJECT_XML, result);
  }

  @Test
  public void testGetProject_gcloudConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getProject(ConfigReader.GCLOUD_CONFIG);
    Assert.assertEquals(PROJECT_GCLOUD, result);
  }

  @Test
  public void testGetProject_nothingSet() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    try {
      deployTargetResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(StandardDeployTargetResolver.PROJECT_ERROR, ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_buildConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getVersion("some-version");
    Assert.assertEquals("some-version", result);
  }

  @Test
  public void testGetVersion_appengineConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getVersion(ConfigReader.APPENGINE_CONFIG);
    Assert.assertEquals(VERSION_XML, result);
  }

  @Test
  public void testGetVersion_gcloudConfig() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    String result = deployTargetResolver.getVersion(ConfigReader.GCLOUD_CONFIG);
    Assert.assertNull(result);
  }

  @Test
  public void testGetVersion_nothingSet() {
    StandardDeployTargetResolver deployTargetResolver =
        new StandardDeployTargetResolver(appengineWebXml, cloudSdkOperations);
    try {
      deployTargetResolver.getVersion(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(StandardDeployTargetResolver.VERSION_ERROR, ex.getMessage());
    }
  }
}
