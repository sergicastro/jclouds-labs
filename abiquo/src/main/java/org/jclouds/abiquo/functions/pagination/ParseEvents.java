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
package org.jclouds.abiquo.functions.pagination;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.http.functions.ParseXMLWithJAXB;

import com.abiquo.server.core.event.EventDto;
import com.abiquo.server.core.event.EventsDto;

/**
 * Parses a paginated event list.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ParseEvents extends BasePaginationParser<EventDto, EventsDto> {
   @Inject
   public ParseEvents(AbiquoApi api, ParseXMLWithJAXB<EventsDto> parser) {
      super(api, parser);
   }

   @Singleton
   public static class ToPagedIterable extends PaginatedCollection.ToPagedIterable<EventDto, EventsDto> {
      @Inject
      public ToPagedIterable(AbiquoApi api, ParseXMLWithJAXB<EventsDto> parser) {
         super(api, parser);
      }
   }

}
