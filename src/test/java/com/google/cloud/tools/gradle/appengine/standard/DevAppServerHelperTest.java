/*
 * Copyright 2017 Google LLC. All Rights Reserved.
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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.appengine.configuration.StopConfiguration;
import com.google.cloud.tools.appengine.operations.CloudSdk;
import com.google.cloud.tools.appengine.operations.DevServer;
import com.google.cloud.tools.appengine.operations.DevServers;
import com.google.cloud.tools.appengine.operations.cloudsdk.process.ProcessHandler;
import com.google.cloud.tools.gradle.appengine.standard.DevAppServerHelper.Validator;
import org.gradle.api.ProjectConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DevAppServerHelperTest {

  @Mock private CloudSdk cloudSdk;
  @Mock private DevServers devServers;
  @Mock private ProcessHandler processHandler;
  @Mock private RunExtension run;
  @Spy private Validator validator;

  @Mock private DevServer v1Server;
  @Mock private DevServer v2Server;

  @InjectMocks private DevAppServerHelper helper = new DevAppServerHelper();

  @Before
  public void setUp() {
    Mockito.when(devServers.newDevAppServer1(processHandler)).thenReturn(v1Server);
    Mockito.when(devServers.newDevAppServer2(processHandler)).thenReturn(v2Server);
  }

  @Test
  public void testGetAppServer_v1() {
    when(run.getServerVersion()).thenReturn("1");
    Assert.assertEquals(v1Server, helper.getAppServer(devServers, run, processHandler));
    verify(validator, times(1)).validateServerVersion(run.getServerVersion());
  }

  @Test
  public void testGetAppServer_v2() {
    when(run.getServerVersion()).thenReturn("2-alpha");
    Assert.assertEquals(v2Server, helper.getAppServer(devServers, run, processHandler));
    verify(validator, times(1)).validateServerVersion(run.getServerVersion());
  }

  @Test
  public void testGetAppServer_badValue() {
    when(run.getServerVersion()).thenReturn("nonsense");
    try {
      helper.getAppServer(devServers, run, processHandler);
      fail();
    } catch (ProjectConfigurationException ex) {
      Assert.assertEquals(
          "Invalid serverVersion 'nonsense' use one of " + DevAppServerHelper.SERVER_VERSIONS,
          ex.getMessage());
    }
  }

  @Test
  public void getStopConfiguration_v1() {
    when(run.getServerVersion()).thenReturn("1");
    when(run.getHost()).thenReturn("v1.com");
    when(run.getPort()).thenReturn(1234);

    StopConfiguration config = helper.getStopConfiguration(run);
    Assert.assertEquals("v1.com", config.getAdminHost());
    Assert.assertEquals(new Integer(1234), config.getAdminPort());

    verify(validator, times(1)).validateServerVersion(run.getServerVersion());
  }

  @Test
  public void getStopConfiguration_v2() {
    when(run.getServerVersion()).thenReturn("2-alpha");
    when(run.getAdminHost()).thenReturn("v2.com");
    when(run.getAdminPort()).thenReturn(4321);

    StopConfiguration config = helper.getStopConfiguration(run);
    Assert.assertEquals("v2.com", config.getAdminHost());
    Assert.assertEquals(new Integer(4321), config.getAdminPort());

    verify(validator, times(1)).validateServerVersion(run.getServerVersion());
  }

  @Test
  public void testGetStopConfiguration_badValue() {
    when(run.getServerVersion()).thenReturn("nonsense");
    try {
      helper.getStopConfiguration(run);
      fail();
    } catch (ProjectConfigurationException ex) {
      Assert.assertEquals(
          "Invalid serverVersion 'nonsense' use one of " + DevAppServerHelper.SERVER_VERSIONS,
          ex.getMessage());
    }
  }

  @Test
  public void testValidator_goodValues() {
    Validator validatorUnderTest = new Validator();

    validatorUnderTest.validateServerVersion("1");
    validatorUnderTest.validateServerVersion("2-alpha");

    // should not throw exceptions
  }

  @Test
  public void testValidator_badValue() {
    Validator validatorUnderTest = new Validator();

    try {
      validatorUnderTest.validateServerVersion("nonsense");
      fail();
    } catch (ProjectConfigurationException ex) {
      Assert.assertEquals(
          "Invalid serverVersion 'nonsense' use one of " + DevAppServerHelper.SERVER_VERSIONS,
          ex.getMessage());
    }
  }
}
