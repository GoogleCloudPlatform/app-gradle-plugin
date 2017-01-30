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

import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePlugin;
import com.google.cloud.tools.gradle.appengine.core.extension.Deploy;
import com.google.cloud.tools.gradle.appengine.core.extension.Tools;
import com.google.cloud.tools.gradle.appengine.standard.extension.Run;
import com.google.cloud.tools.gradle.appengine.standard.extension.StageStandard;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import com.google.common.base.Charsets;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppEngineStandardExtensionParserTest {

  @Rule
  public final TemporaryFolder testProjectDir = new TemporaryFolder();

  public Project setUpTestProject(String buildFileName) throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");
    InputStream buildFileContent = getClass().getClassLoader()
        .getResourceAsStream("projects/AppEnginePluginTest/Extension/" + buildFileName + ".gradle");
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

  @Test
  public void testFileAsString() throws IOException {
    Project p = setUpTestProject("file-as-string");

    ExtensionAware ext = (ExtensionAware) p.getExtensions()
        .getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    Deploy deploy = new ExtensionUtil(ext).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    StageStandard stage = new ExtensionUtil(ext).get(AppEngineStandardPlugin.STAGE_EXTENSION);
    Run run = new ExtensionUtil(ext).get(AppEngineStandardPlugin.RUN_EXTENSION);
    Tools tools = new ExtensionUtil(ext).get(AppEngineCorePlugin.TOOLS_EXTENSION);

    Assert.assertEquals(deploy.getDeployables().size(), 1);
    Assert.assertEquals("test", deploy.getDeployables().get(0).getName());
    Assert.assertEquals(run.getAppYamls().size(), 1);
    Assert.assertEquals("test", run.getAppYamls().get(0).getName());
    Assert.assertEquals("test", stage.getSourceDirectory().getName());
    Assert.assertEquals("test", stage.getStagingDirectory().getName());
    Assert.assertEquals("test", stage.getDockerfile().getName());
    Assert.assertEquals("test", tools.getCloudSdkHome().getName());
  }

  @Test
  public void testFilesAsString() throws IOException {
    Project p = setUpTestProject("files-as-string");

    ExtensionAware ext = (ExtensionAware) p.getExtensions()
        .getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    Deploy deploy = new ExtensionUtil(ext).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    Run run = new ExtensionUtil(ext).get(AppEngineStandardPlugin.RUN_EXTENSION);

    Assert.assertEquals(deploy.getDeployables().size(), 2);
    Assert.assertEquals("test0", deploy.getDeployables().get(0).getName());
    Assert.assertEquals("test1", deploy.getDeployables().get(1).getName());
    Assert.assertEquals(run.getAppYamls().size(), 2);
    Assert.assertEquals("test0", run.getAppYamls().get(0).getName());
    Assert.assertEquals("test1", run.getAppYamls().get(1).getName());
  }

  @Test
  public void testFileAsFile() throws IOException {
    Project p = setUpTestProject("file-as-file");

    ExtensionAware ext = (ExtensionAware) p.getExtensions()
        .getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    Deploy deploy = new ExtensionUtil(ext).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    StageStandard stage = new ExtensionUtil(ext).get(AppEngineStandardPlugin.STAGE_EXTENSION);
    Run run = new ExtensionUtil(ext).get(AppEngineStandardPlugin.RUN_EXTENSION);
    Tools tools = new ExtensionUtil(ext).get(AppEngineCorePlugin.TOOLS_EXTENSION);

    Assert.assertEquals(deploy.getDeployables().size(), 1);
    Assert.assertEquals("test", deploy.getDeployables().get(0).getName());
    Assert.assertEquals(run.getAppYamls().size(), 1);
    Assert.assertEquals("test", run.getAppYamls().get(0).getName());
    Assert.assertEquals("test", stage.getSourceDirectory().getName());
    Assert.assertEquals("test", stage.getStagingDirectory().getName());
    Assert.assertEquals("test", stage.getDockerfile().getName());
    Assert.assertEquals("test", tools.getCloudSdkHome().getName());
  }

  @Test
  public void testFilesAsFiles() throws IOException {
    Project p = setUpTestProject("files-as-files");

    ExtensionAware ext = (ExtensionAware) p.getExtensions()
        .getByName(AppEngineCorePlugin.APPENGINE_EXTENSION);
    Deploy deploy = new ExtensionUtil(ext).get(AppEngineCorePlugin.DEPLOY_EXTENSION);
    Run run = new ExtensionUtil(ext).get(AppEngineStandardPlugin.RUN_EXTENSION);

    Assert.assertEquals(deploy.getDeployables().size(), 2);
    Assert.assertEquals("test0", deploy.getDeployables().get(0).getName());
    Assert.assertEquals("test1", deploy.getDeployables().get(1).getName());
    Assert.assertEquals(run.getAppYamls().size(), 2);
    Assert.assertEquals("test0", run.getAppYamls().get(0).getName());
    Assert.assertEquals("test1", run.getAppYamls().get(1).getName());
  }
}
