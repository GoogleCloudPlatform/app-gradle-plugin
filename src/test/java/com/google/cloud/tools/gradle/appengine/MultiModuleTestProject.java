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

package com.google.cloud.tools.gradle.appengine;

import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardExtension;
import com.google.cloud.tools.gradle.appengine.standard.AppEngineStandardPlugin;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.testfixtures.ProjectBuilder;

/** Test helper to create multimodule appengine projects. */
public class MultiModuleTestProject {

  private final File projectRoot;
  private final List<String> modules = new ArrayList<>();

  public MultiModuleTestProject(File projectRoot) {
    this.projectRoot = projectRoot;
  }

  public MultiModuleTestProject addModule(String moduleName) {
    modules.add(moduleName);
    return this;
  }

  /**
   * Build and evaluate multi-module project.
   *
   * @return root project
   */
  public Project build() throws IOException {
    Project rootProject = ProjectBuilder.builder().withProjectDir(projectRoot).build();
    for (String module : modules) {
      Project p = ProjectBuilder.builder().withName(module).withParent(rootProject).build();

      // Create an appengine-web.xml for each module
      Path webInf = p.getProjectDir().toPath().resolve("src/main/webapp/WEB-INF");
      Files.createDirectories(webInf);
      File appengineWebXml = Files.createFile(webInf.resolve("appengine-web.xml")).toFile();
      Files.write(appengineWebXml.toPath(), "<appengine-web-app/>".getBytes(Charsets.UTF_8));

      p.getPluginManager().apply(JavaPlugin.class);
      p.getPluginManager().apply(WarPlugin.class);
      p.getPluginManager().apply(AppEngineStandardPlugin.class);

      DeployExtension deploy =
          p.getExtensions().getByType(AppEngineStandardExtension.class).getDeploy();
      deploy.setProjectId("project");
      deploy.setVersion("version");
    }
    ((ProjectInternal) rootProject).evaluate();
    return rootProject;
  }
}
