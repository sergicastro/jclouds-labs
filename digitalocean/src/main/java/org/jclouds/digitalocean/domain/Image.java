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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

/**
 * An Image.
 * 
 * @author Sergi Castro
 * 
 */
public class Image {
   private Integer id;
   private String name;
   private String distribution;
   private boolean publicImage;

   @ConstructorProperties({ "id", "name", "distribution", "public" })
   public Image(Integer id, String name, String distribution, boolean publicImage) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.distribution = checkNotNull(distribution, "distribution");
      this.publicImage = publicImage;
   }

   public Integer getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDistribution() {
      return distribution;
   }

   public boolean isPublicImage() {
      return publicImage;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (distribution == null ? 0 : distribution.hashCode());
      result = prime * result + (id == null ? 0 : id.hashCode());
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (publicImage ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Image other = (Image) obj;
      if (distribution == null) {
         if (other.distribution != null) {
            return false;
         }
      } else if (!distribution.equals(other.distribution)) {
         return false;
      }
      if (id == null) {
         if (other.id != null) {
            return false;
         }
      } else if (!id.equals(other.id)) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (publicImage != other.publicImage) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Image [id=" + id + ", name=" + name + ", distribution=" + distribution + ", publicImage=" + publicImage
            + "]";
   }

}
