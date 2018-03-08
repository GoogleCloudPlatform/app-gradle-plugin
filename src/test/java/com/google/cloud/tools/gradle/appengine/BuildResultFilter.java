/*
 * Copyright 2016 Google LLC. All Right Reserved.
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

import com.google.common.collect.FluentIterable;
import java.util.List;
import org.gradle.testkit.runner.BuildResult;

/** ToolsExtension to filter gradle test kit runner results. */
public class BuildResultFilter {

  /** Extract task as a list of path strings. */
  public static List<String> extractTasks(BuildResult buildResult) {

    return FluentIterable.from(buildResult.getTasks())
        .transform(buildTask -> buildTask.getPath())
        .toList();
  }
}
