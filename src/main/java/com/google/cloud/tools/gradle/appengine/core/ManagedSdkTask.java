package com.google.cloud.tools.gradle.appengine.core;

import com.google.common.base.Strings;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class ManagedSdkTask extends DefaultTask {

  private ToolsExtension toolsExtension;

  public void setToolsConfig(ToolsExtension toolsExtension) {
    this.toolsExtension = toolsExtension;
  }

  @TaskAction
  public void managedSdkAction() {
    getProject().getLogger().lifecycle("Running managedSdkTask.");

    getProject().getLogger().lifecycle("Cloud SDK Version: " + toolsExtension.getCloudSdkVersion());
    getProject()
        .getLogger()
        .lifecycle("Download Cloud SDK: " + toolsExtension.getDownloadCloudSdk());
    getProject().getLogger().lifecycle("Cloud SDK Home: " + toolsExtension.getCloudSdkHome());

    String sdkVersion = toolsExtension.getCloudSdkVersion();
    File sdkHome = toolsExtension.getCloudSdkHome();
    boolean download = toolsExtension.getDownloadCloudSdk();

    if (download) {
      if (sdkVersion == null) {
        // Wants to download, but version isn't specified; assume latest version
        // TODO: Download sdk at latest version
      } else {
        // Wants to download specific version
        // TODO: Download sdk at specified version
      }
    } else {
      if (sdkHome == null) {
        // SDK home not specified; try to find installation
        // TODO: Find installation
      }

      if (!Strings.isNullOrEmpty(sdkVersion)) {
        // Version specified, so validate installation
        // TODO: Validate installation
      } else {
        // No validation required
      }
    }
  }
}
