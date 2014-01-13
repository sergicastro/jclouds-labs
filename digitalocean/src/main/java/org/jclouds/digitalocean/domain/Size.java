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

import java.beans.ConstructorProperties;

/**
 * A Size
 * 
 * @author Sergi Castro
 * 
 */
public class Size {
   private int id;

   private String name;

   private String slug;

   private int memory;

   private int cpu;

   private int disk;

   private String costPerHour;

   private String costPerMonth;

   @ConstructorProperties({ "id", "name", "slug", "memory", "cpu", "disk", "cost_per_hour", "cost_per_month" })
   public Size(final int id, final String name, final String slug, final int memory, final int cpu, final int disk,
         final String costPerHour, final String costPerMonth) {
      this.id = id;
      this.name = name;
      this.slug = slug;
      this.memory = memory;
      this.cpu = cpu;
      this.disk = disk;
      this.costPerHour = costPerHour;
      this.costPerMonth = costPerMonth;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getSlug() {
      return slug;
   }

   public int getMemory() {
      return memory;
   }

   public int getCpu() {
      return cpu;
   }

   public int getDisk() {
      return disk;
   }

   public String getCostPerHour() {
      return costPerHour;
   }

   public String getCostPerMonth() {
      return costPerMonth;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (costPerHour == null ? 0 : costPerHour.hashCode());
      result = prime * result + (costPerMonth == null ? 0 : costPerMonth.hashCode());
      result = prime * result + cpu;
      result = prime * result + disk;
      result = prime * result + id;
      result = prime * result + memory;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (slug == null ? 0 : slug.hashCode());
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
      Size other = (Size) obj;
      if (costPerHour == null) {
         if (other.costPerHour != null) {
            return false;
         }
      } else if (!costPerHour.equals(other.costPerHour)) {
         return false;
      }
      if (costPerMonth == null) {
         if (other.costPerMonth != null) {
            return false;
         }
      } else if (!costPerMonth.equals(other.costPerMonth)) {
         return false;
      }
      if (cpu != other.cpu) {
         return false;
      }
      if (disk != other.disk) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (memory != other.memory) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (slug == null) {
         if (other.slug != null) {
            return false;
         }
      } else if (!slug.equals(other.slug)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Size [id=" + id + ", name=" + name + ", slug=" + slug + ", memory=" + memory + ", cpu=" + cpu + ", disk="
            + disk + ", costPerHour=" + costPerHour + ", costPerMonth=" + costPerMonth + "]";
   }

}
