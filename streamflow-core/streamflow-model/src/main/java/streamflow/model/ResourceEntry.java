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
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import streamflow.model.util.DateSerializer;
import java.io.Serializable;
import java.util.Date;
import streamflow.model.util.DateDeserializer;

@Entity("resourceEntry")
public class ResourceEntry implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;
    
    private String userId;

    private String name;

    private String description;

    private Date created;

    private Date modified;

    @Embedded
    private ResourceEntryConfig config;

    private String resource;

    
    public ResourceEntry() {
        this.created = new Date();
        this.modified = this.created;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ResourceEntryConfig getConfig() {
        return config;
    }

    public void setConfig(ResourceEntryConfig config) {
        this.config = config;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 13 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 13 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 13 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 13 * hash + (this.config != null ? this.config.hashCode() : 0);
        hash = 13 * hash + (this.resource != null ? this.resource.hashCode() : 0);
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
        final ResourceEntry other = (ResourceEntry) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.userId == null) ? (other.userId != null) 
                : !this.userId.equals(other.userId)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) 
                : !this.description.equals(other.description)) {
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
        if (this.config != other.config 
                && (this.config == null || !this.config.equals(other.config))) {
            return false;
        }
        if ((this.resource == null) ? (other.resource != null) 
                : !this.resource.equals(other.resource)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceEntry{" + "id=" + id + ", userId=" + userId + ", name=" + name 
                + ", description=" + description + ", created=" + created 
                + ", modified=" + modified + ", config=" + config 
                + ", resource=" + resource + '}';
    }
}
