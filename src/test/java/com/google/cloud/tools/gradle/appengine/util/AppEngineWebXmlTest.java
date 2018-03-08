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

package com.google.cloud.tools.gradle.appengine.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for appengine-web.xml parsing */
public class AppEngineWebXmlTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Test
  public void testIsVm_true() throws IOException {
    Assert.assertTrue(AppEngineWebXml.parse(createAppEngineWebXml("<vm>true</vm>")).isVm());
  }

  @Test
  public void testIsVm_false() throws IOException {
    Assert.assertFalse(AppEngineWebXml.parse(createAppEngineWebXml("<vm>false</vm>")).isVm());
    Assert.assertFalse(AppEngineWebXml.parse(createAppEngineWebXml("<vm>TRUE</vm>")).isVm());
    Assert.assertFalse(AppEngineWebXml.parse(createAppEngineWebXml("<vm>junk</vm>")).isVm());
    Assert.assertFalse(AppEngineWebXml.parse(createAppEngineWebXml("<vm></vm>")).isVm());
    Assert.assertFalse(AppEngineWebXml.parse(createAppEngineWebXml("")).isVm());
  }

  private File createAppEngineWebXml(String content) throws IOException {
    File appengienWebXml = testProjectDir.newFile();
    try (FileWriter writer = new FileWriter(appengienWebXml)) {
      writer.write("<appengine-web-app>" + content + "</appengine-web-app>");
    }
    return appengienWebXml;
  }
}
