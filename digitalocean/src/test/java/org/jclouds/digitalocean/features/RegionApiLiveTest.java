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

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.digitalocean.domain.Region;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link RegionApi} class.
 * 
 * @author Sergi Castro
 */
@Test(groups = "live", testName = "RegionApiLiveTest")
public class RegionApiLiveTest extends BaseApiLiveTest<RegionApi> {

   public RegionApiLiveTest() {
      provider = "digitalocean";
   }

   public void testListRegions() {
      List<Region> regions = api.listRegions();

      assertTrue(regions.size() > 0, "Region list should not be empty");
   }
}
