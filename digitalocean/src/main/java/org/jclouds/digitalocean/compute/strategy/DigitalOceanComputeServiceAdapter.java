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
package org.jclouds.digitalocean.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.tryFind;

import java.security.KeyPair;
import java.security.PublicKey;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Implementation of the Compute Service for the DigitalOcean API.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
public class DigitalOceanComputeServiceAdapter implements ComputeServiceAdapter<Droplet, Size, Image, Region> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final DigitalOceanApi api;
   private final Crypto crypto;
   private final Function<PublicKey, String> publicKeyWriter;

   @Inject
   DigitalOceanComputeServiceAdapter(DigitalOceanApi api, Crypto crypto, Function<PublicKey, String> publicKeyWriter) {
      this.api = checkNotNull(api, "api cannot be null");
      this.crypto = checkNotNull(crypto, "crypto cannot be null");
      this.publicKeyWriter = checkNotNull(publicKeyWriter, "publicKeyWriter cannot be null");
   }

   @Override
   public NodeAndInitialCredentials<Droplet> createNodeWithGroupEncodedIntoName(String group, final String name,
         Template template) {
      // Generate a new keypair to access the node
      KeyPair keys = crypto.rsaKeyPairGenerator().generateKeyPair();
      SshKey initialKey = api.getKeyPairApi().createKey(group, publicKeyWriter.apply(keys.getPublic()));

      // TODO: Create the droplet using the generated ssh key
      Droplet droplet = null;

      LoginCredentials nodeCredentials = LoginCredentials.builder() //
            .identity("root")//
            .authenticateSudo(Boolean.TRUE.equals(template.getOptions().shouldAuthenticateSudo())) //
            .privateKey(Pems.pem(keys.getPrivate()))//
            .build();

      return new NodeAndInitialCredentials<Droplet>(droplet, String.valueOf(droplet.getId()), nodeCredentials);
   }

   @Override
   public Iterable<Image> listImages() {
      return api.getImageApi().listImages();
   }

   @Override
   public Iterable<Size> listHardwareProfiles() {
      return api.getSizesApi().listSizes();
   }

   @Override
   public Iterable<Region> listLocations() {
      return api.getReRegionApi().listRegions();
   }

   @Override
   public Iterable<Droplet> listNodes() {
      return api.getDropletApi().listDroplets();
   }

   @Override
   public Iterable<Droplet> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Droplet>() {
         @Override
         public boolean apply(Droplet droplet) {
            return contains(ids, String.valueOf(droplet.getId()));
         }
      });
   }

   @Override
   public Image getImage(String id) {
      return api.getImageApi().getImage(Integer.parseInt(id));
   }

   @Override
   public Droplet getNode(String id) {
      return null;
   }

   @Override
   public void destroyNode(String id) {
      final Droplet droplet = getNode(id);

      // TODO: Delete droplet

      Optional<SshKey> keyForNode = tryFind(api.getKeyPairApi().listKeys(), new Predicate<SshKey>() {
         @Override
         public boolean apply(SshKey input) {
            return input.getName().equals(droplet.getName());
         }
      });

      if (keyForNode.isPresent()) {
         api.getKeyPairApi().deleteKey(keyForNode.get().getId());
      }
   }

   @Override
   public void rebootNode(String id) {

   }

   @Override
   public void resumeNode(String id) {

   }

   @Override
   public void suspendNode(String id) {

   }

}
