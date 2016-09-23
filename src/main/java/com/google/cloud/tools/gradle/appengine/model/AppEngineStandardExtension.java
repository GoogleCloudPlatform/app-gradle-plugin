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

package com.google.cloud.tools.gradle.appengine.model;

import groovy.lang.Closure;

/**
 * Root App Engine Flexible Environment model element
 */
public class AppEngineStandardExtension {
  private Tools tools = new Tools();
  private Deploy deploy = new Deploy();
  private Run run = new Run();
  private StageStandard stage = new StageStandard();

  public void cloudSdk(Closure c) {
    c.setResolveStrategy(Closure.DELEGATE_FIRST);
    c.setDelegate(tools);
    c.call();
  }

  public void deploy(Closure c) {
    c.setResolveStrategy(Closure.DELEGATE_FIRST);
    c.setDelegate(deploy);
    c.call();
  }

  public void stage(Closure c) {
    c.setResolveStrategy(Closure.DELEGATE_FIRST);
    c.setDelegate(stage);
    c.call();
  }

  public void run(Closure c) {
    c.setResolveStrategy(Closure.DELEGATE_FIRST);
    c.setDelegate(run);
    c.call();
  }

  public Tools getTools() {
    return tools;
  }

  public Deploy getDeploy() {
    return deploy;
  }

  public Run getRun() {
    return run;
  }

  public StageStandard getStage() {
    return stage;
  }
}
