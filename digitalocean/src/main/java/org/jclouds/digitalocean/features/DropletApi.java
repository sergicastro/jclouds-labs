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

import java.io.Closeable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.domain.DropletCreation;
import org.jclouds.digitalocean.domain.options.CreateDropletOptions;
import org.jclouds.digitalocean.functions.ParseDroplet;
import org.jclouds.digitalocean.functions.ParseDropletCreation;
import org.jclouds.digitalocean.functions.ParseDropletList;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.inject.name.Named;

/**
 * Provides access to the Droplet management features.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@RequestFilters(AuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/droplets")
public interface DropletApi extends Closeable {

   /**
    * Lists all existing droplets.
    * 
    * @return The list of all existing droplets.
    */
   @Named("droplet:list")
   @GET
   @ResponseParser(ParseDropletList.class)
   List<Droplet> listDroplets();

   /**
    * Gets the details of the given droplet.
    * 
    * @param id The id of the droplet to get.
    * @return The details of the droplet or <code>null</code> if no droplet
    *         exists with the given id.
    */
   @Named("droplet:get")
   @GET
   @Path("/{id}")
   @ResponseParser(ParseDroplet.class)
   @Fallback(NullOnNotFoundOr404.class)
   Droplet getDroplet(@PathParam("id") int id);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageId The id of the image to use to create the droplet.
    * @param sizeId The size to use to create the droplet.
    * @param regionId The region where the droplet must be created.
    * @return The created droplet.
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @ResponseParser(ParseDropletCreation.class)
   DropletCreation createDroplet(@QueryParam("name") String name, @QueryParam("image_id") int imageId,
         @QueryParam("size_id") int sizeId, @QueryParam("region_id") int regionId);

   /**
    * Creates a new droplet.
    * 
    * @param name The name for the new droplet.
    * @param imageId The id of the image to use to create the droplet.
    * @param sizeId The size to use to create the droplet.
    * @param regionId The region where the droplet must be created.
    * @param options Custom options to create the droplet.
    * @return The created droplet.
    */
   @Named("droplet:create")
   @GET
   @Path("/new")
   @ResponseParser(ParseDropletCreation.class)
   DropletCreation createDroplet(@QueryParam("name") String name, @QueryParam("image_id") int imageId,
         @QueryParam("size_id") int sizeId, @QueryParam("region_id") int regionId, CreateDropletOptions options);

}
