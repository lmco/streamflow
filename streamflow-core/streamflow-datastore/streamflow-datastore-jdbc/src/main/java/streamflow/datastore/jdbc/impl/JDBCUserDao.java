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
import streamflow.datastore.core.UserDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.UserEntity;
import streamflow.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCUserDao extends JDBCDao<User, String, UserEntity> implements UserDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCUserDao.class);

    @Inject
    public JDBCUserDao(EntityManager entityManager) {
        super(entityManager, User.class, UserEntity.class);
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<User>();
        
        try {
            TypedQuery<UserEntity> query = entityManager.createNamedQuery(
                    UserEntity.FIND_ALL, UserEntity.class);
            
            for (UserEntity user : query.getResultList()) {
                users.add(toObject(user));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return users;
    }

    @Override
    public User findByUsername(String username) {
        User user = null;
        
        try {
            TypedQuery<UserEntity> query = entityManager.createNamedQuery(
                    UserEntity.FIND_BY_USERNAME, UserEntity.class);
            query.setParameter("username", username);
            
            user = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = null;
        
        try {
            TypedQuery<UserEntity> query = entityManager.createNamedQuery(
                    UserEntity.FIND_BY_EMAIL, UserEntity.class);
            query.setParameter("email", email);
            
            user = toObject(query.getSingleResult());
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return user;
    }
    
    @Override
    protected User toObject(UserEntity entity) {
        User user = null;
        try {
            if (entity != null) {
                user = mapper.readValue(entity.getEntity(), User.class);
                user.setPassword(entity.getPassword());
                user.setPasswordSalt(entity.getPasswordSalt());
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the entity to an object", ex);
        }
        return user;
    }

    @Override
    protected UserEntity toEntity(User user) {
        UserEntity entity = null;
        try {
            if (user != null) {
                entity = new UserEntity();
                entity.setId(user.getId());
                entity.setUsername(user.getUsername());
                entity.setEmail(user.getEmail());
                entity.setFirstName(user.getFirstName());
                entity.setLastName(user.getLastName());
                entity.setPassword(user.getPassword());
                entity.setPasswordSalt(user.getPasswordSalt());
                entity.setEntity(mapper.writeValueAsString(user));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the object to an entity", ex);
        }
        return entity;
    }
}
