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
package org.jclouds.digitalocean.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.digitalocean.compute.functions.DropletToNodeMetadata;
import org.jclouds.digitalocean.compute.functions.ImageToImage;
import org.jclouds.digitalocean.compute.functions.RegionToLocation;
import org.jclouds.digitalocean.compute.functions.SizeToHardware;
import org.jclouds.digitalocean.compute.strategy.DigitalOceanComputeServiceAdapter;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * Configures the compute service classes for the DigitalOcean API.
 * 
 * @author Sergi Castro
 */
public class DigitalOceanComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<Droplet, Size, Image, Region> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<Droplet, Size, Image, Region>>() {
      }).to(DigitalOceanComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Droplet, NodeMetadata>>() {
      }).to(DropletToNodeMetadata.class);
      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(ImageToImage.class);
      bind(new TypeLiteral<Function<Region, Location>>() {
      }).to(RegionToLocation.class);
      bind(new TypeLiteral<Function<Size, Hardware>>() {
      }).to(SizeToHardware.class);
      install(new LocationsFromComputeServiceAdapterModule<Droplet, Size, Image, Region>() {
      });
   }
}
