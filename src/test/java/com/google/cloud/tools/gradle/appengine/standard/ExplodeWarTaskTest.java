/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
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

import com.google.cloud.tools.gradle.appengine.TestProject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExplodeWarTaskTest {

  @Rule
  public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Test
  public void testSyncTask() throws IOException {
    BuildResult buildResult =
        new TestProject(testProjectDir.getRoot())
            .addStandardBuildFile()
            .addAppEngineWebXml()
            .applyGradleRunner("explodeWar");

    Path explodedApp = testProjectDir.getRoot().toPath().resolve("build").resolve("exploded-" + testProjectDir.getRoot().getName());
    Path appengineGenerated = explodedApp.resolve("WEB-INF").resolve("appengine-generated");
    Path junkXml = appengineGenerated.resolve("junk.xml");
    Path datastoreIndexesAutoXml = appengineGenerated.resolve("datastore-indexes-auto.xml");

    Assert.assertTrue(Files.isDirectory(explodedApp));
    Assert.assertFalse(Files.isRegularFile(datastoreIndexesAutoXml));
    Assert.assertFalse(Files.isRegularFile(junkXml));

    // creates files that we will test persistence on
    Files.createDirectory(appengineGenerated);
    Files.createFile(junkXml);
    Files.createFile(datastoreIndexesAutoXml);

    // run explodeWar again to see if we preserve datastore-indexes-auto and remove junk, force
    // it to skip UP-TO-DATE checks
    new TestProject(testProjectDir.getRoot()).applyGradleRunner("explodeWar", "--rerun-tasks");

    Assert.assertTrue(Files.isRegularFile(datastoreIndexesAutoXml));
    Assert.assertFalse(Files.isRegularFile(junkXml));
  }
}
