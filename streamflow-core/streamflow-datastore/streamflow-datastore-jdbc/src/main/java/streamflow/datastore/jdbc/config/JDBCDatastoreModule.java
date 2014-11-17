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
package streamflow.datastore.jdbc.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import streamflow.datastore.core.ComponentDao;
import streamflow.datastore.core.FileContentDao;
import streamflow.datastore.core.FileInfoDao;
import streamflow.datastore.core.FrameworkDao;
import streamflow.datastore.core.KafkaDao;
import streamflow.datastore.core.ResourceDao;
import streamflow.datastore.core.ResourceEntryDao;
import streamflow.datastore.core.RoleDao;
import streamflow.datastore.core.SerializationDao;
import streamflow.datastore.core.TopologyDao;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.jdbc.impl.JDBCComponentDao;
import streamflow.datastore.jdbc.impl.JDBCDiskFileContentDao;
import streamflow.datastore.jdbc.impl.JDBCFileInfoDao;
import streamflow.datastore.jdbc.impl.JDBCFrameworkDao;
import streamflow.datastore.jdbc.impl.JDBCKafkaDao;
import streamflow.datastore.jdbc.impl.JDBCResourceDao;
import streamflow.datastore.jdbc.impl.JDBCResourceEntryDao;
import streamflow.datastore.jdbc.impl.JDBCRoleDao;
import streamflow.datastore.jdbc.impl.JDBCSerializationDao;
import streamflow.datastore.jdbc.impl.JDBCTopologyDao;
import streamflow.datastore.jdbc.impl.JDBCUserDao;
import streamflow.model.config.DatastoreConfig;
import streamflow.util.environment.StreamflowEnvironment;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCDatastoreModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(JDBCDatastoreModule.class);

    @Override
    protected void configure() {
        LOG.info("Initializing JDBC Datastore...");

        bind(ComponentDao.class).to(JDBCComponentDao.class);
        bind(FileInfoDao.class).to(JDBCFileInfoDao.class);
        bind(FrameworkDao.class).to(JDBCFrameworkDao.class);
        bind(ResourceEntryDao.class).to(JDBCResourceEntryDao.class);
        bind(ResourceDao.class).to(JDBCResourceDao.class);
        bind(RoleDao.class).to(JDBCRoleDao.class);
        bind(SerializationDao.class).to(JDBCSerializationDao.class);
        bind(TopologyDao.class).to(JDBCTopologyDao.class);
        bind(UserDao.class).to(JDBCUserDao.class);
        bind(FileContentDao.class).to(JDBCDiskFileContentDao.class);
        bind(KafkaDao.class).to(JDBCKafkaDao.class);
    }

    @Singleton
    @Provides
    public EntityManager provideEntityManager(DatastoreConfig datastoreConfig) {
        String url = datastoreConfig.getProperty("url", String.class);
        if (url == null) {
            url = "jdbc:h2:" + StreamflowEnvironment.getDataDir() + File.separator + "h2"
                    + File.separator + "streamflow";
        }

        String driver = datastoreConfig.getProperty("driver", String.class);
        if (driver == null) {
            driver = "org.h2.Driver";
        }

        String user = datastoreConfig.getProperty("user", String.class);
        if (user == null) {
            user = "streamflow";
        }

        String password = datastoreConfig.getProperty("password", String.class);
        if (password == null) {
            password = "streamflow";
        }

        // Manually define all properties that would normally appear inside persistence.xml
        Map properties = new HashMap();
        properties.put(JDBC_DRIVER, driver);
        properties.put(JDBC_URL, url);
        properties.put(JDBC_USER, user);
        properties.put(JDBC_PASSWORD, password);
        properties.put(DDL_GENERATION, "create-tables");
        properties.put(DDL_GENERATION_MODE, "database");
        properties.put(CLASSLOADER, this.getClass().getClassLoader());

        EntityManagerFactory entityManagerFactory = 
                new PersistenceProvider().createEntityManagerFactory("streamflow-pu", properties);
        
        return entityManagerFactory.createEntityManager();
    }
}
