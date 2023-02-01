package com.google.cloud.tools.gradle.appengine.util;

import java.io.File;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.util.GradleVersion;

/** Utility class for Gradle compatibility related functions. As an end user, do not use. */
public class GradleCompatibility {

  private GradleCompatibility() {
    // Prevent instantiation and extension.
  }

  /**
   * Compatibility method for getting the archive location.
   *
   * @param task the task whose archive location we're interested in.
   * @return the archive location as a {@link File} for compatibility with older Gradle versions.
   */
  @SuppressWarnings("deprecation")
  public static File getArchiveFile(AbstractArchiveTask task) {
    // getArchiveFile and getArchivePath history:
    //  - Gradle 5.1.0-M1 added `getArchiveFile`
    //  - Gradle 5.1.0-M1 deprecated `getArchivePath`
    //  - Gradle 6.0.0-RC1 added nagging to `getArchivePath`
    //  - Gradle 6.0.0-RC1 removed nagging of `getArchivePath`
    //  - Gradle 7.1.0-RC1 re-enabled nagging of `getArchivePath` for Gradle 8
    //  - Gradle 7.1.0-RC1 removed nagging of `getArchivePath`
    //  - Gradle 8.0-M2 removed `getArchivePath`
    //  - Gradle 8.0-M2 re-added `getArchivePath` with nagging for Gradle 9
    // For full history with references see:
    // https://github.com/GoogleCloudPlatform/app-gradle-plugin/pull/451

    if (GradleVersion.current().getBaseVersion().compareTo(GradleVersion.version("5.1")) >= 0) {
      return task.getArchiveFile().get().getAsFile();
    } else {
      // Note: in case this fails to compile, because Gradle have succeeded with the removal,
      // this needs to be replaced with a reflective call to getArchivePath()
      // or the minimum Gradle version for GCP Gradle Plugin bumped up to 5.1+.
      return task.getArchivePath();
    }
  }
}
