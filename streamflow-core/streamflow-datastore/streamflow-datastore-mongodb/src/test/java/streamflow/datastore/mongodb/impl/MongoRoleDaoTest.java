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
import streamflow.model.Role;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoRoleDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoRoleDao roleDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        roleDao = new MongoRoleDao(datastore);
        
        Role role1 = new Role();
        role1.setId("first-role");
        role1.setName("First Role");
        
        Role role2 = new Role();
        role2.setId("second-role");
        role2.setName("Second Role");
        
        Role role3 = new Role();
        role3.setId("third-role");
        role3.setName("Third Role");
        
        roleDao.save(role3);
        roleDao.save(role1);
        roleDao.save(role2);
    }
    
    @Test
    public void findAllRoles() {
        List<Role> roles = roleDao.findAll();
        
        assertEquals("There should be 3 roles in the datastore", 3, roles.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the role list should have and id of \"first-role\"",
                "first-role", roles.get(0).getId());
        assertEquals("The second item in the role list should have and id of \"second-role\"",
                "second-role", roles.get(1).getId());
        assertEquals("The third item in the role list should have and id of \"third-role\"",
                "third-role", roles.get(2).getId());
    }
    
    @Test
    public void findRoleByName() {
        Role validRole = roleDao.findByName("First Role");
        
        assertNotNull("The returned role should not be null with valid query values", validRole);
        
        Role invalidRole = roleDao.findByName("Invalid Role");
        
        assertNull("The returned role should be null with invalid query values", invalidRole);
    }
}