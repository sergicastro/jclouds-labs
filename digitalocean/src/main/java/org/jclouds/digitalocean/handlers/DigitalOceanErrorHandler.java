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
package org.jclouds.digitalocean.handlers;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean.domain.BaseResponse;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Parse the errors in the response and propagate an appropriate exception.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class DigitalOceanErrorHandler implements HttpErrorHandler {

   private final ParseJson<BaseResponse> errorParser;

   @Inject
   DigitalOceanErrorHandler(ParseJson<BaseResponse> errorParser) {
      this.errorParser = checkNotNull(errorParser, "errorParser cannot be null");
   }

   @Override
   public void handleError(HttpCommand command, HttpResponse response) {
      // Try parsing the error in the response. If it fails, an
      // HttpResponseException will be already propagated
      Exception exception = new HttpResponseException(command, response);
      String message = exception.getMessage();

      if (hasPayload(response)) {
         try {
            BaseResponse error = errorParser.apply(response);
            exception = new HttpResponseException(command, response, error.toString());
            message = error.getDetails();
         } catch (HttpResponseException ex) {
            // If the body can not be parsed, just continue with the default
            // message
         }
      }

      try {
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(message, exception);
               break;
            case 404:
               exception = new ResourceNotFoundException(message, exception);
               break;
         }
      } finally {
         command.setException(exception);
      }
   }

   private static boolean hasPayload(final HttpResponse response) {
      return response.getPayload() != null && response.getPayload().getRawContent() != null;
   }
}
