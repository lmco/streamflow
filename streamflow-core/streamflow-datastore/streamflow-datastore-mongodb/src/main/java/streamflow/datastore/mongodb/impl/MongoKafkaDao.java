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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import streamflow.datastore.core.KafkaDao;
import streamflow.datastore.mongodb.MongoDao;
import streamflow.model.kafka.KafkaCluster;
import org.mongodb.morphia.Datastore;

@Singleton
public class MongoKafkaDao extends MongoDao<KafkaCluster, String> implements KafkaDao {

    @Inject
    public MongoKafkaDao(Datastore datastore) {
        super(datastore, KafkaCluster.class);
    }

    @Override
    public List<KafkaCluster> findAll() {
        return query().order("name").asList();
    }
    @Override
    public KafkaCluster findByName(String name) {
        return query().field("name").equal(name).get();
    }
}
