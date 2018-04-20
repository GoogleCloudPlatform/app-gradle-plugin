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

package com.google.cloud.tools.gradle.appengine.core.model;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExtZ {
  private String zz = "hello";
  private Map<String, List<String>> zzNested =
      ImmutableMap.of("a", Arrays.asList("a1", "a2"), "b", Arrays.asList("b1", "b2"));

  public Map<String, List<String>> getZzNested() {
    return zzNested;
  }

  // Dummies for testing getter detection
  public void getNothing() {}

  public String gettersStartWithGet() {
    return "this is not actually a getter.";
  }

  public int get() {
    return 4;
  }

  public int getParameter(int param) {
    return param;
  }
}
