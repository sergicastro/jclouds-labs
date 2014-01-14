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

import static com.google.common.collect.Iterables.find;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Live tests for the {@link ImageApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseDigitalOceanLiveTest {

   private ImageApi imageApi;
   private RegionApi regionApi;

   private Image image;

   @Override
   protected void initialize() {
      super.initialize();
      imageApi = api.getImageApi();
      regionApi = api.getReRegionApi();
   }

   public void testListImages() {
      List<Image> images = imageApi.listImages();

      assertTrue(images.size() > 0, "Image list should not be empty");
      image = images.get(0);
   }

   @Test(dependsOnMethods = "testListImages")
   public void testGetImage() {
      assertNotNull(imageApi.getImage(image.getId()), "The image should not be null");
   }

   public void testGetImageNotFound() {
      assertNull(imageApi.getImage(-1));
   }

   @Test(dependsOnMethods = "testListImages")
   public void testTransferImage() {
      // Find a different region to be used as the destination
      Region region = find(regionApi.listRegions(), new Predicate<Region>() {
         @Override
         public boolean apply(Region input) {
            return input.getId() != image.getId();
         }
      });

      int eventId = imageApi.transferImage(image.getId(), region.getId());
      assertTrue(eventId > 0);
   }

   @Test(dependsOnMethods = { "testListImages", "testGetImageNotFound", "testGetImage", "testTransferImage" })
   public void testDeleteImage() throws IOException {
      int imageId = image.getId();
      imageApi.deleteImage(imageId);
      assertNull(imageApi.getImage(imageId));
   }
}
