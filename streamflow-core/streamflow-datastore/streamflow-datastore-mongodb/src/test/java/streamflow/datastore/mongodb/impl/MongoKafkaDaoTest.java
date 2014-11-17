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

import com.github.fakemongo.junit.FongoRule;
import java.util.List;
import streamflow.model.kafka.KafkaCluster;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoKafkaDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoKafkaDao kafkaDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        kafkaDao = new MongoKafkaDao(datastore);
        
        KafkaCluster kafkaCluster1 = new KafkaCluster();
        kafkaCluster1.setId("first-cluster");
        kafkaCluster1.setName("First Cluster");
        
        KafkaCluster kafkaCluster2 = new KafkaCluster();
        kafkaCluster2.setId("second-cluster");
        kafkaCluster2.setName("Second Cluster");
        
        KafkaCluster kafkaCluster3 = new KafkaCluster();
        kafkaCluster3.setId("third-cluster");
        kafkaCluster3.setName("Third Cluster");
        
        kafkaDao.save(kafkaCluster3);
        kafkaDao.save(kafkaCluster1);
        kafkaDao.save(kafkaCluster2);
    }
    
    @Test
    public void findAllKafkaClusters() {
        List<KafkaCluster> kafkaClusters = kafkaDao.findAll();
        
        assertEquals("There should be 3 kafka clusters in the datastore", 3, kafkaClusters.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the cluster list should have and id of \"first\"",
                "first-cluster", kafkaClusters.get(0).getId());
        assertEquals("The second item in the cluster list should have and id of \"second\"",
                "second-cluster", kafkaClusters.get(1).getId());
        assertEquals("The third item in the cluster list should have and id of \"third\"",
                "third-cluster", kafkaClusters.get(2).getId());
    }
    
    @Test
    public void findKafkaClusterByName() {
        KafkaCluster validCluster = kafkaDao.findByName("First Cluster");
        
        assertNotNull("The returned cluster should not be null with valid query values", validCluster);
        
        KafkaCluster invalidCluster = kafkaDao.findByName("Invalid Cluster");
        
        assertNull("The returned component should be null with invalid query values", invalidCluster);
    }
}