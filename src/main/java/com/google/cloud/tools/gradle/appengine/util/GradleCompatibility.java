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
   * <p>Illustrious history of why deprecations should be handled as soon as they come up:
   *
   * <ul>
   *   <li>Gradle 5.1.0-M1 added {@code getArchiveFile}: <a
   *       href="https://github.com/gradle/gradle/commit/aff5155fc0f281e407df70dd1712cc5c08571f21#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR122">commit</a>
   *   <li>Gradle 5.1.0-M1 deprecated {@code getArchivePath}: <a
   *       href="https://github.com/gradle/gradle/commit/5c458c522af58612f2bfa85568e07aa29e585931#">commit</a>
   *   <li>Gradle 6.0.0-RC1 added nagging to {@code getArchivePath}: <a
   *       href="https://github.com/gradle/gradle/commit/e3824ff38513762a9a9ed554d7b2161e5be7c31a#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR148">commit</a>
   *   <li>Gradle 6.0.0-RC1 removed nagging of {@code getArchivePath}, because Kotlin plugin wasn't
   *       ready: <a
   *       href="https://github.com/gradle/gradle/commit/f3cafc74853d0c4d0720b6a1b8bc70a09e12c6c1#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR149">commit</a>
   *   <li>Gradle 7.1.0-RC1 re-enabled nagging of {@code getArchivePath} for Gradle 8: <a
   *       href="https://github.com/gradle/gradle/commit/d17832e975a5718e95bf65e8d67a341f78732ff6#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR156">commit</a>
   *   <li>Gradle 7.1.0-RC1 removed nagging of {@code getArchivePath}, because Kotlin plugin wasn't
   *       ready: <a
   *       href="https://github.com/gradle/gradle/commit/3107f771a53e847cc5cd4f543e134d86e15215b6#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR110">commit</a>
   *   <li>Gradle 8.0-M2 removed {@code getArchivePath}: <a
   *       href="https://github.com/gradle/gradle/commit/dbbd4d6875ae9faad57f5677810793f83ea22399#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdL156">commit</a>
   *   <li>Gradle 8.0-M2 re-added {@code getArchivePath} with nagging for Gradle 9, because Kotlin
   *       plugin wasn't ready: <a
   *       href="https://github.com/gradle/gradle/commit/96abd3e04369a8869bca0658369ccb002bb6fe7e#diff-54e324a7535f20d634929dafc00edb5bc8d287199f200cc50b57461e46a513cdR154">commit</a>
   * </ul>
   *
   * @param task the task whose archive location we're interested in.
   * @return the archive location as a {@link File} for compatibility with older Gradle versions.
   */
  @SuppressWarnings("deprecation")
  public static File getArchiveFile(AbstractArchiveTask task) {
    if (GradleVersion.current().getBaseVersion().compareTo(GradleVersion.version("5.1")) >= 0) {
      return task.getArchiveFile().get().getAsFile();
    } else {
      // Note: in case this fails to compile, because Gradle have succeeded with the removal, this
      // needs to be replaced with a reflective call to getArchivePath()
      // or the minimum Gradle version for GCP Gradle Plugin bumped up to 5.1+.
      return task.getArchivePath();
    }
  }
}
