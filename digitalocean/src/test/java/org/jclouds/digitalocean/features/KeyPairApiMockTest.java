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
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Resources;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link KeyPairApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "KeyPairApiMockTest")
public class KeyPairApiMockTest extends BaseDigitalOceanMockTest {

   public void testListKeys() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/keys.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         List<SshKey> keys = keyPairApi.listKeys();

         assertRequestHasCommonFields(server.takeRequest(), "/ssh_keys");
         assertEquals(keys.size(), 1);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/key.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         SshKey key = keyPairApi.getKey(47);

         assertRequestHasCommonFields(server.takeRequest(), "/ssh_keys/47");
         assertEquals(key.getId(), 47);
         assertEquals(key.getName(), "my_key");
         assertNotNull(key.getPublicKey());
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetUnexistingKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         SshKey key = keyPairApi.getKey(47);

         assertRequestHasCommonFields(server.takeRequest(), "/ssh_keys/47");
         assertNull(key);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   @Test
   public void testCreateKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/key.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         String publicKey = Resources.toString(getClass().getResource("/ssh-rsa.txt"), Charsets.UTF_8);
         SshKey key = keyPairApi.createKey("my_key", publicKey);

         assertRequestHasParameters(server.takeRequest(), "/ssh_keys/new",
               ImmutableMultimap.of("name", "my_key", "ssh_pub_key", publicKey));

         assertEquals(key.getId(), 47);
         assertEquals(key.getName(), "my_key");
         assertNotNull(key.getPublicKey());
      } finally {
         api.close();
         server.shutdown();
      }
   }

   @Test
   public void testEditKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/key.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         String publicKey = Resources.toString(getClass().getResource("/ssh-rsa.txt"), Charsets.UTF_8);
         SshKey key = keyPairApi.editKey(47, publicKey);

         assertRequestHasParameters(server.takeRequest(), "/ssh_keys/47/edit",
               ImmutableMultimap.of("ssh_pub_key", publicKey));

         assertEquals(key.getId(), 47);
         assertEquals(key.getName(), "my_key");
         assertNotNull(key.getPublicKey());
      } finally {
         api.close();
         server.shutdown();
      }
   }

   @Test
   public void testEditUnexistingKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         String publicKey = Resources.toString(getClass().getResource("/ssh-rsa.txt"), Charsets.UTF_8);

         try {
            keyPairApi.editKey(47, publicKey);
            fail("Edit key should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/ssh_keys/47/edit",
               ImmutableMultimap.of("ssh_pub_key", publicKey));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse());

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         keyPairApi.deleteKey(47);

         assertRequestHasCommonFields(server.takeRequest(), "/ssh_keys/47/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   @Test
   public void testDeleteUnexistingKey() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      KeyPairApi keyPairApi = api.getKeyPairApi();

      try {
         try {
            keyPairApi.deleteKey(47);
            fail("Delete key should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/ssh_keys/47/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
