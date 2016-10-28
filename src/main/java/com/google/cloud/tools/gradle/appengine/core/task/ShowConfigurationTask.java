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

package com.google.cloud.tools.gradle.appengine.core.task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.gradle.api.DefaultTask;
import org.gradle.api.internal.plugins.ExtensionContainerInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Task to print the appengine configuration closure
 */
public class ShowConfigurationTask extends DefaultTask {

  private static final String MODEL_PKG = "com.google.cloud.tools.gradle.appengine";
  // don't access extensionInstance directly, we need to evaluate it AS LATE AS POSSIBLE
  private Object extensionInstance;

  @Input
  public Object getExtensionInstance() {
    return extensionInstance;
  }

  public void setExtensionInstance(Object extensionInstance) {
    this.extensionInstance = extensionInstance;
  }

  @TaskAction
  public void showConfiguration() throws IllegalAccessException {
    // this is hardcoded in
    getLogger().lifecycle("appengine {");
    getLogger().lifecycle(getAllFields(getExtensionInstance(), 0) + "}");
  }

  @VisibleForTesting
  // recursive (but doesn't search through nested objects, only nested extensions)
  static String getAllFields(Object instance, int depth) throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    // inspect all fields of the extension
    if (instance.getClass().getName().endsWith("_Decorated")) {
      for (Field field : instance.getClass().getSuperclass().getDeclaredFields()) {
        result.append(getFieldData(field, instance, depth + 1));
      }
    }
    // inspect all extensions of the extension
    if (instance instanceof ExtensionAware) {
      depth += 1;
      Map<String, Object> map =
          ((ExtensionContainerInternal) ((ExtensionAware) instance).getExtensions()).getAsMap();
      for (String extensionName : map.keySet()) {
        if (map.get(extensionName).getClass().getSuperclass().getPackage().getName().startsWith(MODEL_PKG)) {
          result.append(spaces(depth))
              .append(extensionName)
              .append(" {\n");
          result.append(getAllFields(map.get(extensionName), depth + 1));
          result.append(spaces(depth))
              .append("}\n");
        }
      }
    }
    return result.toString();
  }

  private static String getFieldData(Field root, Object instance, int depth) throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    root.setAccessible(true);
    result.append(spaces(depth))
        .append("(")
        .append(root.getType().getSimpleName())
        .append(getGenericTypeData(root))
        .append(") ")
        .append(root.getName())
        .append(" = ")
        .append(root.get(instance))
        .append("\n");
    return result.toString();
  }

  private static String getGenericTypeData(Field f) {
    Type genericType = f.getGenericType();
    List<String> types = Lists.newArrayList();
    if (genericType != null && genericType instanceof ParameterizedType) {
      for (Type t :((ParameterizedType)genericType).getActualTypeArguments()) {
        types.add(((Class) t).getSimpleName());
      }
    }
    return (types.size() > 0) ? "<" + Joiner.on(", ").join(types) + ">" : "";
  }

  private static String spaces(int depth) {
    return Strings.repeat(" ", depth);
  }
}
