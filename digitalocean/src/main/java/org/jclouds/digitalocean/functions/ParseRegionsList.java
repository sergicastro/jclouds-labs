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
import org.jclouds.digitalocean.domain.Region;
import org.jclouds.digitalocean.functions.ParseRegionsList.ListRegionsResponse;
import org.jclouds.http.functions.ParseJson;

/**
 * Parses a list of {@link Region} objects.
 * 
 * @author Sergi Castro
 */
@Singleton
public class ParseRegionsList extends BaseResponseParser<ListRegionsResponse, List<Region>> {

   @Inject
   public ParseRegionsList(ParseJson<ListRegionsResponse> parser) {
      super(parser);
   }

   @Override
   protected List<Region> getReturnValue(ListRegionsResponse result) {
      return result.regions;
   }

   public static class ListRegionsResponse extends BaseResponse {
      private List<Region> regions;

      @ConstructorProperties({ "status", "error_message", "message", "regions" })
      public ListRegionsResponse(Status status, String message, String details, List<Region> regions) {
         super(status, message, details);
         this.regions = regions;
      }
   }

}
