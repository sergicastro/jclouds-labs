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
package org.jclouds.digitalocean.json;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import org.easymock.EasyMock;
import org.jclouds.digitalocean.json.SshPublicKeyAdapter.SshPublicKeyWriter;
import org.jclouds.encryption.internal.JCECrypto;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for the {@link SshPublicKeyAdapter} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "SshPublicKeyAdapterTest")
public class SshPublicKeyAdapterTest {

   private Gson adapter;

   @BeforeMethod
   public void setup() throws NoSuchAlgorithmException, CertificateException {
      SshPublicKeyAdapter sshPublicKeyAdapter = new SshPublicKeyAdapter(new JCECrypto(), new SshPublicKeyWriter());
      adapter = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(PublicKey.class, sshPublicKeyAdapter)
            .create();
   }

   public void testParseRSA() throws Exception {
      String rsa = Resources.toString(getClass().getResource("/ssh-rsa.txt"), Charsets.UTF_8);

      PublicKey pub = adapter.fromJson("\"" + rsa + "\"", PublicKey.class);
      assertNotNull(pub);
      assertEquals(pub.getAlgorithm(), "RSA");

      String result = adapter.toJson(pub, PublicKey.class);
      assertEquals(result, "\"" + rsa + "\"");
   }

   public void testParseDSA() throws Exception {
      String dsa = Resources.toString(getClass().getResource("/ssh-dsa.txt"), Charsets.UTF_8);

      PublicKey pub = adapter.fromJson("\"" + dsa + "\"", PublicKey.class);
      assertNotNull(pub);
      assertEquals(pub.getAlgorithm(), "DSA");

      String result = adapter.toJson(pub, PublicKey.class);
      assertEquals(result, "\"" + dsa + "\"");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "crypto cannot be null")
   public void testCryptoIsRequired() {
      new SshPublicKeyAdapter(null, new SshPublicKeyWriter());
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "publicKeyWriter cannot be null")
   public void testPublicKeyWriterIsRequired() throws NoSuchAlgorithmException, CertificateException {
      new SshPublicKeyAdapter(new JCECrypto(), null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No Base64 part to decode")
   public void testDeserializeNoPart() throws Exception {
      adapter.fromJson("\"ssh_pub_key\":\"ssh-rsa foo bar\"", PublicKey.class);
   }

   public void testSerializeInvalidEncoding() throws Exception {
      PublicKey pub = EasyMock.createMock(PublicKey.class);

      expect(pub.getAlgorithm()).andReturn("invalid");
      replay(pub);

      try {
         adapter.toJson(pub, PublicKey.class);
         fail("Should fail to parse keys with invalid algorithms");
      } catch (IllegalArgumentException ex) {
         // Expected exception
      }

      verify(pub);
   }
}
