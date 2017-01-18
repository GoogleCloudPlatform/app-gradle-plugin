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

package com.google.cloud.tools.gradle.appengine.sourcecontext;

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePlugin;
import com.google.cloud.tools.gradle.appengine.sourcecontext.extension.GenRepoInfoFileExtension;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import com.google.common.base.Charsets;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.util.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.internal.file.archive.ZipFileTree;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.tasks.bundling.War;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by appu on 1/17/17.
 */
public class SourceContextPluginTest {

  @Rule
  public final TemporaryFolder testProjectDir = new TemporaryFolder();

  public void setUpTestProject() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");

    Path src = Files.createDirectory(testProjectDir.getRoot().toPath().resolve("src"));
    InputStream buildFileContent = getClass().getClassLoader()
        .getResourceAsStream("projects/SourceContextPluginTest/build.gradle");
    Files.copy(buildFileContent, buildFile);

    Path gitContext = testProjectDir.getRoot().toPath().resolve("gitContext.zip");
    InputStream gitContextContent = getClass().getClassLoader()
        .getResourceAsStream("projects/SourceContextPluginTest/gitContext.zip");
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
  public void testCreateSourceContextViaAssemble_taskTree() throws IOException {
    setUpTestProject();
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments("assemble", "--dry-run")
        .build();

    final List<String> expected = Arrays
        .asList(":_createSourceContext", ":compileJava", ":processResources", ":classes", ":war",
            ":explodeWar", ":assemble");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testCreateSourceContextViaAssemble_checkFiles() throws IOException {
    setUpTestProject();
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(testProjectDir.getRoot())
        .withPluginClasspath()
        .withArguments(":assemble")
        .build();

    String commitHash = "9a282640c4a91769d328bbf23e8d8b2b5dcbbb5b";

    File sourceContextsFile = new File(testProjectDir.getRoot(), "build/exploded-app/WEB-INF/classes/source-contexts.json");
    Assert.assertTrue(sourceContextsFile.getAbsolutePath() + " is missing", sourceContextsFile.exists());
    Assert.assertTrue(com.google.common.io.Files.toString(sourceContextsFile, Charsets.UTF_8).contains(commitHash));

    File sourceContextFile = new File(testProjectDir.getRoot(), "build/exploded-app/WEB-INF/classes/source-context.json");
    Assert.assertTrue(sourceContextFile.getAbsolutePath() + " is missing", sourceContextFile.exists());
    Assert.assertTrue(com.google.common.io.Files.toString(sourceContextFile, Charsets.UTF_8).contains(commitHash));
  }

  public void testDefaultConfiguration() throws IOException {
    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();

    File appengineWebXml = new File(testProjectDir.getRoot(), "src/main/webapp/WEB-INF/appengine-web.xml");
    appengineWebXml.getParentFile().mkdirs();
    appengineWebXml.createNewFile();
    Files.write(appengineWebXml.toPath(), "<web-app/>".getBytes());

    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(WarPlugin.class);
    p.getPluginManager().apply(AppEngineStandardPlugin.class);
    ((ProjectInternal) p).evaluate();

    ExtensionAware ext = (ExtensionAware) p.getExtensions().getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    GenRepoInfoFileExtension genRepoInfoExt = new ExtensionUtil(ext).get(SourceContextPlugin.SOURCE_CONTEXT_EXTENSION);
    Assert.assertEquals(new File(p.getBuildDir(), "sourceContext"), genRepoInfoExt.getOutputDirectory());
  }
}
