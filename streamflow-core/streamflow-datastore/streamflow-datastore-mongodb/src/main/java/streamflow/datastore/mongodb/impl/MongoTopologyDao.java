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
import streamflow.datastore.core.TopologyDao;
import streamflow.datastore.mongodb.MongoDao;
import streamflow.model.Topology;

@Singleton
public class MongoTopologyDao extends MongoDao<Topology, String>
        implements TopologyDao {

    @Inject
    public MongoTopologyDao(Datastore datastore) {
        super(datastore, Topology.class);
    }
    
    @Override
    public boolean exists(String id, String userId) {
        return findById(id, userId) != null;
    }
    
    @Override
    public List<Topology> findAll(String userId) {
        return query().field("userId").equal(userId).order("name").asList();
    }
    
    @Override
    public Topology findById(String id, String userId) {
        return query().field("_id").equal(id).field("userId").equal(userId).get();
    }
    
    @Override
    public Topology findByName(String name, String userId) {
        return query().field("name").equal(name).field("userId").equal(userId).get();
    }
    
    @Override
    public Topology save(Topology topology, String userId) {
        topology.setUserId(userId);
        return save(topology);
    }
    
    @Override
    public Topology update(Topology topology, String userId) {
        topology.setUserId(userId);
        return update(topology);
    }
    
    @Override
    public void delete(Topology topology, String userId) {
        delete(topology);
    }
    
    @Override
    public void deleteById(String id, String userId) {
        Topology topology = findById(id, userId);
        delete(topology);
    }
}
