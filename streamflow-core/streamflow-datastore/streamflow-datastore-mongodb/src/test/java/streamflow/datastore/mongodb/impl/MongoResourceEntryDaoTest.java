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
import streamflow.model.ResourceEntry;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoResourceEntryDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoResourceEntryDao resourceEntryDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        resourceEntryDao = new MongoResourceEntryDao(datastore);
        
        // Preload some items into the datastore
        ResourceEntry resourceEntry1 = new ResourceEntry();
        resourceEntry1.setId("first");
        resourceEntry1.setName("First Resource Entry");
        resourceEntry1.setResource("resource-1");
        resourceEntry1.setUserId("user1");
        
        ResourceEntry resourceEntry2 = new ResourceEntry();
        resourceEntry2.setId("second");
        resourceEntry2.setName("Second Resource Entry");
        resourceEntry2.setResource("resource-1");
        resourceEntry2.setUserId("user2");
        
        ResourceEntry resourceEntry3 = new ResourceEntry();
        resourceEntry3.setId("third");
        resourceEntry3.setName("Third Resource Entry");
        resourceEntry3.setResource("resource-1");
        resourceEntry3.setUserId("user1");
        
        ResourceEntry resourceEntry4 = new ResourceEntry();
        resourceEntry4.setId("fourth");
        resourceEntry4.setName("First Resource Entry");
        resourceEntry4.setResource("resource-1");
        resourceEntry4.setUserId(null);
        
        ResourceEntry resourceEntry5 = new ResourceEntry();
        resourceEntry5.setId("fifth");
        resourceEntry5.setName("Fifth Resource Entry");
        resourceEntry5.setResource("resource-2");
        resourceEntry5.setUserId("user1");
        
        ResourceEntry resourceEntry6 = new ResourceEntry();
        resourceEntry6.setId("sixth");
        resourceEntry6.setName("Sixth Resource Entry");
        resourceEntry6.setResource("resource-2");
        resourceEntry6.setUserId(null);
        
        resourceEntryDao.save(resourceEntry1);
        resourceEntryDao.save(resourceEntry2);
        resourceEntryDao.save(resourceEntry3);
        resourceEntryDao.save(resourceEntry4);
        resourceEntryDao.save(resourceEntry5);
        resourceEntryDao.save(resourceEntry6);
    }
    
    @Test
    public void saveResourceEntryForUser() {
        ResourceEntry requestResourceEntry = new ResourceEntry();
        requestResourceEntry.setId("seventh");
        requestResourceEntry.setName("Seventh Resource");
        requestResourceEntry.setResource("resource-1");
        requestResourceEntry.setUserId("userToOverride");
        
        ResourceEntry responseTopology = resourceEntryDao.save(requestResourceEntry, "user1");
        
        assertEquals("Response resource entry should override original user id in the resource entry",
                "user1", responseTopology.getUserId());
        
        assertEquals("The resource entry count should increase to 3 for user1", 3, 
                resourceEntryDao.findAllWithResource(requestResourceEntry.getResource(), "user1").size());
    }
    
    @Test
    public void saveResourceEntryForAnonymous() {
        ResourceEntry requestResourceEntry = new ResourceEntry();
        requestResourceEntry.setId("seventh");
        requestResourceEntry.setName("Seventh Resource");
        requestResourceEntry.setResource("resource-1");
        requestResourceEntry.setUserId("userToOverride");
        
        ResourceEntry responseTopology = resourceEntryDao.save(requestResourceEntry, null);
        
        assertNull("Response resource entry should override original user id in the resource entry",
                responseTopology.getUserId());
        
        assertEquals("The resource entry count should increase to 2 for anonymous", 2, 
                resourceEntryDao.findAllWithResource(requestResourceEntry.getResource(), null).size());
    }
    
    @Test
    public void updateResourceEntryForUser() {
        ResourceEntry requestResourceEntry = new ResourceEntry();
        requestResourceEntry.setId("first");
        requestResourceEntry.setName("Updated Name");
        requestResourceEntry.setDescription("Updated Description");
        requestResourceEntry.setResource("resource-1");
        
        ResourceEntry responseResourceEntry = resourceEntryDao.update(requestResourceEntry, "user1");
        
        assertNotNull("The updated entry should be returned after saving", responseResourceEntry);
        
        assertEquals("Response entry name should match the requested entry name",
                requestResourceEntry.getName(), responseResourceEntry.getName());
        
        assertEquals("The datastore should still have 2 entries for the specified user", 2, 
                resourceEntryDao.findAllWithResource(requestResourceEntry.getResource(), "user1").size());
    }
    
    @Test
    public void updateResourceEntryForAnonymous() {
        ResourceEntry requestResourceEntry = new ResourceEntry();
        requestResourceEntry.setId("fourth");
        requestResourceEntry.setName("Updated Name");
        requestResourceEntry.setDescription("Updated Description");
        requestResourceEntry.setResource("resource-1");
        
        ResourceEntry responseResourceEntry = resourceEntryDao.update(requestResourceEntry, null);
        
        assertNotNull("The updated entry should be returned after saving", responseResourceEntry);
        
        assertEquals("Response entry name should match the requested entry name",
                requestResourceEntry.getName(), responseResourceEntry.getName());
        
        assertEquals("The datastore should still have 1 entry for the specified user", 1, 
                resourceEntryDao.findAllWithResource(requestResourceEntry.getResource(), null).size());
    }
    
    @Test
    public void checkIfResourceEntryExistsForUser() {
        assertTrue("Exists should return true for an entity with a valid ID", 
                resourceEntryDao.exists("first", "user1"));
        
        assertFalse("Exists should return false for an entity with a invalid ID", 
                resourceEntryDao.exists("second", "user1"));
    }
    
    @Test
    public void checkIfResourceEntryExistsForAnonymous() {
        assertTrue("Exists should return true for an entity with a valid ID", 
                resourceEntryDao.exists("fourth", null));
        
        assertFalse("Exists should return false for an entity with a invalid ID", 
                resourceEntryDao.exists("second", null));
    }
    
    @Test
    public void findAllWithResourceForUser() {
        List<ResourceEntry> resource1Entries = resourceEntryDao.findAllWithResource("resource-1", "user1");
        
        assertEquals("There should be 2 entry for the user", 2, resource1Entries.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the entry list should have and id of \"first\"",
                "first", resource1Entries.get(0).getId());
        assertEquals("The second item in the entry list should have and id of \"third\"",
                "third", resource1Entries.get(1).getId());
        
        List<ResourceEntry> resource2Entries = resourceEntryDao.findAllWithResource("resource-2", "user1");
        
        assertEquals("There should be 1 entry for the user", 1, resource2Entries.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the entry list should have and id of \"fifth\"",
                "fifth", resource2Entries.get(0).getId());
    }
    
    @Test
    public void findAllWithResourceForAnonymous() {
        List<ResourceEntry> resource1Entries = resourceEntryDao.findAllWithResource("resource-1", null);
        
        assertEquals("There should be 2 entry for the user", 1, resource1Entries.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the entry list should have and id of \"fourth\"",
                "fourth", resource1Entries.get(0).getId());
        
        List<ResourceEntry> resource2Entries = resourceEntryDao.findAllWithResource("resource-2", null);
        
        assertEquals("There should be 1 entry for the user", 1, resource2Entries.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the entry list should have and id of \"sixth\"",
                "sixth", resource2Entries.get(0).getId());
    }
    
    @Test
    public void findResourceEntryByIdForUser() {
        ResourceEntry validResourceEntry = resourceEntryDao.findById("first", "user1");
        
        assertNotNull("The returned entry should not be null with valid query values", validResourceEntry);
        
        ResourceEntry invalidTopology = resourceEntryDao.findById("second", "user1");
        
        assertNull("The returned entry should be null with invalid query values", invalidTopology);
    }
    
    @Test
    public void findResourceEntryByIdForAnonymous() {
        ResourceEntry validResourceEntry = resourceEntryDao.findById("fourth", null);
        
        assertNotNull("The returned entry should not be null with valid query values", validResourceEntry);
        
        ResourceEntry invalidResourceEntry = resourceEntryDao.findById("second", null);
        
        assertNull("The returned entry should be null with invalid query values", invalidResourceEntry);
    }
    
    @Test
    public void findResourceEntryByResourceAndNameForUser() {
        ResourceEntry validResourceEntry = resourceEntryDao.findByResourceAndName(
                "resource-1", "First Resource Entry", "user1");
        
        assertNotNull("The returned entry should not be null with valid query values", validResourceEntry);
        
        ResourceEntry invalidResourceEntry = resourceEntryDao.findByResourceAndName(
                "resource-1", "Fifth Resource Entry", "user1");
        
        assertNull("The returned entry should be null with invalid query values", invalidResourceEntry);
    }
    
    @Test
    public void findResourceEntryByResourceAndNameForAnonymous() {
        ResourceEntry validResourceEntry = resourceEntryDao.findByResourceAndName(
                "resource-2", "Sixth Resource Entry", null);
        
        assertNotNull("The returned entry should not be null with valid query values", validResourceEntry);
        
        ResourceEntry invalidResourceEntry = resourceEntryDao.findByResourceAndName(
                "resource-2", "First Resource Entry", null);
        
        assertNull("The returned entry should be null with invalid query values", invalidResourceEntry);
    }
    
    @Test
    public void deleteResourceEntryByReferenceForUser() {
        ResourceEntry resourceEntry = resourceEntryDao.findById("first", "user1");
        
        resourceEntryDao.delete(resourceEntry, "user1");
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-1", "user1").size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", "user1").size());
    }
    
    @Test
    public void deleteResourceEntryByReferenceForAnonymous() {
        ResourceEntry resourceEntry = resourceEntryDao.findById("fourth", null);
        
        resourceEntryDao.delete(resourceEntry, null);
        
        assertEquals("The datastore should have 0 entities after the delete", 
                0, resourceEntryDao.findAllWithResource("resource-1", null).size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", null).size());
    }
    
    @Test
    public void deleteResourceEntryByIdForUser() {
        resourceEntryDao.deleteById("first", "user1");
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-1", "user1").size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", null).size());
        
        resourceEntryDao.deleteById("second", "user1");
        
        assertEquals("The datastore should have 0 entities after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-1", "user1").size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", null).size());
    }
    
    @Test
    public void deleteResourceEntryByIdForAnonymous() {
        resourceEntryDao.deleteById("fourth", null);
        
        assertEquals("The datastore should have 0 entities after the delete", 
                0, resourceEntryDao.findAllWithResource("resource-1", null).size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", null).size());
        
        resourceEntryDao.deleteById("second", null);
        
        assertEquals("The datastore should have 0 entities after the delete", 
                0, resourceEntryDao.findAllWithResource("resource-1", null).size());
        
        assertEquals("The datastore should have 1 entity after the delete", 
                1, resourceEntryDao.findAllWithResource("resource-2", null).size());
    }
}