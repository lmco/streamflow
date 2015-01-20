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
package streamflow.datastore.mongodb.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;

import streamflow.datastore.core.RoleDao;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.mongodb.impl.MongoRoleDao;
import streamflow.datastore.mongodb.impl.MongoUserDao;
import streamflow.model.config.DatastoreConfig;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoAuthstoreModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(MongoAuthstoreModule.class);

    @Override
    protected void configure() {
        LOG.info("Initializing [MongoDB Authstore] Module...");

        bind(RoleDao.class).to(MongoRoleDao.class);
        bind(UserDao.class).to(MongoUserDao.class);
    }
    
}
