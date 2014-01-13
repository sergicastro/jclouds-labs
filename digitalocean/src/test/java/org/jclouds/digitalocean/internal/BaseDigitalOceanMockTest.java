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
package org.jclouds.digitalocean.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.digitalocean.http.filters.AuthenticationFilter.CREDENTIAL_PARAM;
import static org.jclouds.digitalocean.http.filters.AuthenticationFilter.IDENTITY_PARAM;
import static org.jclouds.util.Strings2.toStringAndClose;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import com.squareup.okhttp.mockwebserver.MockWebServer;

/**
 * Base class for all DigitalOcean mock tests.
 * 
 * @author Sergi Castro
 * 
 */
public class BaseDigitalOceanMockTest<A extends Closeable> {
   private final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   protected String provider;

   public BaseDigitalOceanMockTest() {
      provider = "digitalocean";
   }

   @SuppressWarnings("serial")
   public A api(URL url) {
      return ContextBuilder.newBuilder(provider) //
            .credentials("clientid", "apikey") //
            .endpoint(url.toString()) //
            .modules(modules) //
            .buildApi(new TypeToken<A>(getClass()) {
            });
   }

   public static MockWebServer mockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      return server;
   }

   public byte[] payloadFromResource(String resource) {
      try {
         return toStringAndClose(getClass().getResourceAsStream(resource)).getBytes(Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   protected static String urlWithCredentials(String path) {
      return path + "?" + IDENTITY_PARAM + "=clientid&" + CREDENTIAL_PARAM + "=apikey";
   }
}
