/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package streamflow.datastore.jdbc.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import streamflow.datastore.core.ComponentDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.ComponentEntity;
import streamflow.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCComponentDao extends JDBCDao<Component, String, ComponentEntity> 
        implements ComponentDao {

    private final static Logger LOG = LoggerFactory.getLogger(JDBCComponentDao.class);

    @Inject
    public JDBCComponentDao(EntityManager entityManager) {
        super(entityManager, Component.class, ComponentEntity.class);
    }

    @Override
    public List<Component> findAll() {
        List<Component> components = new ArrayList<Component>();
        
        try {
            TypedQuery<ComponentEntity> query = entityManager.createNamedQuery(
                    ComponentEntity.FIND_ALL, ComponentEntity.class);
            
            for (ComponentEntity component : query.getResultList()) {
                components.add(toObject(component));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return components;
    }

    @Override
    public List<Component> findAllWithFramework(String framework) {
        List<Component> components = new ArrayList<Component>();
        
        try {
            TypedQuery<ComponentEntity> query = entityManager.createNamedQuery(
                    ComponentEntity.FIND_ALL_WITH_FRAMEWORK, ComponentEntity.class);
            query.setParameter("framework", framework);
            
            for (ComponentEntity result : query.getResultList()) {
                components.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return components;
    }

    @Override
    public Component findByFrameworkAndName(String framework, String name) {
        Component component = null;
        
        try {
            TypedQuery<ComponentEntity> query = entityManager.createNamedQuery(
                    ComponentEntity.FIND_BY_FRAMEWORK_AND_NAME, ComponentEntity.class);
            query.setParameter("framework", framework);
            query.setParameter("name", name);
            
            component = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return component;
    }
    
    @Override
    protected Component toObject(ComponentEntity entity) {
        Component component = null;
        try {
            if (entity != null) {
                component = mapper.readValue(entity.getEntity(), Component.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return component;
    }

    @Override
    protected ComponentEntity toEntity(Component component) {
        ComponentEntity entity = null;
        try {
            if (component != null) {
                entity = new ComponentEntity();
                entity.setId(component.getId());
                entity.setName(component.getName());
                entity.setLabel(component.getLabel());
                entity.setFramework(component.getFramework());
                entity.setType(component.getType());
                entity.setEntity(mapper.writeValueAsString(component));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
