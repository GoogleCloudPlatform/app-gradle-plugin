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

import com.google.cloud.tools.gradle.appengine.model.AppEngineFlexibleExtension;
import com.google.cloud.tools.gradle.appengine.model.AppEngineStandardExtension;
import com.google.cloud.tools.gradle.appengine.model.TestGrandParent;

import org.gradle.internal.impldep.org.testng.Assert;
import org.junit.Test;

/**
 * Test ShowConfigurationTask
 */
public class ShowConfigurationTaskTest {
  private static String expected = ""
      + " parent {\n"
      + "  (Map<String, Integer>) member1 = null\n"
      + "  child {\n"
      + "   (List<String>) member1 = [hello]\n"
      + "   (int) member2 = 5\n"
      + "   (List<String>) member3 = null\n"
      + "  }\n"
      + " }\n";

  @Test
  public void testGetAllFields() throws IllegalAccessException {
    String result = ShowConfigurationTask.getAllFields(TestGrandParent.class, new TestGrandParent());
    Assert.assertEquals(result, expected);
  }

  @Test
  public void testGetAllFields_AppEngineShowWithoutException() throws IllegalAccessException {
    ShowConfigurationTask
        .getAllFields(AppEngineFlexibleExtension.class, new AppEngineFlexibleExtension());
    ShowConfigurationTask
        .getAllFields(AppEngineStandardExtension.class, new AppEngineStandardExtension());
  }
}
