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
import streamflow.datastore.core.KafkaDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.KafkaClusterEntity;
import streamflow.model.kafka.KafkaCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCKafkaDao extends JDBCDao<KafkaCluster, String, KafkaClusterEntity> 
        implements KafkaDao {

    private final static Logger LOG = LoggerFactory.getLogger(JDBCKafkaDao.class);

    @Inject
    public JDBCKafkaDao(EntityManager entityManager) {
        super(entityManager, KafkaCluster.class, KafkaClusterEntity.class);
    }
    
    @Override
    public List<KafkaCluster> findAll() {
        List<KafkaCluster> clusters = new ArrayList<KafkaCluster>();
        
        try {
            TypedQuery<KafkaClusterEntity> query = entityManager.createNamedQuery(
                    KafkaClusterEntity.FIND_ALL, KafkaClusterEntity.class);
            
            for (KafkaClusterEntity result : query.getResultList()) {
                clusters.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return clusters;
    }
    
    @Override
    public KafkaCluster findByName(String name) {
        KafkaCluster cluster = null;
        
        try {
            TypedQuery<KafkaClusterEntity> query = entityManager.createNamedQuery(
                    KafkaClusterEntity.FIND_BY_NAME, KafkaClusterEntity.class);
            query.setParameter("name", name);
            
            cluster = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return cluster;
    }

    @Override
    protected KafkaCluster toObject(KafkaClusterEntity entity) {
        KafkaCluster cluster = null;
        try {
            if (entity != null) {
                cluster = mapper.readValue(entity.getEntity(), KafkaCluster.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return cluster;
    }

    @Override
    protected KafkaClusterEntity toEntity(KafkaCluster cluster) {
        KafkaClusterEntity entity = null;
        try {
            if (cluster != null) {
                entity = new KafkaClusterEntity();
                entity.setId(cluster.getId());
                entity.setName(cluster.getName());
                entity.setEntity(mapper.writeValueAsString(cluster));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return entity;
    }
}
