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

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Droplet.Status;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link DropletApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "DropletApiMockTest")
public class DropletApiMockTest extends BaseDigitalOceanMockTest {

   public void testListDroplets() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplets.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         List<Droplet> sizes = dropletApi.listDroplets();

         assertRequestHasCommonFields(server.takeRequest(), "/droplets");
         assertEquals(sizes.size(), 1);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetDroplet() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/droplet.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         Droplet droplet = dropletApi.getDroplet(100823);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/100823");
         assertNotNull(droplet);
         assertNotNull(droplet.getBackups());
         assertNotNull(droplet.getSnapshots());
         assertEquals(droplet.getName(), "test222");
         assertEquals(droplet.getStatus(), Status.ACTIVE);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetUnexistingEvent() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      DropletApi dropletApi = api.getDropletApi();

      try {
         Droplet droplet = dropletApi.getDroplet(100823);

         assertRequestHasCommonFields(server.takeRequest(), "/droplets/100823");
         assertNull(droplet);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
