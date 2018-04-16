/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

public class DeployExtensionTest {
  private static final String PROJECT_BUILD = "project-build";
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_BUILD = "version-build";
  private static final String VERSION_XML = "version-xml";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File appengineWebXml;

  private DeployExtension deployExtension;

  /** Setup DeployExtensionTest. */
  @Before
  public void setup() throws IOException {
    System.clearProperty("deploy.read.appengine.web.xml");
    deployExtension = new DeployExtension(ProjectBuilder.builder().build());
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

  /** Cleanup DeployExtensionTest. */
  @After
  public void cleanup() {
    System.clearProperty("deploy.read.appengine.web.xml");
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_flexible()
      throws AppEngineException, SAXException, IOException {
    deployExtension.setVersion(VERSION_BUILD);
    deployExtension.setProject(PROJECT_BUILD);
    DeployExtension result = deployExtension.withPropertiesFromAppEngineWebXml();
    Assert.assertEquals(VERSION_BUILD, result.getVersion());
    Assert.assertEquals(PROJECT_BUILD, result.getProject());
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_buildConfig()
      throws AppEngineException, SAXException, IOException {
    deployExtension.setAppEngineWebXml(appengineWebXml);
    deployExtension.setVersion(VERSION_BUILD);
    deployExtension.setProject(PROJECT_BUILD);
    DeployExtension result = deployExtension.withPropertiesFromAppEngineWebXml();
    Assert.assertEquals(VERSION_BUILD, result.getVersion());
    Assert.assertEquals(PROJECT_BUILD, result.getProject());
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_xml()
      throws AppEngineException, SAXException, IOException {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    deployExtension.setAppEngineWebXml(appengineWebXml);
    DeployExtension result = deployExtension.withPropertiesFromAppEngineWebXml();
    Assert.assertEquals(VERSION_XML, result.getVersion());
    Assert.assertEquals(PROJECT_XML, result.getProject());
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_nothingSet() throws IOException {
    appengineWebXml.createNewFile();
    Files.write(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">"
            + "</appengine-web-app>",
        appengineWebXml,
        Charsets.UTF_8);
    try {
      deployExtension.setAppEngineWebXml(appengineWebXml);
    } catch (GradleException ex) {
      Assert.assertEquals(
          "appengine-plugin does not use gcloud global project state. Please configure the "
              + "application ID and version in your build.gradle or appengine-web.xml.",
          ex.getMessage());
    }
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_sysPropertyBothSet() {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    deployExtension.setProject(PROJECT_BUILD);
    deployExtension.setProject(VERSION_BUILD);
    try {
      deployExtension.setAppEngineWebXml(appengineWebXml);
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
              + "the project/version properties from your build.gradle, or clear the "
              + "deploy.read.appengine.web.xml system property to read from build.gradle.",
          ex.getMessage());
    }
  }

  @Test
  public void testWithPropertiesFromAppEngineWebXml_noSysPropertyOnlyXml() {
    try {
      deployExtension.setAppEngineWebXml(appengineWebXml);
    } catch (GradleException ex) {
      Assert.assertEquals(
          "appengine-plugin does not use gcloud global project state. If you would like to "
              + "use the state from appengine-web.xml, please set the system property "
              + "deploy.read.appengine.web.xml",
          ex.getMessage());
    }
  }
}
