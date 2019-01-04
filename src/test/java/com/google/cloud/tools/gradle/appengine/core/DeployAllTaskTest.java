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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.appengine.AppEngineException;
import com.google.cloud.tools.appengine.configuration.DeployConfiguration;
import com.google.cloud.tools.appengine.operations.Deployment;
import com.google.cloud.tools.appengine.operations.Gcloud;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeployAllTaskTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private Gcloud gcloud;
  @Mock private Deployment deploy;
  @Mock private DeployTargetResolver deployTargetResolver;

  private DeployExtension deployExtension;
  private ArgumentCaptor<DeployConfiguration> deployCapture;

  private DeployAllTask deployAllTask;

  private File stageDir;

  /** Setup DeployAllTaskTest. */
  @Before
  public void setup() throws IOException {
    Project tempProject = ProjectBuilder.builder().build();
    deployExtension = new DeployExtension(tempProject);
    deployExtension.setDeployTargetResolver(deployTargetResolver);
    deployCapture = ArgumentCaptor.forClass(DeployConfiguration.class);
    stageDir = tempFolder.newFolder("staging");

    deployAllTask = tempProject.getTasks().create("tempDeployAllTask", DeployAllTask.class);
    deployAllTask.setDeployExtension(deployExtension);
    deployAllTask.setGcloud(gcloud);
    deployAllTask.setStageDirectory(stageDir);

    when(gcloud.newDeployment(Mockito.any(ProcessHandler.class))).thenReturn(deploy);
  }

  @Test
  public void testDeployAllAction_standard() throws AppEngineException, IOException {
    deployExtension.setAppEngineDirectory(stageDir);

    final Path appYaml = tempFolder.newFile("staging/app.yaml").toPath();
    final Path cronYaml = tempFolder.newFile("staging/cron.yaml").toPath();
    final Path dispatchYaml = tempFolder.newFile("staging/dispatch.yaml").toPath();
    final Path dosYaml = tempFolder.newFile("staging/dos.yaml").toPath();
    final Path indexYaml = tempFolder.newFile("staging/index.yaml").toPath();
    final Path queueYaml = tempFolder.newFile("staging/queue.yaml").toPath();
    final Path invalidYaml = tempFolder.newFile("staging/invalid.yaml").toPath();

    deployAllTask.deployAllAction();

    verify(deploy).deploy(deployCapture.capture());
    DeployConfiguration captured = deployCapture.getValue();
    assertTrue(captured.getDeployables().contains(appYaml));
    assertTrue(captured.getDeployables().contains(cronYaml));
    assertTrue(captured.getDeployables().contains(dispatchYaml));
    assertTrue(captured.getDeployables().contains(dosYaml));
    assertTrue(captured.getDeployables().contains(indexYaml));
    assertTrue(captured.getDeployables().contains(queueYaml));
    assertFalse(captured.getDeployables().contains(invalidYaml));
  }

  @Test
  public void testDeployAllAction_appyaml() throws AppEngineException, IOException {
    deployExtension.setAppEngineDirectory(tempFolder.newFolder("appengine"));

    final Path appYaml = tempFolder.newFile("staging/app.yaml").toPath();
    final Path cronYaml = tempFolder.newFile("appengine/cron.yaml").toPath();
    final Path dispatchYaml = tempFolder.newFile("appengine/dispatch.yaml").toPath();
    final Path dosYaml = tempFolder.newFile("appengine/dos.yaml").toPath();
    final Path indexYaml = tempFolder.newFile("appengine/index.yaml").toPath();
    final Path queueYaml = tempFolder.newFile("appengine/queue.yaml").toPath();
    final Path invalidYaml = tempFolder.newFile("appengine/invalid.yaml").toPath();

    deployAllTask.deployAllAction();

    verify(deploy).deploy(deployCapture.capture());
    DeployConfiguration captured = deployCapture.getValue();
    assertTrue(captured.getDeployables().contains(appYaml));
    assertTrue(captured.getDeployables().contains(cronYaml));
    assertTrue(captured.getDeployables().contains(dispatchYaml));
    assertTrue(captured.getDeployables().contains(dosYaml));
    assertTrue(captured.getDeployables().contains(indexYaml));
    assertTrue(captured.getDeployables().contains(queueYaml));
    assertFalse(captured.getDeployables().contains(invalidYaml));
  }

  @Test
  public void testDeployAllAction_validFileNotInDirStandard()
      throws AppEngineException, IOException {
    deployExtension.setAppEngineDirectory(stageDir);

    final Path appYaml = tempFolder.newFile("staging/app.yaml").toPath();
    final Path validInDifferentDirYaml = tempFolder.newFile("queue.yaml").toPath();

    deployAllTask.deployAllAction();

    verify(deploy).deploy(deployCapture.capture());
    DeployConfiguration captured = deployCapture.getValue();
    assertTrue(captured.getDeployables().contains(appYaml));
    assertFalse(captured.getDeployables().contains(validInDifferentDirYaml));
  }

  @Test
  public void testDeployAllAction_validFileNotInDirAppYaml()
      throws AppEngineException, IOException {
    deployExtension.setAppEngineDirectory(tempFolder.newFolder("appengine"));

    // Make YAMLS
    final Path appYaml = tempFolder.newFile("staging/app.yaml").toPath();
    final Path validInDifferentDirYaml = tempFolder.newFile("queue.yaml").toPath();

    deployAllTask.deployAllAction();

    verify(deploy).deploy(deployCapture.capture());
    DeployConfiguration captured = deployCapture.getValue();
    assertTrue(captured.getDeployables().contains(appYaml));
    assertFalse(captured.getDeployables().contains(validInDifferentDirYaml));
  }
}
