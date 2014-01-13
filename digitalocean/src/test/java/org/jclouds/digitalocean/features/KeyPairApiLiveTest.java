/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.digitalocean.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ImageApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class KeyPairApiLiveTest extends BaseDigitalOceanLiveTest {

   private SshKey key;

   @AfterClass
   public void cleanup() {
      if (key != null) {
         api.getKeyPairApi().delete(key.getId());
         assertNull(api.getKeyPairApi().get(key.getId()));
      }
   }

   public void testCreateKey() throws IOException {
      String publicKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-rsa.txt"));
      key = api.getKeyPairApi().create("foo", publicKey);

      assertNotNull(key);
      assertNotNull(key.getId());
      assertEquals(key.getName(), "foo");
      assertNotNull(key.getPublicKey());
      assertEquals(key.getPublicKey().getAlgorithm(), "RSA");
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testListKeys() {
      List<SshKey> keys = api.getKeyPairApi().list();

      assertTrue(keys.size() > 0, "SSH key list should not be empty");
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testGetKey() {
      assertNotNull(api.getKeyPairApi().get(key.getId()), "The SSH key should not be null");
   }

   @Test(dependsOnMethods = "testCreateKey")
   public void testEditKey() throws IOException {
      String newKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/ssh-dsa.txt"));
      SshKey updated = api.getKeyPairApi().edit(key.getId(), newKey);

      assertNotNull(updated.getPublicKey());
      assertEquals(updated.getPublicKey().getAlgorithm(), "DSA");
   }
}
