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

package com.google.cloud.tools.gradle.appengine.util;

import org.gradle.api.GradleException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Simple parser for appengine-web.xml, this should ideally not exist, but we need it to correctly
 * error when vm=false and the user is using java8 as the target platform
 */
public class AppEngineWebXml {

  private final Document document;

  private AppEngineWebXml(File appengineWebXml) {
    try {
      document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(appengineWebXml);
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new GradleException("Failed to parse appengine-web.xml", e);
    }
  }

  public static AppEngineWebXml parse(File appengineWebXml) {
    return new AppEngineWebXml(appengineWebXml);
  }

  public boolean isVm() {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      String expression = "/appengine-web-app/vm/text()='true'";
      return (Boolean) xpath.evaluate(expression, document, XPathConstants.BOOLEAN);
    } catch (XPathExpressionException e) {
      throw new GradleException("XPath evaluation failed on appengine-web.xml", e);
    }
  }

}
