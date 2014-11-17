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
package streamflow.datastore.mongodb.impl;

import org.mongodb.morphia.Datastore;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.mongodb.MongoDao;
import streamflow.model.User;

@Singleton
public class MongoUserDao extends MongoDao<User, String>
        implements UserDao {

    @Inject
    public MongoUserDao(Datastore datastore) {
        super(datastore, User.class);
    }

    @Override
    public List<User> findAll() {
        return query().order("lastName, firstName").asList();
    }

    @Override
    public User findByUsername(String username) {
        return query().field("username").equal(username).get();
    }

    @Override
    public User findByEmail(String email) {
        return query().field("email").equal(email).get();
    }
}
