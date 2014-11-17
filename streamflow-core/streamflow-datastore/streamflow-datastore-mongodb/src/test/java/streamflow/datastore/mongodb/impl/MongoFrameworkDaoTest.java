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
import streamflow.model.Framework;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoFrameworkDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoFrameworkDao frameworkDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        frameworkDao = new MongoFrameworkDao(datastore);
        
        Framework framework1 = new Framework();
        framework1.setId("first");
        framework1.setName("first-framework");
        framework1.setLabel("First Framework");
        framework1.setVersion("1.0.0");
        
        Framework framework2 = new Framework();
        framework2.setId("second");
        framework2.setName("second-framework");
        framework2.setLabel("Second Framework");
        framework2.setVersion("1.0.0");
        
        Framework framework3 = new Framework();
        framework3.setId("third");
        framework3.setName("third-framework");
        framework3.setLabel("Third Framework");
        framework3.setVersion("1.0.0");
        
        frameworkDao.save(framework3);
        frameworkDao.save(framework1);
        frameworkDao.save(framework2);
    }
    
    @Test
    public void findAllFrameworks() {
        List<Framework> frameworks = frameworkDao.findAll();
        
        assertEquals("There should be 3 frameworks in the datastore", 3, frameworks.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the component list should have and id of \"first\"",
                "first", frameworks.get(0).getId());
        assertEquals("The second item in the component list should have and id of \"second\"",
                "second", frameworks.get(1).getId());
        assertEquals("The third item in the component list should have and id of \"third\"",
                "third", frameworks.get(2).getId());
    }
}