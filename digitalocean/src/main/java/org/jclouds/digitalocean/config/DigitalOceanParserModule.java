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
package org.jclouds.digitalocean.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.inject.Scopes.SINGLETON;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;

/**
 * Custom parser bindings.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
public class DigitalOceanParserModule extends AbstractModule {

   @ImplementedBy(PublicKeyAdapterImpl.class)
   public interface PublicKeyAdapter extends JsonDeserializer<PublicKey> {

   }

   @Singleton
   public static class PublicKeyAdapterImpl implements PublicKeyAdapter {
      private final Crypto crypto;

      @Inject
      PublicKeyAdapterImpl(Crypto crypto) {
         this.crypto = checkNotNull(crypto, "crypto cannot be null");
      }

      private static class DecoderStatus {
         byte[] bytes = null;
         int pos = 0;
      }

      @Override
      public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
         DecoderStatus status = new DecoderStatus();

         // look for the Base64 encoded part of the line to decode
         // both ssh-rsa and ssh-dss begin with "AAAA" due to the length bytes
         for (String part : Splitter.on(' ').split(json.getAsString())) {
            if (part.startsWith("AAAA")) {
               status.bytes = base64().decode(part);
               break;
            }
         }

         checkArgument(status.bytes != null, "No Base64 part to decode");

         String type = decodeType(status);

         try {
            if (type.equals("ssh-rsa")) {
               BigInteger e = decodeBigInt(status);
               BigInteger m = decodeBigInt(status);
               RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
               return crypto.rsaKeyFactory().generatePublic(spec);
            } else if (type.equals("ssh-dss")) {
               BigInteger p = decodeBigInt(status);
               BigInteger q = decodeBigInt(status);
               BigInteger g = decodeBigInt(status);
               BigInteger y = decodeBigInt(status);
               DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
               return KeyFactory.getInstance("DSA").generatePublic(spec);
            } else {
               throw new IllegalArgumentException("Unknown type: " + type);
            }
         } catch (InvalidKeySpecException e) {
            throw Throwables.propagate(e);
         } catch (NoSuchAlgorithmException e) {
            throw Throwables.propagate(e);
         }
      }

      private String decodeType(DecoderStatus status) {
         int len = decodeInt(status);
         String type = new String(status.bytes, status.pos, len);
         status.pos += len;
         return type;
      }

      private int decodeInt(DecoderStatus status) {
         return (status.bytes[status.pos++] & 0xFF) << 24 | (status.bytes[status.pos++] & 0xFF) << 16
               | (status.bytes[status.pos++] & 0xFF) << 8 | status.bytes[status.pos++] & 0xFF;
      }

      private BigInteger decodeBigInt(DecoderStatus status) {
         int len = decodeInt(status);
         byte[] bigIntBytes = new byte[len];
         System.arraycopy(status.bytes, status.pos, bigIntBytes, 0, len);
         status.pos += len;
         return new BigInteger(bigIntBytes);
      }
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class).in(SINGLETON);
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings(PublicKeyAdapter publicKeyAdapter) {
      return ImmutableMap.<Type, Object> of(PublicKey.class, publicKeyAdapter);
   }

}
