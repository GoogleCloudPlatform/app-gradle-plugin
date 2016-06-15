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

package com.google.cloud.tools.gradle.appengine.task.io;

import com.google.cloud.tools.appengine.cloudsdk.process.ProcessOutputLineListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class FileOutputLineListener implements ProcessOutputLineListener {
  final PrintStream logFilePrinter;

  public FileOutputLineListener(File logFile) throws IOException {
    logFilePrinter = new PrintStream(logFile);
  }

  @Override
  public void onOutputLine(String line) {
    logFilePrinter.println(line);
  }
}
