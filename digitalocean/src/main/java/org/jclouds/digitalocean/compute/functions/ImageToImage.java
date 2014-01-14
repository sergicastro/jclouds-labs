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
package org.jclouds.digitalocean.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.digitalocean.domain.Image;

import com.google.common.base.Function;

/**
 * Transforms an {@link Image} to the jclouds portable model.
 * 
 * @author Sergi Castro
 */
@Singleton
public class ImageToImage implements Function<Image, org.jclouds.compute.domain.Image> {

   @Override
   public org.jclouds.compute.domain.Image apply(Image input) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(String.valueOf(input.getId()));
      builder.name(input.getName());
      builder.description(input.getName());
      builder.status(Status.AVAILABLE);

      builder.operatingSystem(OperatingSystem.builder() //
            .name(input.getName()) //
            .family(OsFamily.fromValue(input.getDistribution())) //
            .description(input.getName()) //
            .arch("TODO") // TODO: Parse arch from name
            .version("TODO") // TODO: Parse version from name
            .is64Bit(false) // TODO: Parse is64bit from name
            .build());

      // TODO: builder.defaultCredentials

      return builder.build();
   }
}
