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
import streamflow.datastore.core.RoleDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.RoleEntity;
import streamflow.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCRoleDao extends JDBCDao<Role, String, RoleEntity> implements RoleDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCRoleDao.class);

    @Inject
    public JDBCRoleDao(EntityManager entityManager) {
        super(entityManager, Role.class, RoleEntity.class);
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<Role>();
        
        try {
            TypedQuery<RoleEntity> query = entityManager.createNamedQuery(
                    RoleEntity.FIND_ALL, RoleEntity.class);
            
            for (RoleEntity role : query.getResultList()) {
                roles.add(toObject(role));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return roles;
    }

    @Override
    public Role findByName(String name) {
        Role role = null;
        
        try {
            TypedQuery<RoleEntity> query = entityManager.createNamedQuery(
                    RoleEntity.FIND_BY_NAME, RoleEntity.class);
            query.setParameter("name", name);
            
            role = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return role;
    }
    
    @Override
    protected Role toObject(RoleEntity entity) {
        Role role = null;
        try {
            if (entity != null) {
                role = mapper.readValue(entity.getEntity(), Role.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return role;
    }

    @Override
    protected RoleEntity toEntity(Role role) {
        RoleEntity entity = null;
        try {
            if (role != null) {
                entity = new RoleEntity();
                entity.setId(role.getId());
                entity.setName(role.getName());
                entity.setEntity(mapper.writeValueAsString(role));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
