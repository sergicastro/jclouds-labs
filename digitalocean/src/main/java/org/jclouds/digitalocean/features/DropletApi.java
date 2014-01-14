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
import javax.ws.rs.core.MediaType;

import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.functions.ParseDropletList;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
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
}
