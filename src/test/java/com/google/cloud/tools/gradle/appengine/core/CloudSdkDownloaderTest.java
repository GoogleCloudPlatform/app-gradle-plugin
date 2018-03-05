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

import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.OsInfo;
import com.google.cloud.tools.managedcloudsdk.OsInfo.Name;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import com.google.cloud.tools.managedcloudsdk.Version;
import com.google.cloud.tools.managedcloudsdk.components.SdkComponentInstaller;
import com.google.cloud.tools.managedcloudsdk.install.SdkInstaller;
import com.google.cloud.tools.managedcloudsdk.update.SdkUpdater;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.internal.impldep.org.junit.Rule;
import org.gradle.internal.impldep.org.junit.Test;
import org.junit.Assert;
import org.junit.rules.TemporaryFolder;

public class CloudSdkDownloaderTest {

  @Rule TemporaryFolder temporaryFolder = new TemporaryFolder();

  private CloudSdkDownloader downloader;

  @Test
  public void testDownloadSdk_latest() {
    downloader = new CloudSdkDownloader();

    try {
      downloader.setInstaller(getTempInstaller(Version.LATEST));
      downloader.setComponentInstaller(getTempComponentInstaller());
      downloader.setUpdater(getTempUpdater());

      File sdkHome = downloader.downloadSdk(Version.LATEST.toString());

      Assert.assertTrue(sdkHome.exists());
      Assert.assertTrue(getTempGcloud().toFile().exists());
    } catch (IOException | UnsupportedOsException e) {
      Assert.fail();
    }
  }

  @Test
  public void testDownloadSdk_fixedVersion() {
    downloader = new CloudSdkDownloader();

    try {
      downloader.setInstaller(getTempInstaller(new Version("191.0.0")));
      downloader.setComponentInstaller(getTempComponentInstaller());
      downloader.setUpdater(getTempUpdater());

      File sdkHome = downloader.downloadSdk("191.0.0");

      Assert.assertTrue(sdkHome.exists());
      Assert.assertTrue(getTempGcloud().toFile().exists());
    } catch (IOException | UnsupportedOsException | BadCloudSdkVersionException e) {
      Assert.fail();
    }
  }

  @Test
  public void testIsSdkValid_true() {
    downloader = new CloudSdkDownloader();

    try {
      downloader.setInstaller(getTempInstaller(Version.LATEST));
      downloader.setComponentInstaller(getTempComponentInstaller());
      downloader.setUpdater(getTempUpdater());

      File sdkHome = downloader.downloadSdk(Version.LATEST.toString());
      Assert.assertTrue(downloader.isSdkValid(Version.LATEST.toString(), sdkHome));
    } catch (IOException | UnsupportedOsException e) {
      Assert.fail();
    }
  }

  @Test
  public void testIsSdkValid_false() {
    downloader = new CloudSdkDownloader();

    try {
      downloader.setInstaller(getTempInstaller(new Version("190.0.0")));
      downloader.setComponentInstaller(getTempComponentInstaller());
      downloader.setUpdater(getTempUpdater());

      File sdkHome = downloader.downloadSdk("190.0.0");
      Assert.assertFalse(downloader.isSdkValid("191.0.0", sdkHome));
    } catch (IOException | UnsupportedOsException | BadCloudSdkVersionException e) {
      Assert.fail();
    }
  }

  private SdkInstaller getTempInstaller(Version version)
      throws IOException, UnsupportedOsException {
    return SdkInstaller.newInstaller(
        temporaryFolder.newFolder().toPath(),
        version,
        OsInfo.getSystemOsInfo(),
        "google-cloud-tools-java",
        false);
  }

  private SdkComponentInstaller getTempComponentInstaller() throws UnsupportedOsException {
    return SdkComponentInstaller.newComponentInstaller(getTempGcloud());
  }

  private SdkUpdater getTempUpdater() throws UnsupportedOsException {
    return SdkUpdater.newUpdater(getTempGcloud());
  }

  private Path getTempGcloud() throws UnsupportedOsException {
    OsInfo info = OsInfo.getSystemOsInfo();
    String gcloudBin = info.name().equals(Name.WINDOWS) ? "gcloud.cmd" : "gcloud";
    return temporaryFolder.getRoot().toPath().resolve("bin").resolve(gcloudBin);
  }
}
