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
 */package org.jclouds.digitalocean.domain;

import java.beans.ConstructorProperties;
import java.text.ParseException;
import java.util.Date;

/**
 * A droplet
 * 
 * @author Sergi Castro
 * 
 */
public class Droplet {

   private int id;

   private String name;

   private int imageId;

   private int sizeId;

   private int regionId;

   private boolean backupsActive;

   private String ip_address;

   private String private_ip_address;

   private boolean locked;

   private String status;

   private Date created;

   @ConstructorProperties({ "id", "name", "image_id", "size_id", "region_id", "backups_active", "ip_address",
         "private_ip_address", "locked", "status", "created_at" })
   public Droplet(final int id, final String name, final int imageId, final int sizeId, final int regionId,
         final boolean backupsActive, final String ip_address, final String private_ip_address, final boolean locked,
         final String status, final String created) throws ParseException {
      this.id = id;
      this.name = name;
      this.imageId = imageId;
      this.sizeId = sizeId;
      this.regionId = regionId;
      this.backupsActive = backupsActive;
      this.ip_address = ip_address;
      this.private_ip_address = private_ip_address;
      this.locked = locked;
      this.status = status;
      // TODO parse 2014-01-13T20:53:08Z
      // this.created = new
      // SimpleDateFormat("yyyy-MM-ddhh:mm:ss").parse(created);
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

   public String getIp_address() {
      return ip_address;
   }

   public String getPrivate_ip_address() {
      return private_ip_address;
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
      final int prime = 31;
      int result = 1;
      result = prime * result + (backupsActive ? 1231 : 1237);
      result = prime * result + (created == null ? 0 : created.hashCode());
      result = prime * result + id;
      result = prime * result + imageId;
      result = prime * result + (ip_address == null ? 0 : ip_address.hashCode());
      result = prime * result + (locked ? 1231 : 1237);
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (private_ip_address == null ? 0 : private_ip_address.hashCode());
      result = prime * result + regionId;
      result = prime * result + sizeId;
      result = prime * result + (status == null ? 0 : status.hashCode());
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
      if (ip_address == null) {
         if (other.ip_address != null) {
            return false;
         }
      } else if (!ip_address.equals(other.ip_address)) {
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
      if (private_ip_address == null) {
         if (other.private_ip_address != null) {
            return false;
         }
      } else if (!private_ip_address.equals(other.private_ip_address)) {
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
            + regionId + ", backupsActive=" + backupsActive + ", ip_address=" + ip_address + ", private_ip_address="
            + private_ip_address + ", locked=" + locked + ", status=" + status + ", created=" + created + "]";
   }

}
