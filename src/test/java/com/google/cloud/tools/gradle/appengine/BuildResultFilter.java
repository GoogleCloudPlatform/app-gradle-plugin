/*
 * Copyright 2016 Google LLC. All Rights Reserved.
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

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.Buffer;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;

/** ToolsExtension to filter gradle test kit runner results. */
public class BuildResultFilter {

  /** Extract task as a list of path strings. */
  public static List<String> extractTasks(BuildResult buildResult) {

    // we can't use buildResult.getTasks() because it ignores skipped tasks
    return new BufferedReader(new StringReader(buildResult.getOutput())).lines().filter(str -> str.startsWith(":")).map(str -> str.split(" ")[0]).collect(Collectors.toList());
    //buildResult.getOutput().split();
    //return buildResult.getTasks().stream().map(BuildTask::getPath).collect(Collectors.toList());
  }
}
