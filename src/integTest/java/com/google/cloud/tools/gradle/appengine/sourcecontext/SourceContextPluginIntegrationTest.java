/*
 * Copyright 2017 Google LLC. All Rights Reserved.
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

package com.google.cloud.tools.gradle.appengine.sourcecontext;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.util.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for SourceContext plugin that use git context information. */
public class SourceContextPluginIntegrationTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  /** Create a test project with git source context. */
  public void setUpTestProject() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");

    Path src = Files.createDirectory(testProjectDir.getRoot().toPath().resolve("src"));
    InputStream buildFileContent =
        getClass()
            .getClassLoader()
            .getResourceAsStream("projects/sourcecontext-project/build.gradle");
    Files.copy(buildFileContent, buildFile);

    Path gitContext = testProjectDir.getRoot().toPath().resolve("gitContext.zip");
    InputStream gitContextContent =
        getClass()
            .getClassLoader()
            .getResourceAsStream("projects/sourcecontext-project/gitContext.zip");
    Files.copy(gitContextContent, gitContext);

    try (ZipFile zipFile = new ZipFile(gitContext.toFile())) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        File entryDestination = new File(testProjectDir.getRoot(), entry.getName());
        if (entry.isDirectory()) {
          entryDestination.mkdirs();
        } else {
          entryDestination.getParentFile().mkdirs();
          InputStream in = zipFile.getInputStream(entry);
          OutputStream out = new FileOutputStream(entryDestination);
          IOUtils.copy(in, out);
          IOUtils.closeQuietly(in);
          out.close();
        }
      }
    }

    FileUtils.delete(gitContext.toFile());

    Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));
  }

  @Test
  public void testCreateSourceContext() throws IOException {
    setUpTestProject();
    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withArguments(":assemble")
            .build();

    String commitHash = "9a282640c4a91769d328bbf23e8d8b2b5dcbbb5b";

    File sourceContextFile =
        new File(
            testProjectDir.getRoot(),
            "build/exploded-"
                + testProjectDir.getRoot().getName() // this is project.name
                + "/WEB-INF/classes/source-context.json");
    Assert.assertTrue(
        sourceContextFile.getAbsolutePath() + " is missing", sourceContextFile.exists());
    Assert.assertTrue(
        com.google.common.io.Files.asCharSource(sourceContextFile, Charsets.UTF_8)
            .read()
            .contains(commitHash));
  }
}
