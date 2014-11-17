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
package streamflow.datastore.mongodb;

import streamflow.model.test.TestEntity;
import com.github.fakemongo.junit.FongoRule;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.util.List;
import java.util.UUID;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

@Category(IntegrationTest.class)
public class MongoDaoTest {
    
    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoDao<TestEntity, String> mongoDao;
    
    private TestEntity mockedEntity;

    @Before
    public void setUp() {
        DBCollection collection = fongoRule.getDB("streamflow").getCollection("test");
        
        mockedEntity = new TestEntity();
        mockedEntity.setId(UUID.randomUUID().toString());
        
        BasicDBObject mockedObject = new BasicDBObject();
        mockedObject.put("_id", mockedEntity.getId());
        mockedObject.put("byteField", mockedEntity.getByteField());
        mockedObject.put("intField", mockedEntity.getIntField());
        mockedObject.put("longField", mockedEntity.getLongField());
        mockedObject.put("doubleField", mockedEntity.getDoubleField());
        mockedObject.put("floatField", mockedEntity.getFloatField());
        mockedObject.put("booleanField", mockedEntity.getBooleanField());
        mockedObject.put("stringField", mockedEntity.getStringField());
        mockedObject.put("byteArrayField", mockedEntity.getByteArrayField());
        collection.save(mockedObject);
        
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        mongoDao = new MongoDao<TestEntity, String>(datastore, TestEntity.class);
    }
    
    @Test
    public void saveEntity() {
        TestEntity requestEntity = new TestEntity();
        requestEntity.setId(UUID.randomUUID().toString());
        
        TestEntity responseEntity = mongoDao.save(requestEntity);
        
        assertNotNull("A newly created entity should be returned after saving", responseEntity);
        assertEquals("The response and request entities should be equal", requestEntity, responseEntity);
        
        assertEquals("The datastore should have one new entity", 2, 
                fongoRule.getDB("streamflow").getCollection("test").count());
    }
    
    @Test
    public void updateEntity() {
        TestEntity requestEntity = mockedEntity;
        requestEntity.setIntField(1000);
        requestEntity.setBooleanField(false);
        requestEntity.setDoubleField(Math.PI);
        requestEntity.setFloatField(7.18f);
        requestEntity.setLongField(5000);
        requestEntity.setStringField("Testing 1...2...3");
        
        TestEntity responseEntity = mongoDao.update(requestEntity);
        
        assertNotNull("The updated entity should be returned after saving", responseEntity);
        assertEquals("The response and request entities should be equal", requestEntity, responseEntity);
        
        assertEquals("The datastore should still have one entity", 1, 
                fongoRule.getDB("streamflow").getCollection("test").count());
    }
    
    @Test
    public void findAllEntities() {
        List<TestEntity> entityList = mongoDao.findAll();
        
        assertEquals("There should be one entity in the datastore", 1, entityList.size());
        
        assertEquals("The mocked entity should match the retrieved entity", 
                entityList.get(0), mockedEntity);
    }
    
    @Test
    public void findEntityById() {
        TestEntity validEntity = mongoDao.findById(mockedEntity.getId());
        
        assertEquals("The mocked entity should match the response entity with a valid ID", 
                mockedEntity, validEntity);
        
        TestEntity invalidEntity = mongoDao.findById("id-should-not-exist");
        
        assertNull("Response entity with an invalid ID shoudl return null", invalidEntity);
    }
    
    @Test
    public void entityExists() {
        assertTrue("Exists should return true for an entity with a valid ID", 
                mongoDao.exists(mockedEntity.getId()));
        
        assertFalse("Exists should return false for an entity with a invalid ID", 
                mongoDao.exists("id-should-not-exist"));
    }
    
    @Test
    public void deleteEntityByReference() {
        TestEntity requestEntity = mongoDao.findById(mockedEntity.getId());
        
        mongoDao.delete(requestEntity);
        
        assertEquals("The datastore should have zero entities after the delete", 0, 
                fongoRule.getDB("streamflow").getCollection("test").count());
    }
    
    @Test
    public void deleteEntityById() {
        mongoDao.deleteById(mockedEntity.getId());
        
        assertEquals("The datastore should have zero entities after the delete", 0, 
                fongoRule.getDB("streamflow").getCollection("test").count());
    }
    
    @Test
    public void queryEntities() {
        Query<TestEntity> testQuery = mongoDao.query();
        
        TestEntity responseEntity = testQuery.field("stringField")
                .equal(mockedEntity.getStringField()).get();
        
        assertEquals("Query response entity should match the mocked entity", 
                mockedEntity, responseEntity);
        
        assertEquals("Query for countAll() should return 1", 1, testQuery.countAll());
    }
}
