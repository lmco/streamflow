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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import streamflow.model.util.DateDeserializer;
import streamflow.model.util.DateSerializer;

@Entity("role")
public class Role implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String name;

    private boolean enabled;

    private Date created;

    private Date modified;

    private HashSet<String> permissions = new HashSet<String>();

    
    public Role() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public HashSet<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashSet<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 73 * hash + (this.enabled ? 1 : 0);
        hash = 73 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 73 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 73 * hash + (this.permissions != null ? this.permissions.hashCode() : 0);
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
        final Role other = (Role) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.created != other.created && (this.created == null 
                || !this.created.equals(other.created))) {
            return false;
        }
        if (this.modified != other.modified && (this.modified == null 
                || !this.modified.equals(other.modified))) {
            return false;
        }
        if (this.permissions != other.permissions && (this.permissions == null 
                || !this.permissions.equals(other.permissions))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", name=" + name + ", enabled=" + enabled 
                + ", created=" + created + ", modified=" + modified 
                + ", permissions=" + permissions + '}';
    }
}
