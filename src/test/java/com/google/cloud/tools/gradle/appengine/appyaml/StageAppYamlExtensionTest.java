/*
 * Copyright 2019 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.appyaml;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;
import static org.junit.Assert.assertEquals;

import com.google.cloud.tools.appengine.configuration.AppYamlProjectStageConfiguration;
import com.google.cloud.tools.gradle.appengine.TestProject;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class StageAppYamlExtensionTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  private Project testContextProject;
  private File stagingDirectory;
  private File appEngineDirectory;
  private File artifact;
  private File dockerDirectory;
  private List<File> extraFilesDirectories;

  @Before
  public void setUpFiles() throws IOException {
    stagingDirectory = testProjectDir.newFolder("stage");
    appEngineDirectory = testProjectDir.newFolder("app");
    artifact = testProjectDir.newFile("artifact");
    dockerDirectory = testProjectDir.newFolder("docker");
    extraFilesDirectories =
        Arrays.asList(testProjectDir.newFolder("extra1"), testProjectDir.newFolder("extra2"));
    testContextProject = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
  }

  @Test
  public void testToAppYamlProjectStageConfiguration_allValuesSet() {
    StageAppYamlExtension extension = new StageAppYamlExtension(testContextProject);

    extension.setStagingDirectory(stagingDirectory);
    extension.setAppEngineDirectory(appEngineDirectory);
    extension.setArtifact(artifact);
    extension.setDockerDirectory(dockerDirectory);
    extension.setExtraFilesDirectories(extraFilesDirectories);

    AppYamlProjectStageConfiguration generatedConfig =
        extension.toAppYamlProjectStageConfiguration();
    assertEquals(appEngineDirectory.toPath(), generatedConfig.getAppEngineDirectory());
    assertEquals(stagingDirectory.toPath(), generatedConfig.getStagingDirectory());
    assertEquals(artifact.toPath(), generatedConfig.getArtifact());
    assertEquals(dockerDirectory.toPath(), generatedConfig.getDockerDirectory());
    assertEquals(
        extraFilesDirectories.stream().map(File::toPath).collect(Collectors.toList()),
        generatedConfig.getExtraFilesDirectory());
  }

  @Test
  public void testToAppYamlProjectStageConfiguration_nullExtraFiles() {
    StageAppYamlExtension extension = new StageAppYamlExtension(testContextProject);

    extension.setStagingDirectory(stagingDirectory);
    extension.setAppEngineDirectory(appEngineDirectory);
    extension.setArtifact(artifact);
    extension.setDockerDirectory(dockerDirectory);
    // extraFilesDirectories is not set (default = null)

    AppYamlProjectStageConfiguration generatedConfig =
        extension.toAppYamlProjectStageConfiguration();
    assertEquals(appEngineDirectory.toPath(), generatedConfig.getAppEngineDirectory());
    assertEquals(stagingDirectory.toPath(), generatedConfig.getStagingDirectory());
    assertEquals(artifact.toPath(), generatedConfig.getArtifact());
    assertEquals(dockerDirectory.toPath(), generatedConfig.getDockerDirectory());
    Assert.assertNull(generatedConfig.getExtraFilesDirectory());
  }

  @Test
  public void testToAppYamlProjectStageConfiguration_emptyExtraFiles() {
    StageAppYamlExtension extension = new StageAppYamlExtension(testContextProject);

    extension.setStagingDirectory(stagingDirectory);
    extension.setAppEngineDirectory(appEngineDirectory);
    extension.setArtifact(artifact);
    extension.setDockerDirectory(dockerDirectory);
    extension.setExtraFilesDirectories(Collections.emptyList());

    AppYamlProjectStageConfiguration generatedConfig =
        extension.toAppYamlProjectStageConfiguration();
    assertEquals(appEngineDirectory.toPath(), generatedConfig.getAppEngineDirectory());
    assertEquals(stagingDirectory.toPath(), generatedConfig.getStagingDirectory());
    assertEquals(artifact.toPath(), generatedConfig.getArtifact());
    assertEquals(dockerDirectory.toPath(), generatedConfig.getDockerDirectory());
    assertEquals(0, generatedConfig.getExtraFilesDirectory().size());
  }

  @Test
  public void testGetExtraFilesDirectoriesAsInputFiles_indirectFunctional() throws IOException {
    TestProject testProject = new TestProject(testProjectDir.getRoot())
        .addAppYamlBuildFileWithExtraFilesDirectories()
        .addAppYaml("java11");
    Files.write(
        testProject.getProjectRoot().toPath().resolve("src/main/extras/test1.txt"),
        "hello".getBytes(Charsets.UTF_8));

    BuildResult firstRun = testProject.applyGradleRunner("appengineStage");
    assertEquals(SUCCESS, firstRun.task(":appengineStage").getOutcome());

    BuildResult secondRunNoChange = testProject.applyGradleRunner("appengineStage");
    assertEquals(UP_TO_DATE, secondRunNoChange.task(":appengineStage").getOutcome());

    Files.write(
        testProject.getProjectRoot().toPath().resolve("src/main/extras/test2.txt"),
        "hello".getBytes(Charsets.UTF_8));

    BuildResult runWithNewFileAdded = testProject.applyGradleRunner("appengineStage");
    assertEquals(SUCCESS, runWithNewFileAdded.task(":appengineStage").getOutcome());
  }

}
