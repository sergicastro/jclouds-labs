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

import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.digitalocean.domain.Droplet;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

/**
 * Transforms an {@link Status} to the jclouds portable model.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class DropletStatusToStatus implements Function<Droplet.Status, Status> {

   private static final Function<Droplet.Status, Status> toPortableStatus = Functions.forMap(//
         ImmutableMap.<Droplet.Status, Status> builder() //
               .put(Droplet.Status.active, Status.RUNNING) //
               // TODO: Add missing droplet status
               .build(), //
         Status.UNRECOGNIZED);

   @Override
   public Status apply(final Droplet.Status input) {
      return toPortableStatus.apply(input);
   }
}
