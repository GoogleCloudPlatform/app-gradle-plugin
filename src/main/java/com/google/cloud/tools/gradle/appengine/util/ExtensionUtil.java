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

package com.google.cloud.tools.gradle.appengine.util;

import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;

public class ExtensionUtil {

  private final ExtensionAware searchRoot;

  public ExtensionUtil(ExtensionAware searchRoot) {
    this.searchRoot = searchRoot;
  }

  /**
   * Get an extension by it's path, potentially will throw all kinds of exceptions. Be very careful.
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String... path) {
    ExtensionAware root = searchRoot;
    for (String name : path) {
      ExtensionContainer children = root.getExtensions();
      root = (ExtensionAware) children.getByName(name);
    }
    return (T) root; // this is potentially unchecked.
  }
}
