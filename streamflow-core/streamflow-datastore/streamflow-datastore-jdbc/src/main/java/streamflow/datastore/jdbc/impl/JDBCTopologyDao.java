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
package streamflow.datastore.jdbc.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import streamflow.datastore.core.TopologyDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.TopologyEntity;
import streamflow.model.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCTopologyDao extends JDBCDao<Topology, String, TopologyEntity>
        implements TopologyDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCTopologyDao.class);

    @Inject
    public JDBCTopologyDao(EntityManager entityManager) {
        super(entityManager, Topology.class, TopologyEntity.class);
    }

    @Override
    public List<Topology> findAll() {
        List<Topology> topologies = new ArrayList<Topology>();
        
        try {
            TypedQuery<TopologyEntity> query = entityManager.createNamedQuery(
                    TopologyEntity.FIND_ALL, TopologyEntity.class);
            
            for (TopologyEntity topology : query.getResultList()) {
                topologies.add(toObject(topology));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return topologies;
    }

    @Override
    public List<Topology> findAll(String userId) {
        List<Topology> topologies = new ArrayList<Topology>();
        
        try {
            TypedQuery<TopologyEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_ALL_WITH_USER, TopologyEntity.class);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_ALL_WITH_ANON, TopologyEntity.class);
            }
            
            for (TopologyEntity topology : query.getResultList()) {
                topologies.add(toObject(topology));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return topologies;
    }

    @Override
    public Topology findByName(String name, String userId) {
        Topology topology = null;
        
        try {
            TypedQuery<TopologyEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_BY_NAME_WITH_USER, TopologyEntity.class);
                query.setParameter("name", name);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_BY_NAME_WITH_ANON, TopologyEntity.class);
                query.setParameter("name", name);
            }
            
            topology = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return topology;
    }

    @Override
    public boolean exists(String id, String userId) {
        return findById(id, userId) != null;
    }

    @Override
    public Topology findById(String id, String userId) {
        Topology topology = null;
        
        try {
            TypedQuery<TopologyEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_BY_ID_WITH_USER, TopologyEntity.class);
                query.setParameter("id", id);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                        TopologyEntity.FIND_BY_ID_WITH_ANON, TopologyEntity.class);
                query.setParameter("id", id);
            }
            
            topology = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return topology;
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
        deleteById(id);
    }
    
    @Override
    protected Topology toObject(TopologyEntity entity) {
        Topology topology = null;
        try {
            if (entity != null) {
                topology = mapper.readValue(entity.getEntity(), Topology.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return topology;
    }

    @Override
    protected TopologyEntity toEntity(Topology topology) {
        TopologyEntity entity = null;
        try {
            if (topology != null) {
                entity = new TopologyEntity();
                entity.setId(topology.getId());
                entity.setName(topology.getName());
                entity.setUserId(topology.getUserId());
                entity.setEntity(mapper.writeValueAsString(topology));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
