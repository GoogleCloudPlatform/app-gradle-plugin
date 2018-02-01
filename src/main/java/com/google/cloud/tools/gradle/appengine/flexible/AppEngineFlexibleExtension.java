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

package com.google.cloud.tools.gradle.appengine.flexible;

import com.google.cloud.tools.gradle.appengine.core.AppEngineCoreExtensionProperties;
import com.google.cloud.tools.gradle.appengine.core.DeployExtension;
import com.google.cloud.tools.gradle.appengine.core.ToolsExtension;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;

public class AppEngineFlexibleExtension implements AppEngineCoreExtensionProperties {
  private ToolsExtension tools;
  private DeployExtension deploy;
  private StageFlexibleExtension stage;

  /**
   * Create nested configuration blocks as Extensions.
   */
  public void createSubExtensions(Project project) {
    tools = ((ExtensionAware) this).getExtensions().create("tools", ToolsExtension.class, project);
    deploy =
        ((ExtensionAware) this).getExtensions().create("deploy", DeployExtension.class, project);
    stage =
        ((ExtensionAware) this)
            .getExtensions()
            .create("stage", StageFlexibleExtension.class, project);
  }

  public void tools(Action<? super ToolsExtension> action) {
    action.execute(tools);
  }

  public void deploy(Action<? super DeployExtension> action) {
    action.execute(deploy);
  }

  public void stage(Action<? super StageFlexibleExtension> action) {
    action.execute(stage);
  }

  @Override
  public ToolsExtension getTools() {
    return tools;
  }

  @Override
  public DeployExtension getDeploy() {
    return deploy;
  }

  public StageFlexibleExtension getStage() {
    return stage;
  }
}
