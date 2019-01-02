/*
 * Copyright 2017 Google LLC. All Rights Reserved.
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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.cloud.tools.appengine.configuration.StopConfiguration;
import com.google.cloud.tools.appengine.operations.DevServer;
import com.google.cloud.tools.appengine.operations.LocalRun;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.gradle.api.ProjectConfigurationException;

/**
 * Helper class for DevAppServer[X]Task to obtain the correct server or configuration based on the
 * server version.
 */
public class DevAppServerHelper {

  private static final String V1 = "1";
  private static final String V2 = "2-alpha";
  @VisibleForTesting static final List<String> SERVER_VERSIONS = ImmutableList.of(V1, V2);

  private Validator validator = new Validator();

  /** Return an appserver based on serverVersion. */
  public DevServer getAppServer(
      LocalRun localRun, RunExtension run, ProcessHandler processHandler) {

    String serverVersion = run.getServerVersion();
    validator.validateServerVersion(serverVersion);

    switch (serverVersion) {
      case V1:
        return localRun.newDevAppServer1(processHandler);
      case V2:
        return localRun.newDevAppServer2(processHandler);
      default:
        throw new AssertionError("Unexpected serverVersion " + run.getServerVersion());
    }
  }

  /** Return a stop configuration based on serverVersion. */
  public StopConfiguration getStopConfiguration(RunExtension run) {

    String serverVersion = run.getServerVersion();
    validator.validateServerVersion(serverVersion);

    switch (serverVersion) {
      case V1:
        return StopConfiguration.builder()
            .adminHost(run.getHost())
            .adminPort(run.getPort())
            .build();
      case V2:
        return StopConfiguration.builder()
            .adminHost(run.getAdminHost())
            .adminPort(run.getAdminPort())
            .build();
      default:
        throw new AssertionError("Unexpected serverVersion " + run.getServerVersion());
    }
  }

  @VisibleForTesting
  static class Validator {

    @VisibleForTesting
    void validateServerVersion(String serverVersion) throws ProjectConfigurationException {
      if (!SERVER_VERSIONS.contains(serverVersion)) {
        throw new ProjectConfigurationException(
            "Invalid serverVersion '" + serverVersion + "' use one of " + SERVER_VERSIONS, null);
      }
    }
  }
}
