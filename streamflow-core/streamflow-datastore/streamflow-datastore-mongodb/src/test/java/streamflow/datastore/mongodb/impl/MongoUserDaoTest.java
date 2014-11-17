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
import streamflow.model.User;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoUserDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoUserDao userDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        userDao = new MongoUserDao(datastore);
        
        User user1 = new User();
        user1.setId("first-user");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setUsername("johndoe");
        user1.setEmail("john.doe@email.com");
        
        User user2 = new User();
        user2.setId("second-user");
        user2.setFirstName("John");
        user2.setLastName("Smith");
        user2.setUsername("johnsmith");
        user2.setEmail("john.smith@email.com");
        
        User user3 = new User();
        user3.setId("third-user");
        user3.setFirstName("Jane");
        user3.setLastName("Doe");
        user3.setUsername("janedoe");
        user3.setEmail("jane.doe@email.com");
        
        userDao.save(user3);
        userDao.save(user1);
        userDao.save(user2);
    }
    
    @Test
    public void findAllUsers() {
        List<User> users = userDao.findAll();
        
        assertEquals("There should be 3 users in the datastore", 3, users.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the user list should have and id of \"third-user\"",
                "third-user", users.get(0).getId());
        assertEquals("The second item in the user list should have and id of \"first-user\"",
                "first-user", users.get(1).getId());
        assertEquals("The third item in the user list should have and id of \"second-user\"",
                "second-user", users.get(2).getId());
    }
    
    @Test
    public void findUserByUsername() {
        User validUser = userDao.findByUsername("johndoe");
        
        assertNotNull("The returned user should not be null with valid query values", validUser);
        
        User invalidUser = userDao.findByUsername("janesmith");
        
        assertNull("The returned user should be null with invalid query values", invalidUser);
    }
    
    @Test
    public void findUserByEmail() {
        User validUser = userDao.findByEmail("john.smith@email.com");
        
        assertNotNull("The returned user should not be null with valid query values", validUser);
        
        User invalidUser = userDao.findByEmail("jane.smith@email.com");
        
        assertNull("The returned user should be null with invalid query values", invalidUser);
    }
}