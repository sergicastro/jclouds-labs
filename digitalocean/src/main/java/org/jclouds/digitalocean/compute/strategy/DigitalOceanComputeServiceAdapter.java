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
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.compute.options.DigitalOceanTemplateOptions;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.domain.options.CreateDropletOptions;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

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
   private final Predicate<DropletCreation> dropletProvisionedCheck;

   @Inject
   DigitalOceanComputeServiceAdapter(DigitalOceanApi api,
         @Named(TIMEOUT_NODE_RUNNING) Predicate<DropletCreation> dropletProvisionedCheck) {
      this.api = checkNotNull(api, "api cannot be null");
      this.dropletProvisionedCheck = checkNotNull(dropletProvisionedCheck, "dropletProvisionedCheck cannot be null");
   }

   @Override
   public NodeAndInitialCredentials<Droplet> createNodeWithGroupEncodedIntoName(String group, final String name,
         Template template) {
      DigitalOceanTemplateOptions templateOptions = template.getOptions().as(DigitalOceanTemplateOptions.class);

      CreateDropletOptions.Builder options = CreateDropletOptions.builder();

      // Check if there is a key to authorize in the portable options
      if (!Strings.isNullOrEmpty(template.getOptions().getPublicKey())) {
         logger.debug(">> creating keypair for node...");
         SshKey key = api.getKeyPairApi().create(name, template.getOptions().getPublicKey());
         logger.debug(">> keypair created! %s", key);
         options.addSshKeyId(key.getId());
      }

      // DigitalOcean specific options
      if (!templateOptions.getSshKeyIds().isEmpty()) {
         options.addSshKeyIds(templateOptions.getSshKeyIds());
      }
      if (templateOptions.getPrivateNetworking() != null) {
         options.privateNetworking(templateOptions.getPrivateNetworking());
      }
      if (templateOptions.getBackupsEnabled() != null) {
         options.backupsEnabled(templateOptions.getBackupsEnabled());
      }

      DropletCreation dropletCreation = api.getDropletApi().create(name,
            Integer.parseInt(template.getImage().getProviderId()), //
            Integer.parseInt(template.getHardware().getProviderId()),//
            Integer.parseInt(template.getLocation().getId()), //
            options.build());

      // We have to actively wait until the droplet has been provisioned until
      // we can build the entire Droplet object we want to return
      dropletProvisionedCheck.apply(dropletCreation);
      Droplet droplet = api.getDropletApi().get(dropletCreation.getId());

      // Don't set the node credentials. If credentials are given in the
      // options, those will be used. Otherwise, the credentials of the image
      // will be used.
      return new NodeAndInitialCredentials<Droplet>(droplet, String.valueOf(droplet.getId()), null);
   }

   @Override
   public Iterable<Image> listImages() {
      return api.getImageApi().list();
   }

   @Override
   public Iterable<Size> listHardwareProfiles() {
      return api.getSizesApi().list();
   }

   @Override
   public Iterable<Region> listLocations() {
      return api.getReRegionApi().list();
   }

   @Override
   public Iterable<Droplet> listNodes() {
      return api.getDropletApi().list();
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
      return api.getImageApi().get(Integer.parseInt(id));
   }

   @Override
   public Droplet getNode(String id) {
      return api.getDropletApi().get(Integer.valueOf(id));
   }

   @Override
   public void destroyNode(String id) {
      api.getDropletApi().destroy(Integer.valueOf(id), true);
   }

   @Override
   public void rebootNode(String id) {
      api.getDropletApi().reboot(Integer.valueOf(id));
   }

   @Override
   public void resumeNode(String id) {
      api.getDropletApi().powerOn(Integer.valueOf(id));
   }

   @Override
   public void suspendNode(String id) {
      api.getDropletApi().powerOff(Integer.valueOf(id));
   }

}
