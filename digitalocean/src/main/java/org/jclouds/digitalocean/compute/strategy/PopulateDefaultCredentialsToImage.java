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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.digitalocean.compute.config.DigitalOceanComputeServiceContextModule.DefaultImageCredetials;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Supplier;

/**
 * Populates the default credentials to the images.
 * <p>
 * A key pair will be generated and will be shared by all images.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class PopulateDefaultCredentialsToImage implements PopulateDefaultLoginCredentialsForImageStrategy {

   private final Supplier<DefaultImageCredetials> defaultCredentials;

   @Inject
   PopulateDefaultCredentialsToImage(@Memoized Supplier<DefaultImageCredetials> defaultCredentials) {
      this.defaultCredentials = checkNotNull(defaultCredentials, "defaultCredentials cannot be null");
   }

   @Override
   public LoginCredentials apply(Object image) {
      return defaultCredentials.get().getCredentials();
   }

}
