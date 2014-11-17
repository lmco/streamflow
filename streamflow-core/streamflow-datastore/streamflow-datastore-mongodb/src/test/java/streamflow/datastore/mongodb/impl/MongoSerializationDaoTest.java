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
import streamflow.model.Serialization;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoSerializationDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoSerializationDao serializationDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        serializationDao = new MongoSerializationDao(datastore);
        
        // Preload some items into the datastore
        Serialization serialization1 = new Serialization();
        serialization1.setId("first-serialization");
        serialization1.setFramework("test");
        serialization1.setTypeClass("streamflow.test.First");
        
        Serialization serialization2 = new Serialization();
        serialization2.setId("second-serialization");
        serialization2.setFramework("sample");
        serialization2.setTypeClass("streamflow.test.second");
        
        Serialization serialization3 = new Serialization();
        serialization3.setId("third-serialization");
        serialization3.setFramework("test");
        serialization3.setTypeClass("streamflow.test.third");
        
        serializationDao.save(serialization3);
        serializationDao.save(serialization1);
        serializationDao.save(serialization2);
    }
    
    @Test
    public void findAllSerializations() {
        List<Serialization> serializations = serializationDao.findAll();
        
        assertEquals("There should be 3 serializations in the datastore", 3, serializations.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the serialization list should have and id of \"first-serialization\"",
                "first-serialization", serializations.get(0).getId());
        assertEquals("The second item in the serialization list should have and id of \"second-serialization\"",
                "second-serialization", serializations.get(1).getId());
        assertEquals("The third item in the serialization list should have and id of \"third-serialization\"",
                "third-serialization", serializations.get(2).getId());
    }
    
    @Test
    public void findAllSerializationsWithFramework() {
        List<Serialization> serializations = serializationDao.findAllWithFramework("test");
        
        assertEquals("There should be 2 serializations with the given framework", 2, serializations.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the serialization list should have and id of \"first-serialization\"",
                "first-serialization", serializations.get(0).getId());
        assertEquals("The second item in the serialization list should have and id of \"third-serializations\"",
                "third-serialization", serializations.get(1).getId());
    }
    
    @Test
    public void findSerializationByTypeClass() {
        Serialization validSerialization = serializationDao.findByTypeClass("streamflow.test.First");
        
        assertNotNull("The returned serialization should not be null with valid query values", validSerialization);
        
        Serialization invalidSerialization = serializationDao.findByTypeClass("streamflow.test.Invalid");
        
        assertNull("The returned serialization should be null with invalid query values", invalidSerialization);
    }
}