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

package com.google.cloud.tools.gradle.appengine.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.gradle.api.DefaultTask;
import org.gradle.api.internal.plugins.ExtensionContainerInternal;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/** Task to print the appengine configuration closure. */
public class ShowConfigurationTask extends DefaultTask {

  private String extensionId;
  private String allowedNestedPackageSearchPath;

  @Input
  public String getExtensionId() {
    return extensionId;
  }

  public void setExtensionId(String extensionId) {
    this.extensionId = extensionId;
  }

  /** Task entrypoint : Log out configuration to lifecyle. */
  @TaskAction
  public void showConfiguration() throws IllegalAccessException {
    Object extensionInstance = getProject().getExtensions().getByName(extensionId);
    getLogger().lifecycle(getExtensionData(extensionId, extensionInstance, 0));
  }

  @VisibleForTesting
  // recursive (but doesn't search through nested objects, only nested extensions)
  static String getExtensionData(String extensionName, Object extensionInstance, int depth)
      throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    // extension start block
    result.append(spaces(depth)).append(extensionName).append(" {\n");

    // all non-extension fields
    for (Method method : extensionInstance.getClass().getSuperclass().getDeclaredMethods()) {
      // ignore synthetic fields (stuff added by compiler or instrumenter)
      if (method.isSynthetic()) {
        continue;
      }
      if (method.getName().startsWith("get")
          && method.getName().length() >= 4
          && Character.isUpperCase(method.getName().charAt(3))
          && method.getParameterCount() == 0
          && method.getReturnType() != void.class) {
        result.append(getMethodData(method, extensionInstance, depth + 1));
      }
    }

    // all extension fields
    Map<String, Object> map =
        ((ExtensionContainerInternal) ((ExtensionAware) extensionInstance).getExtensions())
            .getAsMap();
    for (String childExtensionName : map.keySet()) {
      Object childExtensionInstance = map.get(childExtensionName);
      // only expand out extensions we understand (we're ignoring the default ext group here, which
      // is not ExtensionAware)
      if (childExtensionInstance instanceof ExtensionAware) {
        result.append(getExtensionData(childExtensionName, map.get(childExtensionName), depth + 1));
      }
    }

    // extension end block
    result.append(spaces(depth)).append("}\n");

    return result.toString();
  }

  // Extract the type (and generic type parameters) and value for a given field.
  private static String getMethodData(Method method, Object instance, int depth)
      throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    method.setAccessible(true);
    result
        .append(spaces(depth))
        .append("(")
        .append(method.getReturnType().getSimpleName())
        .append(getGenericTypeData(method.getGenericReturnType()))
        .append(") ")
        .append(getFieldNameFromGetter(method.getName()))
        .append(" = ");
    try {
      result.append(method.invoke(instance));
    } catch (InvocationTargetException ex) {
      result.append("<Failed to read property: ").append(ex.getCause().getMessage()).append(">");
    }
    result.append("\n");
    return result.toString();
  }

  private static String getFieldNameFromGetter(String name) {
    return Character.toLowerCase(name.charAt(3)) + name.substring(4);
  }

  // Extract the generic type information <...>, recursively including any nested generic type info.
  private static String getGenericTypeData(Type genericType) {
    List<String> types = Lists.newArrayList();
    if (genericType != null && genericType instanceof ParameterizedType) {
      for (Type t : ((ParameterizedType) genericType).getActualTypeArguments()) {
        if (t instanceof ParameterizedType) {
          String nestedGeneric = ((Class) ((ParameterizedType) t).getRawType()).getSimpleName();
          nestedGeneric += getGenericTypeData(t);
          types.add(nestedGeneric);
        } else {
          types.add(((Class) t).getSimpleName());
        }
      }
    }
    return (types.size() > 0) ? "<" + Joiner.on(", ").join(types) + ">" : "";
  }

  // control spaces to control tab width
  private static String spaces(int depth) {
    return Strings.repeat(" ", depth * 2);
  }
}
