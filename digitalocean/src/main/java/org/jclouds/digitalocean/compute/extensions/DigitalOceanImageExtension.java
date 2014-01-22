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
package org.jclouds.digitalocean.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * The {@link ImageExtension} implementation for the DigitalOcean provider.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class DigitalOceanImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOceanApi api;
   private final Predicate<Integer> imageAvailablePredicate;
   private final Predicate<Integer> nodeStoppedPredicate;
   private final Function<org.jclouds.digitalocean.domain.Image, Image> imageTransformer;

   @Inject
   DigitalOceanImageExtension(DigitalOceanApi api,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<Integer> imageAvailablePredicate,
         @Named(TIMEOUT_NODE_SUSPENDED) Predicate<Integer> nodeStoppedPredicate,
         Function<org.jclouds.digitalocean.domain.Image, Image> imageTransformer) {
      this.api = Preconditions.checkNotNull(api, "api cannot be null");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate cannot be null");
      this.nodeStoppedPredicate = checkNotNull(nodeStoppedPredicate, "nodeStoppedPredicate cannot be null");
      this.imageTransformer = checkNotNull(imageTransformer, "imageTransformer cannot be null");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      Droplet droplet = null;
      try {
         droplet = api.getDropletApi().get(Integer.valueOf(id));
      } catch (Exception ex) {
         throw new NoSuchElementException("Cannot find droplet with id: " + id);
      }

      if (droplet == null) {
         throw new NoSuchElementException("Cannot find droplet with id: " + id);
      }

      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, " digitalocean only supports creating images through cloning.");
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;

      // Droplet needs to be stopped
      int powerOffEvent = api.getDropletApi().powerOff(Integer.parseInt(cloneTemplate.getSourceNodeId()));
      nodeStoppedPredicate.apply(powerOffEvent);

      int snapshotEvent = api.getDropletApi().snapshot(Integer.parseInt(cloneTemplate.getSourceNodeId()),
            cloneTemplate.getName());

      logger.info(">> registered new Image, waiting for it to become available");

      // Until the process completes we don't have enough information to build
      // an image to return
      imageAvailablePredicate.apply(snapshotEvent);

      Optional<org.jclouds.digitalocean.domain.Image> snapshot = tryFind(api.getImageApi().list(),
            new Predicate<org.jclouds.digitalocean.domain.Image>() {
               @Override
               public boolean apply(org.jclouds.digitalocean.domain.Image input) {
                  return input.getName().equals(cloneTemplate.getName());
               }
            });

      if (!snapshot.isPresent()) {
         throw new NoSuchElementException("Cannot find image with name: " + cloneTemplate.getName());
      }

      return immediateFuture(imageTransformer.apply(snapshot.get()));
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         api.getImageApi().delete(Integer.parseInt(id));
         return true;
      } catch (Exception ex) {
         return false;
      }
   }
}
