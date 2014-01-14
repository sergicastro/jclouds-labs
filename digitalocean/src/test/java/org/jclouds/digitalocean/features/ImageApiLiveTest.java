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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ImageApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseDigitalOceanLiveTest {

   private ImageApi imageApi;

   private List<Image> images;

   @Override
   protected void initialize() {
      super.initialize();
      imageApi = api.getImageApi();
   }

   public void testListImages() {
      images = imageApi.listImages();

      assertTrue(images.size() > 0, "Image list should not be empty");
   }

   @Test(dependsOnMethods = "testListImages")
   public void testGetImage() {
      assertNotNull(imageApi.getImage(images.get(0).getId()), "The image should not be null");
   }

   public void testGetImageNotFound() {
      assertNull(imageApi.getImage(-1));
   }
}
