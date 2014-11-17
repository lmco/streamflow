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
import streamflow.datastore.core.ResourceDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.ResourceEntity;
import streamflow.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCResourceDao extends JDBCDao<Resource, String, ResourceEntity> 
        implements ResourceDao {

    private final static Logger LOG = LoggerFactory.getLogger(JDBCResourceDao.class);

    @Inject
    public JDBCResourceDao(EntityManager entityManager) {
        super(entityManager, Resource.class, ResourceEntity.class);
    }

    @Override
    public List<Resource> findAll() {
        List<Resource> resources = new ArrayList<Resource>();
        
        try {
            TypedQuery<ResourceEntity> query = entityManager.createNamedQuery(
                    ResourceEntity.FIND_ALL, ResourceEntity.class);
            
            for (ResourceEntity result : query.getResultList()) {
                resources.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return resources;
    }

    @Override
    public List<Resource> findAllWithFramework(String framework) {
        List<Resource> resources = new ArrayList<Resource>();
        
        try {
            TypedQuery<ResourceEntity> query = entityManager.createNamedQuery(
                    ResourceEntity.FIND_ALL_WITH_FRAMEWORK, ResourceEntity.class);
            query.setParameter("framework", framework);
            
            for (ResourceEntity result : query.getResultList()) {
                resources.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return resources;
    }

    @Override
    public Resource findByFrameworkAndName(String framework, String name) {
        Resource resource = null;
        
        try {
            TypedQuery<ResourceEntity> query = entityManager.createNamedQuery(
                    ResourceEntity.FIND_BY_FRAMEWORK_AND_NAME, ResourceEntity.class);
            query.setParameter("framework", framework);
            query.setParameter("name", name);
            
            resource = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return resource;
    }
    
    @Override
    protected Resource toObject(ResourceEntity entity) {
        Resource resource = null;
        try {
            if (entity != null) {
                resource = mapper.readValue(entity.getEntity(), Resource.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return resource;
    }

    @Override
    protected ResourceEntity toEntity(Resource resource) {
        ResourceEntity entity = null;
        try {
            if (resource != null) {
                entity = new ResourceEntity();
                entity.setId(resource.getId());
                entity.setName(resource.getName());
                entity.setLabel(resource.getLabel());
                entity.setFramework(resource.getFramework());
                entity.setEntity(mapper.writeValueAsString(resource));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
