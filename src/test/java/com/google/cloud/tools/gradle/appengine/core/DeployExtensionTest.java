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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.cloud.tools.appengine.configuration.DeployConfiguration;
import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeployExtensionTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Mock private DeployTargetResolver deployTargetResolver;
  private Project testProject;

  @Before
  public void setUp() {
    Mockito.when(deployTargetResolver.getProject("test-project-id"))
        .thenReturn("processed-project-id");
    Mockito.when(deployTargetResolver.getVersion("test-version")).thenReturn("processed-version");
    testProject = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();
  }

  @Test
  public void testToDeployConfiguration_allValuesSet() {
    DeployExtension testExtension = new DeployExtension(testProject);
    testExtension.setDeployTargetResolver(deployTargetResolver);

    testExtension.setBucket("test-bucket");
    testExtension.setImageUrl("test-img-url");
    testExtension.setProjectId("test-project-id");
    testExtension.setPromote(true);
    testExtension.setServer("test-server");
    testExtension.setStopPreviousVersion(true);
    testExtension.setVersion("test-version");

    List<Path> projects = ImmutableList.of(Paths.get("project1"), Paths.get("project2"));
    DeployConfiguration x = testExtension.toDeployConfiguration(projects);

    Assert.assertEquals(projects, x.getDeployables());
    Assert.assertEquals("test-bucket", x.getBucket());
    Assert.assertEquals("test-img-url", x.getImageUrl());
    Assert.assertEquals("processed-project-id", x.getProjectId());
    Assert.assertEquals(Boolean.TRUE, x.getPromote());
    Assert.assertEquals("test-server", x.getServer());
    Assert.assertEquals(Boolean.TRUE, x.getStopPreviousVersion());
    Assert.assertEquals("processed-version", x.getVersion());

    Mockito.verify(deployTargetResolver).getProject("test-project-id");
    Mockito.verify(deployTargetResolver).getVersion("test-version");
    Mockito.verifyNoMoreInteractions(deployTargetResolver);
  }

  @Test
  public void testToDeployConfiguration_onlyRequiredValuesSet() {
    DeployExtension testExtension = new DeployExtension(testProject);
    testExtension.setDeployTargetResolver(deployTargetResolver);

    testExtension.setProjectId("test-project-id");
    testExtension.setVersion("test-version");

    List<Path> projects = ImmutableList.of(Paths.get("project1"), Paths.get("project2"));
    DeployConfiguration x = testExtension.toDeployConfiguration(projects);

    Assert.assertEquals("processed-project-id", x.getProjectId());
    Assert.assertEquals("processed-version", x.getVersion());

    Assert.assertNull(x.getBucket());
    Assert.assertNull(x.getImageUrl());
    Assert.assertNull(x.getPromote());
    Assert.assertNull(x.getServer());
    Assert.assertNull(x.getStopPreviousVersion());

    Mockito.verify(deployTargetResolver).getProject("test-project-id");
    Mockito.verify(deployTargetResolver).getVersion("test-version");
    Mockito.verifyNoMoreInteractions(deployTargetResolver);
  }
}
