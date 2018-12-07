/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

public class NullSafeTest {

  @Test
  public void testConvert_null() {
    Path testPath = null;
    Assert.assertNull(NullSafe.convert(testPath, Path::toFile));
  }

  @Test
  public void testConvert_value() {
    Path testPath = Paths.get("some/path");
    File expectedResult = testPath.toFile();
    Assert.assertEquals(expectedResult, NullSafe.convert(testPath, Path::toFile));
  }
}
