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
package streamflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import streamflow.model.util.DateDeserializer;
import streamflow.model.util.DateSerializer;

@Entity("user")
public class User implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String passwordSalt;

    private String firstName;

    private String lastName;

    private String email;

    private boolean enabled;

    private Date created;

    private Date modified;

    private HashSet<String> roles = new HashSet<String>();

    
    public User() {
    }

    @Override
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

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPasswordSalt() {
        return passwordSalt;
    }

    @JsonProperty
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getCreated() {
        return created;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getModified() {
        return modified;
    }

    @JsonDeserialize(using = DateDeserializer.class)
    public void setModified(Date modified) {
        this.modified = modified;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

    public void setRoles(HashSet<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 89 * hash + (this.password != null ? this.password.hashCode() : 0);
        hash = 89 * hash + (this.passwordSalt != null ? this.passwordSalt.hashCode() : 0);
        hash = 89 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 89 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 89 * hash + (this.email != null ? this.email.hashCode() : 0);
        hash = 89 * hash + (this.enabled ? 1 : 0);
        hash = 89 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 89 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 89 * hash + (this.roles != null ? this.roles.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.username == null) ? (other.username != null) 
                : !this.username.equals(other.username)) {
            return false;
        }
        if ((this.password == null) ? (other.password != null) 
                : !this.password.equals(other.password)) {
            return false;
        }
        if ((this.passwordSalt == null) ? (other.passwordSalt != null) 
                : !this.passwordSalt.equals(other.passwordSalt)) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) 
                : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) 
                : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.email == null) ? (other.email != null) 
                : !this.email.equals(other.email)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.created != other.created 
                && (this.created == null || !this.created.equals(other.created))) {
            return false;
        }
        if (this.modified != other.modified 
                && (this.modified == null || !this.modified.equals(other.modified))) {
            return false;
        }
        if (this.roles != other.roles 
                && (this.roles == null || !this.roles.equals(other.roles))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password 
                + ", passwordSalt=" + passwordSalt + ", firstName=" + firstName 
                + ", lastName=" + lastName + ", email=" + email + ", enabled=" + enabled 
                + ", created=" + created + ", modified=" + modified + ", roles=" + roles + '}';
    }
}
