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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DownloadCloudSdkTaskTest {

  @Mock private ToolsExtension toolsExtension;
  @Mock private CloudSdkBuilderFactory cloudSdkBuilderFactory;
  @Mock private SdkDownloader sdkDownloader;

  @Rule public ExpectedException exception = ExpectedException.none();
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
    downloadCloudSdkTask.setSdkDownloader(sdkDownloader);
  }

  @Test
  public void testDownloadCloudSdkAction_validateTrue() {
    String version = "LATEST";
    File home = getTempHomeDirectory("LATEST");
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(toolsExtension.getCloudSdkHome()).thenReturn(home);

    when(sdkDownloader.isSdkValid(version, home)).thenReturn(true);

    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(sdkDownloader, never()).downloadSdk(version);
    verify(cloudSdkBuilderFactory).setCloudSdkHome(home);
  }

  @Test
  public void testDownloadCloudSdkAction_validateFalse() {
    String version = "LATEST";
    File home = getTempHomeDirectory("100.100.100");
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(toolsExtension.getCloudSdkHome()).thenReturn(home);

    when(sdkDownloader.isSdkValid(version, home)).thenReturn(false);

    exception.expect(GradleException.class);
    exception.expectMessage(
        "Specified Cloud SDK version and actual version of the SDK installed in the "
            + "specified directory do not match. You must either specify the correct "
            + "cloudSdkHome and cloudSdkVersion, or you can remove the cloudSdkHome field "
            + "to download the version you want.");

    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(sdkDownloader, never()).downloadSdk(version);
    verify(cloudSdkBuilderFactory, never()).setCloudSdkHome(home);
  }

  @Test
  public void testDownloadCloudSdkAction_noValidation() {
    String version = null;
    File home = getTempHomeDirectory("LATEST");
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(toolsExtension.getCloudSdkHome()).thenReturn(home);

    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(sdkDownloader, never()).downloadSdk(version);
    verify(cloudSdkBuilderFactory).setCloudSdkHome(home);
  }

  @Test
  public void testDownloadCloudSdkAction_downloadVersion() {
    String version = "100.100.100";
    File home = null;
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(toolsExtension.getCloudSdkHome()).thenReturn(home);

    File tempDir = getTempHomeDirectory(version);
    when(sdkDownloader.downloadSdk(version)).thenReturn(tempDir);

    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(sdkDownloader).downloadSdk(version);
    verify(cloudSdkBuilderFactory).setCloudSdkHome(tempDir);
  }

  @Test
  public void testDownloadCloudSdkAction_downloadLatest() {
    String version = null;
    File home = null;
    when(toolsExtension.getCloudSdkVersion()).thenReturn(version);
    when(toolsExtension.getCloudSdkHome()).thenReturn(home);

    File tempDir = getTempHomeDirectory("LATEST");
    when(sdkDownloader.downloadSdk("LATEST")).thenReturn(tempDir);

    downloadCloudSdkTask.downloadCloudSdkAction();
    verify(sdkDownloader).downloadSdk("LATEST");
    verify(cloudSdkBuilderFactory).setCloudSdkHome(tempDir);
  }

  private File getTempHomeDirectory(String version) {
    try {
      return temporaryFolder.newFolder(version);
    } catch (IOException ex) {
      Assert.fail(
          "Failed to create temp Cloud SDK download directory at "
              + version
              + ": "
              + ex.getMessage());
      return null;
    }
  }
}
