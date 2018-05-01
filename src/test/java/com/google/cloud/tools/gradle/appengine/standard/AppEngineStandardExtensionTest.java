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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests to check we are parsing objects -> file/files correctly. */
public class AppEngineStandardExtensionTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private Project setUpTestProject(String buildFileName) throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    InputStream buildFileContent =
        getClass()
            .getClassLoader()
            .getResourceAsStream(
                "projects/AppEnginePluginTest/Extension/" + buildFileName + ".gradle");
    Files.copy(buildFileContent, buildFile);

    Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));

    Project p = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    p.getPluginManager().apply(JavaPlugin.class);
    p.getPluginManager().apply(WarPlugin.class);
    p.getPluginManager().apply(AppEngineStandardPlugin.class);
    ((ProjectInternal) p).evaluate();

    return p;
  }

  // TODO : Make this a test that reads ALL params
  @Test
  public void testReadEnvironment() throws IOException {
    Project p = setUpTestProject("environment-params");

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    RunExtension run = ext.getRun();

    Assert.assertEquals(run.getEnvironment(), ImmutableMap.of("key1", "value1", "key2", "value2"));
  }

  @Test
  public void testFileAsString() throws IOException {
    Project p = setUpTestProject("file-as-string");

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    StageStandardExtension stage = ext.getStage();
    RunExtension run = ext.getRun();
    ToolsExtension tools = ext.getTools();

    Assert.assertEquals(run.getServices().size(), 1);
    Assert.assertEquals("test", run.getServices().get(0).getName());
    Assert.assertEquals("test", stage.getSourceDirectory().getName());
    Assert.assertEquals("test", stage.getStagingDirectory().getName());
    Assert.assertEquals("test", stage.getDockerfile().getName());
    Assert.assertEquals("test", tools.getCloudSdkHome().getName());
  }

  @Test
  public void testFilesAsString() throws IOException {
    Project p = setUpTestProject("files-as-string");

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    RunExtension run = ext.getRun();

    Assert.assertEquals(run.getServices().size(), 2);
    Assert.assertEquals("test0", run.getServices().get(0).getName());
    Assert.assertEquals("test1", run.getServices().get(1).getName());
  }

  @Test
  public void testFileAsFile() throws IOException {
    Project p = setUpTestProject("file-as-file");

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    RunExtension run = ext.getRun();
    StageStandardExtension stage = ext.getStage();
    ToolsExtension tools = ext.getTools();

    Assert.assertEquals(run.getServices().size(), 1);
    Assert.assertEquals("test", run.getServices().get(0).getName());
    Assert.assertEquals("test", stage.getSourceDirectory().getName());
    Assert.assertEquals("test", stage.getStagingDirectory().getName());
    Assert.assertEquals("test", stage.getDockerfile().getName());
    Assert.assertEquals("test", tools.getCloudSdkHome().getName());
  }

  @Test
  public void testFilesAsFiles() throws IOException {
    Project p = setUpTestProject("files-as-files");

    AppEngineStandardExtension ext = p.getExtensions().getByType(AppEngineStandardExtension.class);
    RunExtension run = ext.getRun();

    Assert.assertEquals(run.getServices().size(), 2);
    Assert.assertEquals("test0", run.getServices().get(0).getName());
    Assert.assertEquals("test1", run.getServices().get(1).getName());
  }
}
