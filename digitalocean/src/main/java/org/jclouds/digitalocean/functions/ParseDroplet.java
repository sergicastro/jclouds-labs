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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean.domain.BaseResponse;
import org.jclouds.digitalocean.domain.Droplet;
import org.jclouds.digitalocean.functions.ParseDroplet.DropletResponse;
import org.jclouds.http.functions.ParseJson;

/**
 * Parses a response with a {@link Droplet}.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class ParseDroplet extends BaseResponseParser<DropletResponse, Droplet> {

   @Inject
   ParseDroplet(ParseJson<DropletResponse> parser) {
      super(parser);
   }

   @Override
   protected Droplet getReturnValue(DropletResponse result) {
      return result.droplet;
   }

   public static class DropletResponse extends BaseResponse {
      private final Droplet droplet;

      @ConstructorProperties({ "status", "error_message", "message", "droplet" })
      public DropletResponse(Status status, String message, String details, Droplet droplet) {
         super(status, message, details);
         this.droplet = droplet;
      }
   }

}
