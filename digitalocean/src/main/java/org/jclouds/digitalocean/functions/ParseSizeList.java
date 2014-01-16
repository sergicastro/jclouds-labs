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
import org.jclouds.digitalocean.domain.Size;
import org.jclouds.digitalocean.functions.ParseSizeList.ListSizesResponse;
import org.jclouds.http.functions.ParseJson;

/**
 * Parses a list of {@link Size} objects.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class ParseSizeList extends BaseResponseParser<ListSizesResponse, List<Size>> {

   @Inject
   ParseSizeList(ParseJson<ListSizesResponse> parser) {
      super(parser);
   }

   @Override
   protected List<Size> getReturnValue(ListSizesResponse result) {
      return result.sizes;
   }

   public static class ListSizesResponse extends BaseResponse {
      private final List<Size> sizes;

      @ConstructorProperties({ "status", "error_message", "message", "sizes" })
      public ListSizesResponse(Status status, String message, String details, List<Size> sizes) {
         super(status, message, details);
         this.sizes = sizes;
      }
   }

}
