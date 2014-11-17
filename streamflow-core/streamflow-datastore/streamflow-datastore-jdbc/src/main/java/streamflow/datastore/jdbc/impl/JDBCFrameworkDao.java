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
import streamflow.datastore.core.FrameworkDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.FrameworkEntity;
import streamflow.model.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCFrameworkDao extends JDBCDao<Framework, String, FrameworkEntity> implements FrameworkDao {
    
    private final static Logger LOG = LoggerFactory.getLogger(JDBCFrameworkDao.class);

    @Inject
    public JDBCFrameworkDao(EntityManager entityManager) {
        super(entityManager, Framework.class, FrameworkEntity.class);
    }
    
    @Override
    public List<Framework> findAll() {
        List<Framework> frameworks = new ArrayList<Framework>();
        
        try {
            TypedQuery<FrameworkEntity> query = entityManager.createNamedQuery(
                    FrameworkEntity.FIND_ALL, FrameworkEntity.class);
            
            for (FrameworkEntity result : query.getResultList()) {
                frameworks.add(toObject(result));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while listing the entities: ", ex);
        }
        
        return frameworks;
    }
    
    @Override
    protected Framework toObject(FrameworkEntity entity) {
        Framework framework = null;
        try {
            if (entity != null) {
                framework = mapper.readValue(entity.getEntity(), Framework.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the framework entity to a framework", ex);
        }
        return framework;
    }
    
    @Override
    protected FrameworkEntity toEntity(Framework framework) {
        FrameworkEntity entity = null;
        try {
            if (framework != null) {
                entity = new FrameworkEntity();
                entity.setId(framework.getId());
                entity.setName(framework.getName());
                entity.setLabel(framework.getLabel());
                entity.setEntity(mapper.writeValueAsString(framework));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the framework to a framework entity", ex);
        }
        return entity;
    }
}
