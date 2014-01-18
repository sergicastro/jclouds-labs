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
package org.jclouds.digitalocean.domain.enums;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperatingSystem {
   private final Distribution distribution;
   private final String version;
   private final String arch;

   private OperatingSystem(final String distribution, final String version, final String arch) {
      this.distribution = Distribution.fromValue(distribution);
      this.version = version;
      this.arch = arch;
   }

   public Distribution getDistribution() {
      return distribution;
   }

   public String getVersion() {
      return version;
   }

   public String getArch() {
      return arch;
   }

   // "Ubuntu 12.10 x64"
   private static final Pattern VERSION_PATTERN = compile("\\d+(\\.?\\d+)?");
   private static final Pattern ARCH_PATTERN = compile("x\\d{2}");

   public static OperatingSystem buildOperatingSystem(final String distribution, final String input) {
      return new OperatingSystem(distribution, match(VERSION_PATTERN, input), match(ARCH_PATTERN, input));
   }

   private static String match(final Pattern pattern, final String input) {
      Matcher m = pattern.matcher(input);
      return m.find() ? nullToEmpty(m.group(0)) : "";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (arch == null ? 0 : arch.hashCode());
      result = prime * result + (distribution == null ? 0 : distribution.hashCode());
      result = prime * result + (version == null ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      OperatingSystem other = (OperatingSystem) obj;
      if (arch == null) {
         if (other.arch != null) {
            return false;
         }
      } else if (!arch.equals(other.arch)) {
         return false;
      }
      if (distribution != other.distribution) {
         return false;
      }
      if (version == null) {
         if (other.version != null) {
            return false;
         }
      } else if (!version.equals(other.version)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "OperatingSystem [distribution=" + distribution + ", version=" + version + ", arch=" + arch + "]";
   }

}
