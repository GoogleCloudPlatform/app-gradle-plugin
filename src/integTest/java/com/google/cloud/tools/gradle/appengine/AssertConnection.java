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

package com.google.cloud.tools.gradle.appengine;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

/** Assertions for checking connections web apps. */
public class AssertConnection {

  /** Connect and assert response is as expected. */
  public static void assertResponse(String url, int expectedCode, String expectedText) {
    try {
      HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
      int responseCode = urlConnection.getResponseCode();
      Assert.assertEquals(expectedCode, responseCode);
      String response = CharStreams.toString(new InputStreamReader(urlConnection.getInputStream()));
      Assert.assertThat(response, CoreMatchers.equalTo(expectedText));
    } catch (IOException e) {
      Assert.fail("IOException while running test");
    }
  }

  /** Connect and assert that the target is unreachable. */
  public static void assertUnreachable(String url, long timeoutInMillis)
      throws IOException, InterruptedException {

    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < timeoutInMillis) {
      try {
        HttpURLConnection urlConnection =
            (HttpURLConnection) new URL("http://localhost:8080").openConnection();
        urlConnection.getResponseCode();
      } catch (ConnectException ce) {
        return; // ConnectException is what we want
      }
      Thread.sleep(500);
    }
    Assert.fail("ConnectException expected");
  }
}
