/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.datastore.core;

import java.util.List;
import streamflow.model.ResourceEntry;

public interface ResourceEntryDao extends GenericDao<ResourceEntry, String> {
    
    boolean exists(String id, String userId);
    
    List<ResourceEntry> findAllWithResource(String resource, String userId);
    
    ResourceEntry findById(String id, String userId); 

    ResourceEntry findByResourceAndName(String resource, String resourceEntryName, String userId);
    
    ResourceEntry save(ResourceEntry resourceEntry, String userId);
    
    ResourceEntry update(ResourceEntry resourceEntry, String userId);
    
    void delete(ResourceEntry resourceEntry, String userId);
    
    void deleteById(String id, String userId);
}
