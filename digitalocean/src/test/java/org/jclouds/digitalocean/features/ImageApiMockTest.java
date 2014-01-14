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
import static org.testng.Assert.assertNull;

import java.util.List;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ImageApi} class.
 * 
 * @author Sergi Castro
 */
@Test(groups = "unit", testName = "ImageApiMockTest")
public class ImageApiMockTest extends BaseDigitalOceanMockTest {

   @Test(expectedExceptions = HttpResponseException.class, expectedExceptionsMessageRegExp = "No Image Found")
   public void testListImagesFailure() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/error.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.listImages();
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testListImages() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/images.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         List<Image> images = imageApi.listImages();

         assertRequestHasCommonFields(server.takeRequest(), "/images");
         assertEquals(images.size(), 3);
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetImageNotFound() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setStatus("404"));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         Image image = imageApi.getImage(15);

         assertRequestHasCommonFields(server.takeRequest(), "/images/15");
         assertNull(image);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
