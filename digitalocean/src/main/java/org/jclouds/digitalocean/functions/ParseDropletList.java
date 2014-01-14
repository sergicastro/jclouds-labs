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
package org.jclouds.digitalocean.functions;

import java.beans.ConstructorProperties;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean.domain.BaseResponse;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.functions.ParseDropletList.ListDropletsResponse;
import org.jclouds.http.functions.ParseJson;

/**
 * Parses a list of {@link Droplet} objects.
 * 
 * @author Sergi Castro
 */
@Singleton
public class ParseDropletList extends BaseResponseParser<ListDropletsResponse, List<Droplet>> {

   @Inject
   public ParseDropletList(ParseJson<ListDropletsResponse> parser) {
      super(parser);
   }

   @Override
   protected List<Droplet> getReturnValue(ListDropletsResponse result) {
      return result.droplets;
   }

   public static class ListDropletsResponse extends BaseResponse {
      private List<Droplet> droplets;

      @ConstructorProperties({ "status", "error_message", "message", "droplets" })
      public ListDropletsResponse(Status status, String message, String details, List<Droplet> droplets) {
         super(status, message, details);
         this.droplets = droplets;
      }
   }

}
