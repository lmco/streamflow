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
import streamflow.model.Resource;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoResourceDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoResourceDao resourceDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        resourceDao = new MongoResourceDao(datastore);
        
        // Preload some items into the datastore
        Resource resource1 = new Resource();
        resource1.setId("first");
        resource1.setFramework("test");
        resource1.setName("first-resource");
        resource1.setLabel("First Resource");
        
        Resource resource2 = new Resource();
        resource2.setId("second");
        resource2.setFramework("sample");
        resource2.setName("second-resource");
        resource2.setLabel("Second Resource");
        
        Resource resource3 = new Resource();
        resource3.setId("third");
        resource3.setFramework("test");
        resource3.setName("third-resource");
        resource3.setLabel("Third Resource");
        
        resourceDao.save(resource3);
        resourceDao.save(resource1);
        resourceDao.save(resource2);
    }
    
    @Test
    public void findAllResources() {
        List<Resource> resources = resourceDao.findAll();
        
        assertEquals("There should be 3 resources in the datastore", 3, resources.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the resource list should have and id of \"first\"",
                "first", resources.get(0).getId());
        assertEquals("The second item in the resource list should have and id of \"second\"",
                "second", resources.get(1).getId());
        assertEquals("The third item in the resource list should have and id of \"third\"",
                "third", resources.get(2).getId());
    }
    
    @Test
    public void findAllResourceWithFramework() {
        List<Resource> resources = resourceDao.findAllWithFramework("test");
        
        assertEquals("There should be 2 resources with the given framework", 2, resources.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the resource list should have and id of \"first\"",
                "first", resources.get(0).getId());
        assertEquals("The second item in the resource list should have and id of \"third\"",
                "third", resources.get(1).getId());
    }
    
    @Test
    public void findResourceByFrameworkAndName() {
        Resource validResource = resourceDao.findByFrameworkAndName("test", "first-resource");
        
        assertNotNull("The returned resource should not be null with valid query values", validResource);
        
        Resource invalidResource = resourceDao.findByFrameworkAndName("test", "invalid-resource");
        
        assertNull("The returned resource should be null with invalid query values", invalidResource);
    }
}