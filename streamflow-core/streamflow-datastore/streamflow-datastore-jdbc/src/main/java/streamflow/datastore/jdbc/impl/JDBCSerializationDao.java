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
import streamflow.datastore.core.SerializationDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.SerializationEntity;
import streamflow.model.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCSerializationDao extends JDBCDao<Serialization, String, SerializationEntity>
        implements SerializationDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCSerializationDao.class);

    @Inject
    public JDBCSerializationDao(EntityManager entityManager) {
        super(entityManager, Serialization.class, SerializationEntity.class);
    }
    
    @Override
    public List<Serialization> findAll() {
        List<Serialization> serializations = new ArrayList<Serialization>();
        
        try {
            TypedQuery<SerializationEntity> query = entityManager.createNamedQuery(
                    SerializationEntity.FIND_ALL, SerializationEntity.class);
            
            for (SerializationEntity serialization : query.getResultList()) {
                serializations.add(toObject(serialization));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return serializations;
    }

    @Override
    public List<Serialization> findAllWithFramework(String framework) {
        List<Serialization> serializations = new ArrayList<Serialization>();
        
        try {
            TypedQuery<SerializationEntity> query = entityManager.createNamedQuery(
                    SerializationEntity.FIND_ALL_WITH_FRAMEWORK, SerializationEntity.class);
            query.setParameter("framework", framework);
            
            for (SerializationEntity result : query.getResultList()) {
                serializations.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return serializations;
    }

    @Override
    public Serialization findByTypeClass(String typeClass) {
        Serialization serialization = null;
        
        try {
            TypedQuery<SerializationEntity> query = entityManager.createNamedQuery(
                    SerializationEntity.FIND_BY_TYPE_CLASS, SerializationEntity.class);
            query.setParameter("typeClass", typeClass);
            
            serialization = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return serialization;
    }
    
    @Override
    protected Serialization toObject(SerializationEntity entity) {
        Serialization serialization = null;
        try {
            if (entity != null) {
                serialization = mapper.readValue(entity.getEntity(), Serialization.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return serialization;
    }

    @Override
    protected SerializationEntity toEntity(Serialization serialization) {
        SerializationEntity entity = null;
        try {
            if (serialization != null) {
                entity = new SerializationEntity();
                entity.setId(serialization.getId());
                entity.setFramework(serialization.getFramework());
                entity.setTypeClass(serialization.getTypeClass());
                entity.setEntity(mapper.writeValueAsString(serialization));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
