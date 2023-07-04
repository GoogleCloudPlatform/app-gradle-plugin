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

import com.google.cloud.tools.gradle.appengine.TestProject;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import java.io.IOException;
import java.util.List;
import org.gradle.api.Project;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AppEngineCorePluginConfigurationTest {

  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder();

  @Test
  public void testCreateDownloadSdkTask_configureAppEngineComponent() throws IOException {
    Project project =
        new TestProject(testProjectDir.getRoot())
            .addAppEngineWebXml()
            .applyStandardProjectBuilder();

    DownloadCloudSdkTask task =
        (DownloadCloudSdkTask)
            project
                .getTasks()
                .getByPath(AppEngineCorePluginConfiguration.DOWNLOAD_CLOUD_SDK_TASK_NAME);
    List<SdkComponent> components = task.getComponents();
    MatcherAssert.assertThat(components, Matchers.hasItem(SdkComponent.APP_ENGINE_JAVA));
    Assert.assertEquals(1, components.size());
  }

  @Test
  public void testCreateDownloadSdkTask_noComponents() {
    Project project = new TestProject(testProjectDir.getRoot()).applyAppYamlProjectBuilder();

    DownloadCloudSdkTask task =
        (DownloadCloudSdkTask)
            project
                .getTasks()
                .getByPath(AppEngineCorePluginConfiguration.DOWNLOAD_CLOUD_SDK_TASK_NAME);
    Assert.assertEquals(0, task.getComponents().size());
  }
}
