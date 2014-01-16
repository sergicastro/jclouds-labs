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
package org.jclouds.digitalocean.json;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Custom class to serialize and deserialize SSH public keys.
 * 
 * @author Sergi Castro
 * @author Ignasi Barrera
 */
@Singleton
public class SshPublicKeyAdapter extends TypeAdapter<PublicKey> {
   private final Crypto crypto;
   private final Function<PublicKey, String> publicKeyWriter;

   @Inject
   SshPublicKeyAdapter(Crypto crypto, Function<PublicKey, String> publicKeyWriter) {
      this.crypto = checkNotNull(crypto, "crypto cannot be null");
      this.publicKeyWriter = checkNotNull(publicKeyWriter, "publicKeyWriter cannot be null");
   }

   private static class DecoderStatus {
      byte[] bytes = null;
      int pos = 0;
   }

   @Singleton
   public static class SshPublicKeyWriter implements Function<PublicKey, String> {
      @Override
      public String apply(PublicKey publicKey) {
         try {
            String algorithm = publicKey.getAlgorithm();
            if (algorithm.equals("RSA")) {
               RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
               ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
               DataOutputStream dos = new DataOutputStream(byteOs);

               dos.writeInt("ssh-rsa".getBytes().length);
               dos.write("ssh-rsa".getBytes());
               dos.writeInt(rsaPublicKey.getPublicExponent().toByteArray().length);
               dos.write(rsaPublicKey.getPublicExponent().toByteArray());
               dos.writeInt(rsaPublicKey.getModulus().toByteArray().length);
               dos.write(rsaPublicKey.getModulus().toByteArray());

               String publicKeyEncoded = new String(base64().encode(byteOs.toByteArray()));
               return "ssh-rsa " + publicKeyEncoded + " user@host";
            } else if (algorithm.equals("DSA")) {
               DSAPublicKey dsaPublicKey = (DSAPublicKey) publicKey;
               DSAParams dsaParams = dsaPublicKey.getParams();
               ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
               DataOutputStream dos = new DataOutputStream(byteOs);

               dos.writeInt("ssh-dss".getBytes().length);
               dos.write("ssh-dss".getBytes());
               dos.writeInt(dsaParams.getP().toByteArray().length);
               dos.write(dsaParams.getP().toByteArray());
               dos.writeInt(dsaParams.getQ().toByteArray().length);
               dos.write(dsaParams.getQ().toByteArray());
               dos.writeInt(dsaParams.getG().toByteArray().length);
               dos.write(dsaParams.getG().toByteArray());
               dos.writeInt(dsaPublicKey.getY().toByteArray().length);
               dos.write(dsaPublicKey.getY().toByteArray());

               String publicKeyEncoded = new String(base64().encode(byteOs.toByteArray()));
               return "ssh-dss " + publicKeyEncoded + " user@host";
            } else {
               throw new IllegalArgumentException("Unknown public key encoding: " + algorithm);
            }
         } catch (IOException ex) {
            throw Throwables.propagate(ex);
         }
      }
   }

   @Override
   public void write(JsonWriter writer, PublicKey publicKey) throws IOException {
      writer.value(publicKeyWriter.apply(publicKey));
   }

   @Override
   public PublicKey read(JsonReader reader) throws IOException {
      DecoderStatus status = new DecoderStatus();
      // look for the Base64 encoded part of the line to decode
      // both ssh-rsa and ssh-dss begin with "AAAA" due to the length bytes
      for (String part : Splitter.on(' ').split(reader.nextString())) {
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
