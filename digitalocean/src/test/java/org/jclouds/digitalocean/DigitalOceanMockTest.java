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
package org.jclouds.digitalocean;

import static org.testng.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

/**
 * @author Sergi Castro
 */
@Test(groups = "unit", testName = "DigitalOceanMockTest")
public class DigitalOceanMockTest extends BaseDigitalOceanMockTest<DigitalOceanApi> {

   private void mockResponse(final MockWebServer server, final String jsonBody) {
      server.enqueue(new MockResponse().setBody(payloadFromResource(jsonBody)));
   }

   private void assertRequest(final MockWebServer server, final String path) throws InterruptedException {
      RecordedRequest request = server.takeRequest();

      assertEquals(request.getRequestLine(), "GET " + urlWithCredentials(path) + " HTTP/1.1");
      assertEquals(request.getHeader(HttpHeaders.ACCEPT), MediaType.APPLICATION_JSON);
   }

   public void testListImages() throws Exception {
      MockWebServer server = mockWebServer();
      try {
         DigitalOceanApi api = api(server.getUrl("/"));
         mockResponse(server, "/images.json");

         List<Image> images = api.listImages();

         assertRequest(server, "/images");

         // Verify the response is properly parsed
         assertEquals(images.size(), 3);
      } finally {
         server.shutdown();
      }
   }

   public void testListRegions() throws Exception {
      MockWebServer server = mockWebServer();
      try {
         DigitalOceanApi api = api(server.getUrl("/"));
         mockResponse(server, "/regions.json");

         List<Region> regions = api.listRegions();

         assertRequest(server, "/regions");

         // Verify the response is properly parsed
         assertEquals(regions.size(), 4);
      } finally {
         server.shutdown();
      }
   }

   public void testListSizes() throws Exception {
      MockWebServer server = mockWebServer();
      try {
         DigitalOceanApi api = api(server.getUrl("/"));
         mockResponse(server, "/sizes.json");

         List<Size> sizes = api.listSizes();

         assertRequest(server, "/sizes");

         // Verify the response is properly parsed
         assertEquals(sizes.size(), 4);
      } finally {
         server.shutdown();
      }
   }

   public void testListDroplets() throws Exception {
      MockWebServer server = mockWebServer();
      try {
         DigitalOceanApi api = api(server.getUrl("/"));
         mockResponse(server, "/droplets.json");

         List<Droplet> sizes = api.listDroplets();

         assertRequest(server, "/droplets");

         // Verify the response is properly parsed
         assertEquals(sizes.size(), 1);
      } finally {
         server.shutdown();
      }
   }
}
