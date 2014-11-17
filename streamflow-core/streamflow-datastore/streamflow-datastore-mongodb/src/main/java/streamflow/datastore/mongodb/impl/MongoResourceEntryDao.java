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
package streamflow.datastore.mongodb.impl;

import org.mongodb.morphia.Datastore;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import streamflow.datastore.core.ResourceEntryDao;
import streamflow.datastore.mongodb.MongoDao;
import streamflow.model.ResourceEntry;

@Singleton
public class MongoResourceEntryDao extends MongoDao<ResourceEntry, String>
        implements ResourceEntryDao {

    @Inject
    public MongoResourceEntryDao(Datastore datastore) {
        super(datastore, ResourceEntry.class);
    }
    
    @Override
    public ResourceEntry save(ResourceEntry resourceEntry, String userId) {
        resourceEntry.setUserId(userId);
        return save(resourceEntry);
    }
    
    @Override
    public ResourceEntry update(ResourceEntry resourceEntry, String userId) {
        resourceEntry.setUserId(userId);
        return update(resourceEntry);
    }
    
    @Override
    public boolean exists(String id, String userId) {
        return findById(id, userId) != null;
    }

    @Override
    public List<ResourceEntry> findAllWithResource(String resource, String userId) {
        return query().field("resource").equal(resource).field("userId").equal(userId).order("name").asList();
    }
    
    @Override
    public ResourceEntry findById(String id, String userId) {
        return query().field("_id").equal(id).field("userId").equal(userId).get();
    }

    @Override
    public ResourceEntry findByResourceAndName(String resource, String resourceEntryName, String userId) {
        return query().field("resource").equal(resource).field("name").equal(resourceEntryName)
                .field("userId").equal(userId).get();
    }
    
    @Override
    public void delete(ResourceEntry resourceEntry, String userId) {
        delete(resourceEntry);
    }
    
    @Override
    public void deleteById(String id, String userId) {
        ResourceEntry resourceEntry = findById(id, userId);
        delete(resourceEntry);
    }
}
