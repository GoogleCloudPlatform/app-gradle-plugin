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

import org.junit.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectiveConfigTester {

  public static void checkConfiguration(Class<?> configuration, Class<?> parent) {
    Assert.assertTrue(parent.isAssignableFrom(configuration));

    for (Method method : parent.getMethods()) {
      assertOverridden(method, configuration);
      assertSetterPresent(method, configuration);
    }
  }

  public static void assertOverridden(Method method, Class<?> configuration) {
    for (Method m : configuration.getDeclaredMethods()) {
      if (method.getGenericReturnType().equals(m.getGenericReturnType()) &&
          Arrays.equals(method.getGenericParameterTypes(),m.getGenericParameterTypes()) &&
          method.getName().equals(m.getName())) {
        return;
      }
    }
    Assert.fail("Did not find override on " + method);
  }

  public static void assertSetterPresent(Method method, Class<?> configuration) {
    for (Method m : configuration.getDeclaredMethods()) {
      String setterName = method.getName().replaceAll("^(is|get)", "set");
      if (m.getName().equals(setterName) &&
          // TODO : use m.getParameterCount() after moving to java 8
          m.getParameterTypes().length == 1 &&
          m.getGenericParameterTypes()[0].equals(method.getGenericReturnType()) &&
          m.getReturnType().equals(Void.TYPE)) {
        return;
      }
    }
    Assert.fail("Did not find setter for " + method);
  }

}
