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

import com.google.cloud.tools.gradle.appengine.BuildResultFilter;
import com.google.cloud.tools.gradle.appengine.core.AppEngineCorePluginConfiguration;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardExtension;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.cloud.tools.gradle.appengine.util.ExtensionUtil;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Test SourceContext plugin configuration. */
public class SourceContextPluginTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private void setUpTestProject() throws IOException {
    Path buildFile = testProjectDir.getRoot().toPath().resolve("build.gradle");

    Files.createDirectory(testProjectDir.getRoot().toPath().resolve("src"));
    InputStream buildFileContent =
        getClass()
            .getClassLoader()
            .getResourceAsStream("projects/SourceContextPluginTest/build.gradle");
    Files.copy(buildFileContent, buildFile);

    Path webInf = testProjectDir.getRoot().toPath().resolve("src/main/webapp/WEB-INF");
    Files.createDirectories(webInf);
    File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
    Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));
  }

  @Test
  public void testCreateSourceContextViaAssemble_taskTree() throws IOException {
    setUpTestProject();
    BuildResult buildResult =
        GradleRunner.create()
            .withProjectDir(testProjectDir.getRoot())
            .withPluginClasspath()
            .withArguments("assemble", "--dry-run")
            .build();

    final List<String> expected =
        Arrays.asList(
            ":_createSourceContext",
            ":compileJava",
            ":processResources",
            ":classes",
            ":war",
            ":explodeWar",
            ":assemble");

    Assert.assertEquals(expected, BuildResultFilter.extractTasks(buildResult));
  }

  @Test
  public void testDefaultConfiguration() throws IOException {
    File appengineWebXml =
        new File(testProjectDir.getRoot(), "src/main/webapp/WEB-INF/appengine-web.xml");
    appengineWebXml.getParentFile().mkdirs();
    appengineWebXml.createNewFile();
    Files.write(appengineWebXml.toPath(), "<web-app/>".getBytes());

    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
    project.getPluginManager().apply(JavaPlugin.class);
    project.getPluginManager().apply(WarPlugin.class);
    project.getPluginManager().apply(AppEngineStandardPlugin.class);
    project.getPluginManager().apply(SourceContextPlugin.class);

    DeployExtension deploy =
        project.getExtensions().getByType(AppEngineStandardExtension.class).getDeploy();
    deploy.setProjectId("project");
    deploy.setVersion("version");
    ((ProjectInternal) project).evaluate();

    ExtensionAware ext =
        (ExtensionAware)
            project.getExtensions().getByName(AppEngineCorePluginConfiguration.APPENGINE_EXTENSION);
    GenRepoInfoFileExtension genRepoInfoExt =
        new ExtensionUtil(ext).get(SourceContextPlugin.SOURCE_CONTEXT_EXTENSION);
    Assert.assertEquals(
        new File(project.getBuildDir(), "sourceContext"), genRepoInfoExt.getOutputDirectory());
  }
}
