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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.digitalocean.domain.BaseResponse;
import org.jclouds.digitalocean.domain.BaseResponse.Status;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.base.Function;

/**
 * Base class for all response parsers.
 * <p>
 * The DigitalOcean api does always return a 200 status code in the responses,
 * and sets an "ERROR" string in the body when something failed.
 * <p>
 * Using a {@link ResponseParser} to propagate the exceptions is not the best
 * place (as the retry logic has already happened), but as long as the response
 * status is not properly populated, it is the best way to handle errors.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
public abstract class BaseResponseParser<T extends BaseResponse, V> implements Function<HttpResponse, V> {

   private final ParseJson<T> parser;

   BaseResponseParser(ParseJson<T> parser) {
      this.parser = checkNotNull(parser, "images cannot be null");
   }

   @Override
   public V apply(HttpResponse input) {
      V result = null;
      if (hasPayload(input)) {
         T content = parser.apply(input);
         if (content.getStatus() == Status.ERROR) {
            throw new HttpResponseException(content.getDetails(), null, input);
         }
         result = getReturnValue(content);
      }
      return result;
   }

   protected abstract V getReturnValue(T result);

   private static boolean hasPayload(final HttpResponse response) {
      return response.getPayload() != null && response.getPayload().getRawContent() != null;
   }

}
