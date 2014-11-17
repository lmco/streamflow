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
import streamflow.datastore.core.ComponentDao;
import streamflow.datastore.mongodb.MongoDao;
import streamflow.model.Component;

@Singleton
public class MongoComponentDao extends MongoDao<Component, String> implements ComponentDao {

    @Inject
    public MongoComponentDao(Datastore datastore) {
        super(datastore, Component.class);
    }

    @Override
    public List<Component> findAll() {
        return query().order("label").asList();
    }

    @Override
    public List<Component> findAllWithFramework(String framework) {
        return query().field("framework").equal(framework)
                .order("label").asList();
    }

    @Override
    public Component findByFrameworkAndName(String framework, String name) {
        return query().field("framework").equal(framework)
                .field("name").equal(name).get();
    }
}
