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
import java.util.UUID;
import streamflow.model.Topology;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoTopologyDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoTopologyDao topologyDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        topologyDao = new MongoTopologyDao(datastore);
        
        // Preload some items into the datastore
        Topology topology1 = new Topology();
        topology1.setId("first");
        topology1.setName("First Topology");
        topology1.setType("STANDARD");
        topology1.setUserId("user1");
        
        Topology topology2 = new Topology();
        topology2.setId("second");
        topology2.setName("Second Topology");
        topology2.setType("STANDARD");
        topology2.setUserId("user2");
        
        Topology topology3 = new Topology();
        topology3.setId("third");
        topology3.setName("Third Topology");
        topology3.setType("TRIDENT");
        topology3.setUserId("user1");
        
        Topology topology4 = new Topology();
        topology4.setId("fourth");
        topology4.setName("Fourth Topology");
        topology4.setType("TRIDENT");
        topology4.setUserId(null);
        
        topologyDao.save(topology3);
        topologyDao.save(topology1);
        topologyDao.save(topology4);
        topologyDao.save(topology2);
    }
    
    @Test
    public void findAllTopologiesForUser() {
        List<Topology> topologies = topologyDao.findAll("user1");
        
        assertEquals("There should be 2 topologies for the user", 2, topologies.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the topology list should have and id of \"first\"",
                "first", topologies.get(0).getId());
        assertEquals("The second item in the topology list should have and id of \"third\"",
                "third", topologies.get(1).getId());
    }
    
    @Test
    public void findAllTopologiesForAnonymous() {
        List<Topology> topologies = topologyDao.findAll(null);
        
        assertEquals("There should be 1 topologies for the anonymous user", 1, topologies.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The only item in the topology list should have and id of \"fourth\"",
                "fourth", topologies.get(0).getId());
    }
    
    @Test
    public void checkIfTopologyExistsForUser() {
        assertTrue("Exists should return true for an entity with a valid ID", 
                topologyDao.exists("first", "user1"));
        
        assertFalse("Exists should return false for an entity with a invalid ID", 
                topologyDao.exists("second", "user1"));
    }
    
    @Test
    public void checkIfTopologyExistsForAnonymous() {
        assertTrue("Exists should return true for an entity with a valid ID", 
                topologyDao.exists("fourth", null));
        
        assertFalse("Exists should return false for an entity with a invalid ID", 
                topologyDao.exists("second", null));
    }
    
    @Test
    public void findTopologyByIdForUser() {
        Topology validTopology = topologyDao.findById("first", "user1");
        
        assertNotNull("The returned topology should not be null with valid query values", validTopology);
        
        Topology invalidTopology = topologyDao.findById("second", "user1");
        
        assertNull("The returned topology should be null with invalid query values", invalidTopology);
    }
    
    @Test
    public void findTopologyByIdForAnonymous() {
        Topology validTopology = topologyDao.findById("fourth", null);
        
        assertNotNull("The returned topology should not be null with valid query values", validTopology);
        
        Topology invalidTopology = topologyDao.findById("second", null);
        
        assertNull("The returned topology should be null with invalid query values", invalidTopology);
    }
    
    @Test
    public void findTopologyByNameForUser() {
        Topology validTopology = topologyDao.findByName("First Topology", "user1");
        
        assertNotNull("The returned topology should not be null with valid query values", validTopology);
        
        Topology invalidTopology = topologyDao.findByName("Second Topology", "user1");
        
        assertNull("The returned topology should be null with invalid query values", invalidTopology);
    }
    
    @Test
    public void findTopologyByNameForAnonymous() {
        Topology validTopology = topologyDao.findByName("Fourth Topology", null);
        
        assertNotNull("The returned topology should not be null with valid query values", validTopology);
        
        Topology invalidTopology = topologyDao.findByName("Second Topology", null);
        
        assertNull("The returned topology should be null with invalid query values", invalidTopology);
    }
    
    @Test
    public void saveTopologyForUser() {
        Topology requestTopology = new Topology();
        requestTopology.setId(UUID.randomUUID().toString());
        requestTopology.setName("New Topology");
        requestTopology.setType("STANDARD");
        requestTopology.setUserId("thisWillBeOverriden");
        
        Topology responseTopology = topologyDao.save(requestTopology, "user2");
        
        assertEquals("Response topology should override original user id in the topology",
                "user2", responseTopology.getUserId());
        
        assertEquals("The topology count should increase to 2 for user2", 
                2, topologyDao.findAll("user2").size());
    }
    
    @Test
    public void saveTopologyForAnonymous() {
        Topology requestTopology = new Topology();
        requestTopology.setId(UUID.randomUUID().toString());
        requestTopology.setName("New Topology");
        requestTopology.setType("STANDARD");
        requestTopology.setUserId("thisWillBeOverriden");
        
        Topology responseTopology = topologyDao.save(requestTopology, null);
        
        assertNull("Response topology should override original user id in the topology",
                responseTopology.getUserId());
        
        assertEquals("The topology count should increase to 2 after creation of new topology", 
                2, topologyDao.findAll(null).size());
    }
    
    @Test
    public void updateTopologyForUser() {
        Topology requestTopology = new Topology();
        requestTopology.setId("first");
        requestTopology.setName("Updated Name");
        requestTopology.setDescription("Updated Description");
        
        Topology responseTopology = topologyDao.update(requestTopology, "user1");
        
        assertNotNull("The updated entity should be returned after saving", responseTopology);
        
        assertEquals("Response topology name should match the requested topology name",
                requestTopology.getName(), responseTopology.getName());
        
        assertEquals("The datastore should still have 2 topologies for the specified user", 
                2, topologyDao.findAll("user1").size());
    }
    
    @Test
    public void updateTopologyForAnonymous() {
        Topology requestTopology = new Topology();
        requestTopology.setId("fourth");
        requestTopology.setName("Updated Name");
        requestTopology.setDescription("Updated Description");
        
        Topology responseTopology = topologyDao.update(requestTopology, null);
        
        assertNotNull("The updated entity should be returned after saving", responseTopology);
        
        assertEquals("Response topology name should match the requested topology name",
                requestTopology.getName(), responseTopology.getName());
        
        assertEquals("The datastore should still have 2 topologies for the specified user", 
                1, topologyDao.findAll(null).size());
    }
    
    @Test
    public void deleteTopologyByReferenceForUser() {
        Topology topology = topologyDao.findById("first", "user1");
        
        topologyDao.delete(topology, "user1");
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, topologyDao.findAll("user1").size());
    }
    
    @Test
    public void deleteTopologyByReferenceForAnonymous() {
        Topology topology = topologyDao.findById("fourth", null);
        
        topologyDao.delete(topology, null);
        
        assertEquals("The datastore should have 0 entities after the delete", 
                0, topologyDao.findAll(null).size());
    }
    
    @Test
    public void deleteTopologyByIdForUser() {
        topologyDao.deleteById("first", "user1");
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, topologyDao.findAll("user1").size());
        
        topologyDao.deleteById("second", "user1");
        
        assertEquals("Invalid delete values should result in 1 entity ", 
                1, topologyDao.findAll("user1").size());
    }
    
    @Test
    public void deleteTopologyByIdForAnonymous() {
        topologyDao.deleteById("fourth", null);
        
        assertEquals("The datastore should have 0 entities after the delete", 
                0, topologyDao.findAll(null).size());
        
        topologyDao.deleteById("second", null);
        
        assertEquals("Invalid delete values should result in 0 entities ", 
                0, topologyDao.findAll(null).size());
    }
}