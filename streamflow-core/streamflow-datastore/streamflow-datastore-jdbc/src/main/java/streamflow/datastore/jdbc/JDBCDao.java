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
package streamflow.datastore.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import streamflow.datastore.core.GenericDao;
import streamflow.model.util.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JDBCDao <T extends Entity<ID>, ID extends Serializable, V>
        implements GenericDao<T, ID> {
    
    private static final Logger LOG = LoggerFactory.getLogger(JDBCDao.class);
    
    protected final EntityManager entityManager;
    
    protected final Class<T> persistentClass;
    
    protected final Class<V> entityClass;

    protected final ObjectMapper mapper = new ObjectMapper();

    public JDBCDao(EntityManager entityManager, Class<T> persistentClass, Class<V> entityClass) {
        this.entityManager = entityManager;
        this.persistentClass = persistentClass;
        this.entityClass = entityClass;
    }
    
    protected abstract T toObject(V entity);
    
    protected abstract V toEntity(T entity);
    
    @Override
    public List<T> findAll() {
        List<T> results = new ArrayList<T>();
        
        try {
            CriteriaQuery<V> criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
            criteria.select(criteria.from(entityClass));

            TypedQuery<V> query = entityManager.createQuery(criteria);
            for (V result : query.getResultList()) {
                results.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return results;
    }

    @Override
    public boolean exists(ID id) {
        return findEntity(id) != null;
    }

    @Override
    public T findById(ID id) {
        T result = null;
        try {
            result = toObject(findEntity(id));
        } catch (Exception ex) {
            //LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        return result;
    }

    @Override
    public T save(T object) {
        try {
            if (object != null) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.persist(toEntity(object));
                transaction.commit();
            }
            
            return object;
        } catch (Exception ex) {
            LOG.error("Exception occurred while saving the entity: ", ex);
            
            return null;
        }
    }

    @Override
    public T update(T object) {
        try {
            if (object != null) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.merge(toEntity(object));
                transaction.commit();
            }
            return object;
        } catch (Exception ex) {
            LOG.error("Exception occurred while updating the entity: ", ex);
            
            return null;
        }
    }

    @Override
    public void delete(T object) {
        try {
            V entity = findEntity(object.getId());
            if (entity != null) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(entity);
                transaction.commit();
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while deleting the entity: ", ex);
        }
    }

    @Override
    public void deleteById(ID id) {
        try {
            V entity = findEntity(id);
            if (entity != null) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(entity);
                transaction.commit();
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while deleting the entity: ", ex);
        }
    }
    
    public V findEntity(ID id) {
        V entity = null;
        try {
            entity = entityManager.find(entityClass, id);
        } catch (Exception ex) {
            LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        return entity;
    }
}
