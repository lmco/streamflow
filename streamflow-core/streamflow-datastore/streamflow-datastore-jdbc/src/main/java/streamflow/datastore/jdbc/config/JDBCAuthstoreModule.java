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

import streamflow.datastore.core.RoleDao;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.jdbc.impl.JDBCRoleDao;
import streamflow.datastore.jdbc.impl.JDBCUserDao;
import streamflow.model.config.DatastoreConfig;
import streamflow.util.environment.StreamflowEnvironment;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCAuthstoreModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(JDBCAuthstoreModule.class);

    @Override
    protected void configure() {
        LOG.info("Initializing JDBC Authstore...");

        bind(UserDao.class).to(JDBCUserDao.class);
        bind(RoleDao.class).to(JDBCRoleDao.class);
    }
}
