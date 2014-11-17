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
import streamflow.datastore.core.ResourceEntryDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.ResourceEntryEntity;
import streamflow.model.ResourceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCResourceEntryDao extends JDBCDao<ResourceEntry, String, ResourceEntryEntity>
        implements ResourceEntryDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCResourceEntryDao.class);

    @Inject
    public JDBCResourceEntryDao(EntityManager entityManager) {
        super(entityManager, ResourceEntry.class, ResourceEntryEntity.class);
    }

    @Override
    public List<ResourceEntry> findAllWithResource(String resource, String userId) {
        List<ResourceEntry> resourceEntries = new ArrayList<ResourceEntry>();
        
        try {
            TypedQuery<ResourceEntryEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                        ResourceEntryEntity.FIND_ALL_BY_RESOURCE_WITH_USER, ResourceEntryEntity.class);
                query.setParameter("resource", resource);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                        ResourceEntryEntity.FIND_ALL_BY_RESOURCE_WITH_ANON, ResourceEntryEntity.class);
                query.setParameter("resource", resource);
            }
            
            for (ResourceEntryEntity entity : query.getResultList()) {
                resourceEntries.add(toObject(entity));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return resourceEntries;
    }

    @Override
    public ResourceEntry findByResourceAndName(String resource, String resourceEntryName, String userId) {
        ResourceEntry resourceEntry = null;
        
        try {
            TypedQuery<ResourceEntryEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                    ResourceEntryEntity.FIND_BY_RESOURCE_AND_NAME_WITH_USER, ResourceEntryEntity.class);
                query.setParameter("resource", resource);
                query.setParameter("name", resourceEntryName);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                    ResourceEntryEntity.FIND_BY_RESOURCE_AND_NAME_WITH_ANON, ResourceEntryEntity.class);
                query.setParameter("resource", resource);
                query.setParameter("name", resourceEntryName);
            }
            
            resourceEntry = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return resourceEntry;
    }

    @Override
    public boolean exists(String id, String userId) {
        return findById(id, userId) != null;
    }

    @Override
    public ResourceEntry findById(String id, String userId) {
        ResourceEntry resourceEntry = null;
        
        try {
            TypedQuery<ResourceEntryEntity> query;
            if (userId != null) {
                query = entityManager.createNamedQuery(
                    ResourceEntryEntity.FIND_BY_ID_WITH_USER, ResourceEntryEntity.class);
                query.setParameter("id", id);
                query.setParameter("userId", userId);
            } else {
                query = entityManager.createNamedQuery(
                    ResourceEntryEntity.FIND_BY_ID_WITH_ANON, ResourceEntryEntity.class);
                query.setParameter("id", id);
            }
            
            resourceEntry = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return resourceEntry;
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
    public void delete(ResourceEntry resourceEntry, String userId) {
        delete(resourceEntry);
    }

    @Override
    public void deleteById(String id, String userId) {
        deleteById(id);
    }
    
    @Override
    protected ResourceEntry toObject(ResourceEntryEntity entity) {
        ResourceEntry resourceEntry = null;
        try {
            if (entity != null) {
                resourceEntry = mapper.readValue(entity.getEntity(), ResourceEntry.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return resourceEntry;
    }

    @Override
    protected ResourceEntryEntity toEntity(ResourceEntry resource) {
        ResourceEntryEntity entity = null;
        try {
            if (resource != null) {
                entity = new ResourceEntryEntity();
                entity.setId(resource.getId());
                entity.setName(resource.getName());
                entity.setResource(resource.getResource());
                entity.setUserId(resource.getUserId());
                entity.setEntity(mapper.writeValueAsString(resource));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
