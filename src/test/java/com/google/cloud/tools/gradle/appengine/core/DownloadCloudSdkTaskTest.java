/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine.core;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponentInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstaller;
import com.google.cloud.tools.managedcloudsdk.update.SdkUpdater;
import java.io.File;
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
public class DownloadCloudSdkTaskTest {

  @Mock private ToolsExtension toolsExtension;
  @Mock private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  @Mock private ManagedCloudSdk managedCloudSdk;
  @Mock private ManagedCloudSdkFactory managedCloudSdkFactory;

  @Mock private SdkInstaller installer;
  @Mock private SdkComponentInstaller componentInstaller;
  @Mock private SdkUpdater updater;

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private DownloadCloudSdkTask downloadCloudSdkTask;

  /** Setup DownloadCloudSdkTaskTest. */
  @Before
  public void setup() {
    Project tempProject = ProjectBuilder.builder().build();
    downloadCloudSdkTask =
        tempProject.getTasks().create("tempDownloadTask", DownloadCloudSdkTask.class);
    downloadCloudSdkTask.setCloudSdkBuilderFactory(cloudSdkBuilderFactory);
    downloadCloudSdkTask.setToolsExtension(toolsExtension);
    downloadCloudSdkTask.setManagedCloudSdkFactory(managedCloudSdkFactory);

    when(managedCloudSdk.newInstaller()).thenReturn(installer);
    when(managedCloudSdk.newComponentInstaller()).thenReturn(componentInstaller);
    when(managedCloudSdk.newUpdater()).thenReturn(updater);
    when(managedCloudSdk.getSdkHome()).thenReturn(new File("").toPath());
  }

  @Test
  public void testDownloadCloudSdkAction_install() throws Exception {
    String version = "LATEST";
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(managedCloudSdkFactory.newManagedSdk(version)).thenReturn(managedCloudSdk);

    when(managedCloudSdk.isInstalled()).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk).newInstaller();
  }

  @Test
  public void testDownloadCloudSdkAction_installComponent() throws Exception {
    String version = "LATEST";
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(managedCloudSdkFactory.newManagedSdk(version)).thenReturn(managedCloudSdk);

    when(managedCloudSdk.isInstalled()).thenReturn(true);
    when(managedCloudSdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk, times(0)).newInstaller();
    verify(managedCloudSdk).newComponentInstaller();
  }

  @Test
  public void testDownloadCloudSdkAction_update() throws Exception {
    String version = "LATEST";
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(managedCloudSdkFactory.newManagedSdk(version)).thenReturn(managedCloudSdk);

    when(managedCloudSdk.isInstalled()).thenReturn(true);
    when(managedCloudSdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)).thenReturn(true);
    when(managedCloudSdk.isUpToDate()).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk, times(0)).newInstaller();
    verify(managedCloudSdk, times(0)).newComponentInstaller();
    verify(managedCloudSdk).newUpdater();
  }
}
