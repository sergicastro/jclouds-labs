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
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.internal.BaseDigitalOceanMockTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Mock tests for the {@link ImageApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
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

   public void testGetImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/image.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         Image image = imageApi.getImage(2);

         assertRequestHasCommonFields(server.takeRequest(), "/images/2");
         assertNotNull(image);
         assertEquals(image.getId(), 2);
         assertEquals(image.getDistribution(), "Ubuntu");
         assertEquals(image.getName(), "Automated Backup");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testGetUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

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

   public void testDeleteImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse());

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         imageApi.deleteImage(15);

         assertRequestHasCommonFields(server.takeRequest(), "/images/15/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testDeleteUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         try {
            imageApi.deleteImage(15);
            fail("Delete image should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasCommonFields(server.takeRequest(), "/images/15/destroy");
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testTransferUnexistingImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         try {
            imageApi.transferImage(47, 23);
            fail("Transfer image should fail on 404");
         } catch (ResourceNotFoundException ex) {
            // Expected exception
         }

         assertRequestHasParameters(server.takeRequest(), "/images/47/transfer",
               ImmutableMultimap.of("region_id", "23"));
      } finally {
         api.close();
         server.shutdown();
      }
   }

   public void testTransferImage() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(payloadFromResource("/eventid.json")));

      DigitalOceanApi api = api(server.getUrl("/"));
      ImageApi imageApi = api.getImageApi();

      try {
         int eventId = imageApi.transferImage(47, 23);

         assertRequestHasParameters(server.takeRequest(), "/images/47/transfer",
               ImmutableMultimap.of("region_id", "23"));
         assertEquals(eventId, 7499);
      } finally {
         api.close();
         server.shutdown();
      }
   }
}
