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

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.api.deploy.AppEngineDeployment;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk.Builder;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeployAllTaskTest {

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private DeployExtension deployConfig;

  @Mock private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  @Mock private Builder builder;
  @Mock private CloudSdk cloudSdk;

  @Mock private AppEngineDeployment deploy;

  private DeployAllTask deployAllTask;

  @Before
  public void setup() throws IOException, AppEngineException {
    Project tempProject = ProjectBuilder.builder().build();
    deployAllTask = tempProject.getTasks().create("tempDeployAllTask", DeployAllTask.class);
    List<File> deployables = new ArrayList<>();
    File stageDir = tempFolder.newFolder("staging");

    when(deployConfig.getDeployables()).thenReturn(deployables);
    when(deployConfig.getAppEngineDirectory()).thenReturn(stageDir);

    deployAllTask.setDeployConfig(deployConfig);
    deployAllTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);

    when(cloudSdkBuilderFactory.newBuilder(deployAllTask.getLogger())).thenReturn(builder);
    when(builder.build()).thenReturn(cloudSdk);
    when(cloudSdkBuilderFactory.newAppEngineDeployment(cloudSdk)).thenReturn(deploy);

    // Create appengine-web.xml to mark it as standard environment
    File appengineWebXml = new File(tempFolder.newFolder("source", "WEB-INF"), "appengine-web.xml");
    appengineWebXml.createNewFile();
    Files.write("<appengine-web-app></appengine-web-app>", appengineWebXml, Charsets.UTF_8);
  }

  @Test
  public void testDeployAllAction() throws AppEngineException, IOException {
    // Make YAMLS
    File appYaml = tempFolder.newFile("staging/app.yaml");
    File cronYaml = tempFolder.newFile("staging/cron.yaml");
    File dispatchYaml = tempFolder.newFile("staging/dispatch.yaml");
    File dosYaml = tempFolder.newFile("staging/dos.yaml");
    File indexYaml = tempFolder.newFile("staging/index.yaml");
    File queueYaml = tempFolder.newFile("staging/queue.yaml");
    File invalidYaml = tempFolder.newFile("staging/invalid.yaml");

    deployAllTask.deployAllAction();

    assertTrue(deployConfig.getDeployables().contains(appYaml));
    assertTrue(deployConfig.getDeployables().contains(cronYaml));
    assertTrue(deployConfig.getDeployables().contains(dispatchYaml));
    assertTrue(deployConfig.getDeployables().contains(dosYaml));
    assertTrue(deployConfig.getDeployables().contains(indexYaml));
    assertTrue(deployConfig.getDeployables().contains(queueYaml));
    assertFalse(deployConfig.getDeployables().contains(invalidYaml));
    verify(deploy).deploy(deployConfig);
  }

  @Test
  public void testDeployAllAction_validFileNotInDir() throws AppEngineException, IOException {
    // Make YAMLS
    File appYaml = tempFolder.newFile("staging/app.yaml");
    File validInDifferentDirYaml = tempFolder.newFile("queue.yaml");

    deployAllTask.deployAllAction();

    assertTrue(deployConfig.getDeployables().contains(appYaml));
    assertFalse(deployConfig.getDeployables().contains(validInDifferentDirYaml));
    verify(deploy).deploy(deployConfig);
  }
}
