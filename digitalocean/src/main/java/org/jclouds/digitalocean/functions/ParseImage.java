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
import org.jclouds.digitalocean.domain.Image;
import org.jclouds.digitalocean.functions.ParseImage.ImageResponse;
import org.jclouds.http.functions.ParseJson;

/**
 * Parses a list of {@link Image} objects.
 * 
 * @author Sergi Castro
 */
@Singleton
public class ParseImage extends BaseResponseParser<ImageResponse, Image> {

   @Inject
   public ParseImage(ParseJson<ImageResponse> parser) {
      super(parser);
   }

   @Override
   protected Image getReturnValue(ImageResponse result) {
      return result.image;
   }

   public static class ImageResponse extends BaseResponse {
      private Image image;

      @ConstructorProperties({ "status", "error_message", "message", "image" })
      public ImageResponse(Status status, String message, String details, Image image) {
         super(status, message, details);
         this.image = image;
      }
   }

}
