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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PropertyResolverTest {
  private static final String PROJECT_BUILD = "project-build";
  private static final String PROJECT_XML = "project-xml";
  private static final String VERSION_BUILD = "version-build";
  private static final String VERSION_XML = "version-xml";

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private File appengineWebXml;

  private PropertyResolver propertyResolver;

  /** Setup PropertyResolverTest. */
  @Before
  public void setup() throws IOException {
    System.clearProperty("deploy.read.appengine.web.xml");
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

  /** Cleanup PropertyResolverTest. */
  @After
  public void cleanup() {
    System.clearProperty("deploy.read.appengine.web.xml");
  }

  @Test
  public void testGetProject_buildConfig() {
    propertyResolver = new PropertyResolver(appengineWebXml);
    String result = propertyResolver.getProject(PROJECT_BUILD);
    Assert.assertEquals(PROJECT_BUILD, result);
  }

  @Test
  public void testGetProject_xml() {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    propertyResolver = new PropertyResolver(appengineWebXml);
    String result = propertyResolver.getProject(null);
    Assert.assertEquals(PROJECT_XML, result);
  }

  @Test
  public void testGetProject_nothingSet() throws IOException {
    appengineWebXml.createNewFile();
    Files.write(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">"
            + "</appengine-web-app>",
        appengineWebXml,
        Charsets.UTF_8);
    propertyResolver = new PropertyResolver(appengineWebXml);
    try {
      propertyResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "appengine-plugin does not use gcloud global project state. Please configure the "
              + "application ID in your build.gradle or appengine-web.xml.",
          ex.getMessage());
    }
  }

  @Test
  public void testGetProject_sysPropertyBothSet() {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    propertyResolver = new PropertyResolver(appengineWebXml);
    try {
      propertyResolver.getProject(PROJECT_BUILD);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
              + "the project/version properties from your build.gradle, or clear the "
              + "deploy.read.appengine.web.xml system property to read from build.gradle.",
          ex.getMessage());
    }
  }

  @Test
  public void testGetProject_noSysPropertyOnlyXml() {
    propertyResolver = new PropertyResolver(appengineWebXml);
    try {
      propertyResolver.getProject(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "appengine-plugin does not use gcloud global project state. If you would like to "
              + "use the state from appengine-web.xml, please set the system property "
              + "deploy.read.appengine.web.xml=true.",
          ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_buildConfig() {
    propertyResolver = new PropertyResolver(appengineWebXml);
    String result = propertyResolver.getVersion(VERSION_BUILD);
    Assert.assertEquals(VERSION_BUILD, result);
  }

  @Test
  public void testGetVersion_xml() {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    propertyResolver = new PropertyResolver(appengineWebXml);
    String result = propertyResolver.getVersion(null);
    Assert.assertEquals(VERSION_XML, result);
  }

  @Test
  public void testGetVersion_nothingSet() throws IOException {
    appengineWebXml.createNewFile();
    Files.write(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">"
            + "</appengine-web-app>",
        appengineWebXml,
        Charsets.UTF_8);
    propertyResolver = new PropertyResolver(appengineWebXml);
    String result = propertyResolver.getVersion(null);
    Assert.assertEquals(null, result);
  }

  @Test
  public void testGetVersion_sysPropertyBothSet() {
    System.setProperty("deploy.read.appengine.web.xml", "true");
    propertyResolver = new PropertyResolver(appengineWebXml);
    try {
      propertyResolver.getVersion(VERSION_BUILD);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Cannot override appengine.deploy config with appengine-web.xml. Either remove "
              + "the project/version properties from your build.gradle, or clear the "
              + "deploy.read.appengine.web.xml system property to read from build.gradle.",
          ex.getMessage());
    }
  }

  @Test
  public void testGetVersion_noSysPropertyOnlyXml() {
    propertyResolver = new PropertyResolver(appengineWebXml);
    try {
      propertyResolver.getVersion(null);
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "appengine-plugin does not use gcloud global project state. If you would like to "
              + "use the state from appengine-web.xml, please set the system property "
              + "deploy.read.appengine.web.xml=true.",
          ex.getMessage());
    }
  }
}
