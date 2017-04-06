/*
 * Copyright (c) 2016 Google Inc. All Right Reserved.
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

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.flexible.AppEngineFlexiblePlugin;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;

/**
 * This is a getting-started plugin that auto detects the user's configuration and assigns it a
 * standard or flexible environment build.
 */
public class AppEnginePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {

    if (isAppEngineStandard(project)) {
      project.getPluginManager().apply(AppEngineStandardPlugin.class);
    } else {
      project.getPluginManager().apply(AppEngineFlexiblePlugin.class);
    }
  }

  private boolean isAppEngineStandard(Project project) {

    // ask the war plugin if it has appengine-web.xml
    if (project.getPlugins().hasPlugin(WarPlugin.class)) {
      WarPluginConvention warConfig = project.getConvention().getPlugin(WarPluginConvention.class);
      Path appengineWebXml = warConfig.getWebAppDir().toPath().resolve("WEB-INF/appengine-web.xml");
      if (Files.exists(appengineWebXml)) {
        return true;
      }
    }
    // convention based lookup of appengine-web.xml as a fallback
    Path appengineWebXml =
        project.getProjectDir().toPath().resolve("src/main/java/webapp/WEB-INF/appengine-web.xml");
    if (Files.exists(appengineWebXml)) {
      return true;
    }

    return false;
  }
}
