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
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.internal.BaseDigitalOceanLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link DropletApi} class.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Test(groups = "live", testName = "DropletApiLiveTest")
public class DropletApiLiveTest extends BaseDigitalOceanLiveTest {

   private DropletCreation dropletCreation;
   private Droplet droplet;

   @Override
   protected void initialize() {
      super.initialize();
      initializeImageSizeAndRegion();
   }

   @AfterClass
   public void cleanup() {
      if (droplet != null) {
         api.getDropletApi().destroy(droplet.getId(), true);
      }
   }

   public void testCreateDroplet() {
      dropletCreation = api.getDropletApi().create("droplettest", defaultImage.getId(), defaultSize.getId(),
            defaultRegion.getId());

      assertTrue(dropletCreation.getId() > 0, "Created droplet id should be > 0");
      assertTrue(dropletCreation.getEventId() > 0, "Droplet creation event id should be > 0");
   }

   @Test(dependsOnMethods = "testCreateDroplet")
   public void testGetDroplet() {
      waitForEvent(dropletCreation.getEventId());
      droplet = api.getDropletApi().get(dropletCreation.getId());

      assertNotNull(droplet, "Created droplet should not be null");
   }

   @Test(dependsOnMethods = "testGetDroplet")
   public void testListDroplets() {
      List<Droplet> droplets = api.getDropletApi().list();

      assertTrue(droplets.size() > 0, "Droplet list should not be empty");
   }

   @Test(dependsOnMethods = "testGetDroplet")
   public void testRebootDroplet() {
      int event = api.getDropletApi().reboot(droplet.getId());
      assertTrue(event > 0);
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testRebootDroplet")
   public void testPowerCycleDroplet() {
      int event = api.getDropletApi().powerCycle(droplet.getId());
      assertTrue(event > 0);
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerCycleDroplet")
   public void testPowerOfftDroplet() {
      int event = api.getDropletApi().powerOff(droplet.getId());
      assertTrue(event > 0);
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerOfftDroplet")
   public void testPowerOntDroplet() {
      int event = api.getDropletApi().powerOn(droplet.getId());
      assertTrue(event > 0);
      waitForEvent(event);
   }

   @Test(dependsOnMethods = "testPowerOntDroplet")
   public void testShutdowntDroplet() {
      int event = api.getDropletApi().shutdown(droplet.getId());
      assertTrue(event > 0);
      waitForEvent(event);
   }

   // TODO: resetPassword, resize, snapshot, restore, rebuild, rename
}
