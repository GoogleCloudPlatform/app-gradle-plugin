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

package com.google.cloud.tools.gradle.appengine.task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Task to print the appengine configuration closure
 */
public class ShowConfigurationTask extends DefaultTask {

  // require extensionClass because gradle creates a decorated_class extension instance
  private Class<?> extensionClass;
  private Object extensionInstance;

  @Input
  public Class<?> getExtensionClass() {
    return extensionClass;
  }

  public void setExtensionClass(Class<?> extension) {
    this.extensionClass = extension;
  }

  @Input
  public Object getExtensionInstance() {
    return extensionInstance;
  }

  public void setExtensionInstance(Object extensionInstance) {
    this.extensionInstance = extensionInstance;
  }

  @TaskAction
  public void showConfiguration() throws IllegalAccessException {
    if (!extensionClass.isInstance(extensionInstance)) {
      throw new GradleException(extensionInstance.getClass() + " is not " + extensionClass.getName());
    }
    // this is hardcoded in
    getLogger().lifecycle("appengine {");
    getLogger().lifecycle(getAllFields(extensionClass, extensionInstance));
    getLogger().lifecycle("}");
  }

  @VisibleForTesting
  static String getAllFields(Class root, Object instance) throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    for (Field field : root.getDeclaredFields()) {
      result.append(getAllFields(field, 1, instance));
    }
    return result.toString();
  }

  // recursive
  private static String getAllFields(Field root, int depth, Object instance) throws IllegalAccessException {
    StringBuilder result = new StringBuilder("");
    root.setAccessible(true);
    if (!root.getType().isPrimitive() && root.getType().getPackage().getName().equals("com.google.cloud.tools.gradle.appengine.model")) {
      result.append(Strings.repeat(" ", depth)).append(root.getName()).append(" {\n");
      for (Field child : root.getType().getDeclaredFields()) {
        result.append(getAllFields(child, depth + 1, root.get(instance)));
      }
      result.append(Strings.repeat(" ", depth)).append("}\n");
    }
    else {
      result.append(Strings.repeat(" ", depth))
          .append("(")
          .append(root.getType().getSimpleName())
          .append(getGenericTypeData(root))
          .append(") ")
          .append(root.getName())
          .append(" = ")
          .append(root.get(instance))
          .append("\n");
    }
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
}
