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
package org.jclouds.digitalocean.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.security.PublicKey;

import org.jclouds.digitalocean.config.DigitalOceanParserModule.PublicKeyAdapter;
import org.jclouds.digitalocean.config.DigitalOceanParserModule.PublicKeyAdapterImpl;
import org.jclouds.encryption.internal.JCECrypto;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonPrimitive;

/**
 * Unit tests for the {@link PublicKeyAdapter} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "PublicKeyAdapterTest")
public class PublicKeyAdapterTest {

   public void testDeserializeRSA() throws Exception {
      String rsa = Resources.toString(getClass().getResource("/ssh-rsa.txt"), Charsets.UTF_8);

      PublicKeyAdapter adapter = new PublicKeyAdapterImpl(new JCECrypto());
      PublicKey pub = adapter.deserialize(new JsonPrimitive(rsa), PublicKey.class, null);

      assertNotNull(pub);
      assertEquals(pub.getAlgorithm(), "RSA");
   }

   public void testDeserializeDSA() throws Exception {
      String dss = Resources.toString(getClass().getResource("/ssh-dsa.txt"), Charsets.UTF_8);

      PublicKeyAdapter adapter = new PublicKeyAdapterImpl(new JCECrypto());
      PublicKey pub = adapter.deserialize(new JsonPrimitive(dss), PublicKey.class, null);

      assertNotNull(pub);
      assertEquals(pub.getAlgorithm(), "DSA");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "crypto cannot be null")
   public void testCryptoIsRequired() {
      new PublicKeyAdapterImpl(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No Base64 part to decode")
   public void testDeserializeNoPart() throws Exception {
      String invalid = "ssh-rsa foo bar";

      PublicKeyAdapter adapter = new PublicKeyAdapterImpl(new JCECrypto());
      adapter.deserialize(new JsonPrimitive(invalid), PublicKey.class, null);
   }
}
