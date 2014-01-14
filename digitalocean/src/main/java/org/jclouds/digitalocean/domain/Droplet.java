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
import java.text.ParseException;
import java.util.Date;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

/**
 * A droplet.
 * 
 * @author Sergi Castro
 */
public class Droplet {

   private final int id;
   private final String name;
   @Named("image_id")
   private final int imageId;
   @Named("size_id")
   private final int sizeId;
   @Named("region_id")
   private final int regionId;
   @Named("backups_active")
   private final boolean backupsActive;
   @Named("ip_address")
   private final String ip;
   @Named("private_ip_address")
   private final String privateIp;
   private final boolean locked;
   private final String status; // TODO: Change to enum?
   @Named("created_at")
   private final Date created;

   @ConstructorProperties({ "id", "name", "image_id", "size_id", "region_id", "backups_active", "ip_address",
         "private_ip_address", "locked", "status", "created_at" })
   public Droplet(int id, String name, int imageId, int sizeId, int regionId, boolean backupsActive, String ip,
         @Nullable String privateIp, boolean locked, String status, Date created) throws ParseException {
      this.id = id;
      this.name = checkNotNull(name, "name cannot be null");
      this.imageId = imageId;
      this.sizeId = sizeId;
      this.regionId = regionId;
      this.backupsActive = backupsActive;
      this.ip = checkNotNull(ip, "ip cannot be null");
      this.privateIp = privateIp;
      this.locked = locked;
      this.status = checkNotNull(status, "status cannot be null");
      this.created = checkNotNull(created, "created cannot be null");
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getImageId() {
      return imageId;
   }

   public int getSizeId() {
      return sizeId;
   }

   public int getRegionId() {
      return regionId;
   }

   public boolean isBackupsActive() {
      return backupsActive;
   }

   public String getIp() {
      return ip;
   }

   public String getPrivateIp() {
      return privateIp;
   }

   public boolean isLocked() {
      return locked;
   }

   public String getStatus() {
      return status;
   }

   public Date getCreated() {
      return created;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = prime * result + (backupsActive ? 1231 : 1237);
      result = prime * result + (created == null ? 0 : created.hashCode());
      result = prime * result + id;
      result = prime * result + imageId;
      result = prime * result + (ip == null ? 0 : ip.hashCode());
      result = prime * result + (locked ? 1231 : 1237);
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (privateIp == null ? 0 : privateIp.hashCode());
      result = prime * result + regionId;
      result = prime * result + sizeId;
      result = prime * result + (status == null ? 0 : status.hashCode());
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
      Droplet other = (Droplet) obj;
      if (backupsActive != other.backupsActive) {
         return false;
      }
      if (created == null) {
         if (other.created != null) {
            return false;
         }
      } else if (!created.equals(other.created)) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (imageId != other.imageId) {
         return false;
      }
      if (ip == null) {
         if (other.ip != null) {
            return false;
         }
      } else if (!ip.equals(other.ip)) {
         return false;
      }
      if (locked != other.locked) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (privateIp == null) {
         if (other.privateIp != null) {
            return false;
         }
      } else if (!privateIp.equals(other.privateIp)) {
         return false;
      }
      if (regionId != other.regionId) {
         return false;
      }
      if (sizeId != other.sizeId) {
         return false;
      }
      if (status == null) {
         if (other.status != null) {
            return false;
         }
      } else if (!status.equals(other.status)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Droplet [id=" + id + ", name=" + name + ", imageId=" + imageId + ", sizeId=" + sizeId + ", regionId="
            + regionId + ", backupsActive=" + backupsActive + ", ip=" + ip + ", privateIp=" + privateIp + ", locked="
            + locked + ", status=" + status + ", created=" + created + "]";
   }

}
