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

package com.google.cloud.tools.gradle.appengine.standard;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RunExtensionTest {

  @Rule public TemporaryFolder tmpDir = new TemporaryFolder();

  @Test
  @SuppressWarnings("deprecation") // this is intentionally testing deprecated methods
  public void testSetAppYamls() throws IOException {
    Project p = ProjectBuilder.builder().build();
    RunExtension run = p.getExtensions().create("run", RunExtension.class, p);

    // set some default for services
    run.setServices(ImmutableList.of(tmpDir.getRoot()));
    Assert.assertEquals(ImmutableList.of(tmpDir.getRoot()), run.getServices());

    File file = new File("/tmp/some/app.yaml");
    run.setAppYamls(file);
    // setAppYamls transforms the file list
    Assert.assertEquals(new ArrayList<>(p.files(file).getFiles()), run.getServices());
  }
}
