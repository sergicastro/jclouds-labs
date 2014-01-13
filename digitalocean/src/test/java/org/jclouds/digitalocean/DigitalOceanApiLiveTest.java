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

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.testng.annotations.Test;

/**
 * @author Sergi Castro
 */
@Test(groups = "live", testName = "DigitalOceanApiLiveTest")
public class DigitalOceanApiLiveTest extends BaseApiLiveTest<DigitalOceanApi> {

   public DigitalOceanApiLiveTest() {
      provider = "digitalocean";
   }

   public void testListImages() {
      List<Image> images = api.listImages();

      assertTrue(images.size() > 0, "Image list should not be empty");
   }

   public void testListRegions() {
      List<Region> regions = api.listRegions();

      assertTrue(regions.size() > 0, "Region list should not be empty");
   }

   public void testListSizes() {
      List<Size> sizes = api.listSizes();

      assertTrue(sizes.size() > 0, "Size list should not be empty");
   }

   public void testListDroplets() {
      List<Droplet> droplets = api.listDroplets();

      assertTrue(droplets.size() > 0, "Droplets list should not be empty");
   }
}
