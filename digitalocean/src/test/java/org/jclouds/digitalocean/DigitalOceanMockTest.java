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

import org.jclouds.digitalocean.domain.Image;
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

   public void testListImages() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/images.json")));

      try {
         DigitalOceanApi api = api(server.getUrl("/"));
         List<Image> images = api.listImages();

         // Verify the generated request
         RecordedRequest request = server.takeRequest();

         assertEquals(request.getRequestLine(), "GET " + urlWithCredentials("/images") + " HTTP/1.1");
         assertEquals(request.getHeader(HttpHeaders.ACCEPT), MediaType.APPLICATION_JSON);

         // Verify the response ir properly parsed
         assertEquals(images.size(), 3);
      } finally {
         server.shutdown();
      }
   }
}
