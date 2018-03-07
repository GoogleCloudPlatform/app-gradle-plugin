/*
 * Copyright (c) 2018 Google Inc. All Right Reserved.
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

import com.google.cloud.tools.managedcloudsdk.BadCloudSdkVersionException;
import com.google.cloud.tools.managedcloudsdk.ManagedCloudSdk;
import com.google.cloud.tools.managedcloudsdk.UnsupportedOsException;
import com.google.cloud.tools.managedcloudsdk.Version;
import org.junit.Assert;
import org.junit.Test;

public class ManagedCloudSdkFactoryTest {

  @Test
  public void testNewManagedSdk() throws UnsupportedOsException, BadCloudSdkVersionException {
    ManagedCloudSdk sdk;
    ManagedCloudSdkFactory factory = new ManagedCloudSdkFactory();

    // There's no way of testing for direct ManagedCloudSdk equality, so compare home paths
    sdk = factory.newManagedSdk(null);
    Assert.assertEquals(ManagedCloudSdk.newManagedSdk().getSdkHome(), sdk.getSdkHome());

    sdk = factory.newManagedSdk("LATEST");
    Assert.assertEquals(ManagedCloudSdk.newManagedSdk().getSdkHome(), sdk.getSdkHome());

    sdk = factory.newManagedSdk("191.0.0");
    Assert.assertEquals(
        ManagedCloudSdk.newManagedSdk(new Version("191.0.0")).getSdkHome(), sdk.getSdkHome());
  }
}
