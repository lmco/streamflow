/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package streamflow.datastore.jdbc.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = UserEntity.FIND_ALL,
            query = "SELECT u FROM UserEntity u ORDER BY u.lastName, u.firstName ASC"),
    @NamedQuery(name = UserEntity.FIND_BY_USERNAME,
            query = "SELECT u FROM UserEntity u WHERE u.username = :username"),
    @NamedQuery(name = UserEntity.FIND_BY_EMAIL,
            query = "SELECT u FROM UserEntity u WHERE u.email = :email")
})
public class UserEntity implements Serializable {

    public static final String FIND_ALL = "UserEntity.findAll";
    public static final String FIND_BY_USERNAME = "UserEntity.findByUsername";
    public static final String FIND_BY_EMAIL = "UserEntity.findByEmail";

    @Id
    private String id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;
    
    private String password;
    
    private String passwordSalt;

    @Lob
    private String entity;

    public UserEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
