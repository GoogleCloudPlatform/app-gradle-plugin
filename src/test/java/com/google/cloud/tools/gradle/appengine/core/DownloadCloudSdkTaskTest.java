/*
 * Copyright 2018 Google LLC. All Rights Reserved.
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVerificationException;
import com.google.cloud.tools.managedcloudsdk.ManagedSdkVersionMismatchException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExecutionException;
import com.google.cloud.tools.managedcloudsdk.command.CommandExitException;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponent;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponentInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstallerException;
import com.google.cloud.tools.managedcloudsdk.update.SdkUpdater;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DownloadCloudSdkTaskTest {

  @Mock private ManagedCloudSdk managedCloudSdk;

  @Mock private SdkInstaller installer;
  @Mock private SdkComponentInstaller componentInstaller;
  @Mock private SdkUpdater updater;

  private DownloadCloudSdkTask downloadCloudSdkTask;

  /** Setup DownloadCloudSdkTaskTest. */
  @Before
  public void setup() {
    Project tempProject = ProjectBuilder.builder().build();
    downloadCloudSdkTask =
        tempProject.getTasks().create("tempDownloadTask", DownloadCloudSdkTask.class);

    when(managedCloudSdk.newInstaller()).thenReturn(installer);
    when(managedCloudSdk.newComponentInstaller()).thenReturn(componentInstaller);
    when(managedCloudSdk.newUpdater()).thenReturn(updater);
  }

  @Test
  public void testDownloadCloudSdkAction_badConfigure()
      throws CommandExecutionException, InterruptedException, SdkInstallerException,
          ManagedSdkVersionMismatchException, CommandExitException, ManagedSdkVerificationException,
          IOException {
    downloadCloudSdkTask.setManagedCloudSdk(null);
    try {
      downloadCloudSdkTask.downloadCloudSdkAction();
      Assert.fail();
    } catch (GradleException ex) {
      Assert.assertEquals(
          "Cloud SDK home path must not be configured to run this task.", ex.getMessage());
    }
  }

  @Test
  public void testDownloadCloudSdkAction_install()
      throws ManagedSdkVerificationException, ManagedSdkVersionMismatchException,
          InterruptedException, CommandExecutionException, SdkInstallerException, IOException,
          CommandExitException {
    downloadCloudSdkTask.setManagedCloudSdk(managedCloudSdk);
    when(managedCloudSdk.isInstalled()).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk).newInstaller();
  }

  @Test
  public void testDownloadCloudSdkAction_installComponent()
      throws ManagedSdkVerificationException, ManagedSdkVersionMismatchException,
          InterruptedException, CommandExecutionException, SdkInstallerException, IOException,
          CommandExitException {
    downloadCloudSdkTask.setManagedCloudSdk(managedCloudSdk);
    when(managedCloudSdk.isInstalled()).thenReturn(true);
    when(managedCloudSdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk, never()).newInstaller();
    verify(managedCloudSdk).newComponentInstaller();
  }

  @Test
  public void testDownloadCloudSdkAction_update()
      throws ManagedSdkVerificationException, ManagedSdkVersionMismatchException,
          InterruptedException, CommandExecutionException, SdkInstallerException, IOException,
          CommandExitException {
    downloadCloudSdkTask.setManagedCloudSdk(managedCloudSdk);
    when(managedCloudSdk.isInstalled()).thenReturn(true);
    when(managedCloudSdk.hasComponent(SdkComponent.APP_ENGINE_JAVA)).thenReturn(true);
    when(managedCloudSdk.isUpToDate()).thenReturn(false);
    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(managedCloudSdk, never()).newInstaller();
    verify(managedCloudSdk, never()).newComponentInstaller();
    verify(managedCloudSdk).newUpdater();
  }
}
