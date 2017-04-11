/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
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

import com.google.cloud.tools.gradle.appengine.core.model.ExtX;
import com.google.cloud.tools.gradle.appengine.core.model.ExtY;
import com.google.cloud.tools.gradle.appengine.core.model.ExtZ;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Test;

/** Test for ShowConfigurationTask. */
public class ShowConfigurationTaskTest {

  @Test
  public void testGetAllFields_NestedExtensions() throws IllegalAccessException {
    String expected =
        ""
            + "root {\n"
            + "  x {\n"
            + "    y {\n"
            + "      (int) yy = 0\n"
            + "      z {\n"
            + "        (String) zz = hello\n"
            + "        (Map<String, List<String>>) zzNested = {a=[a1, a2], b=[b1, b2]}\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n";
    Project p = ProjectBuilder.builder().build();
    ExtensionAware root = (ExtensionAware) p.getExtensions().create("root", ExtX.class);
    ExtensionAware x = (ExtensionAware) root.getExtensions().create("x", ExtX.class);
    ExtensionAware y = (ExtensionAware) x.getExtensions().create("y", ExtY.class);
    y.getExtensions().create("z", ExtZ.class);

    String result = ShowConfigurationTask.getExtensionData("root", root, 0);
    Assert.assertEquals(expected, result);
  }
}
