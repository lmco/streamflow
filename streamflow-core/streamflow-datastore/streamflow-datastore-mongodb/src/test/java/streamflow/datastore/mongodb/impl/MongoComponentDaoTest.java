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
import streamflow.model.Component;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoComponentDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoComponentDao componentDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        componentDao = new MongoComponentDao(datastore);
        
        // Preload some items into the datastore
        Component component1 = new Component();
        component1.setId("first");
        component1.setFramework("test");
        component1.setName("first-component");
        component1.setLabel("First Component");
        
        Component component2 = new Component();
        component2.setId("second");
        component2.setFramework("sample");
        component2.setName("second-component");
        component2.setLabel("Second Component");
        
        Component component3 = new Component();
        component3.setId("third");
        component3.setFramework("test");
        component3.setName("third-component");
        component3.setLabel("Third Component");
        
        componentDao.save(component3);
        componentDao.save(component1);
        componentDao.save(component2);
    }
    
    @Test
    public void findAllComponents() {
        List<Component> components = componentDao.findAll();
        
        assertEquals("There should be 3 components in the datastore", 3, components.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the component list should have and id of \"first\"",
                "first", components.get(0).getId());
        assertEquals("The second item in the component list should have and id of \"second\"",
                "second", components.get(1).getId());
        assertEquals("The third item in the component list should have and id of \"third\"",
                "third", components.get(2).getId());
    }
    
    @Test
    public void findAllComponentsWithFramework() {
        List<Component> components = componentDao.findAllWithFramework("test");
        
        assertEquals("There should be 2 components with the given framework", 2, components.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the component list should have and id of \"first\"",
                "first", components.get(0).getId());
        assertEquals("The second item in the component list should have and id of \"third\"",
                "third", components.get(1).getId());
    }
    
    @Test
    public void findComponentByFrameworkAndName() {
        Component validComponent = componentDao.findByFrameworkAndName("test", "first-component");
        
        assertNotNull("The returned component should not be null with valid query values", validComponent);
        
        Component invalidComponent = componentDao.findByFrameworkAndName("test", "invalid-component");
        
        assertNull("The returned component should be null with invalid query values", invalidComponent);
    }
}
