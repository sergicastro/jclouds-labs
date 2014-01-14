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
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.functions.ParseKey.KeyResponse;
import org.jclouds.http.functions.ParseJson;

import com.google.inject.name.Named;

/**
 * Parses a list of {@link SshKey} objects.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class ParseKey extends BaseResponseParser<KeyResponse, SshKey> {

   @Inject
   ParseKey(ParseJson<KeyResponse> parser) {
      super(parser);
   }

   @Override
   protected SshKey getReturnValue(KeyResponse result) {
      return result.key;
   }

   public static class KeyResponse extends BaseResponse {
      @Named("ssh_key")
      private final SshKey key;

      @ConstructorProperties({ "status", "error_message", "message", "ssh_key" })
      public KeyResponse(Status status, String message, String details, SshKey key) {
         super(status, message, details);
         this.key = key;
      }
   }

}
