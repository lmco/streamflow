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
import streamflow.model.util.DateSerializer;
import java.io.Serializable;
import java.util.Date;
import streamflow.model.util.DateDeserializer;

@Entity("framework")
public class Framework implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String name;

    private String version;

    private String label;

    private int count;

    private boolean enabled;
    
    private boolean isPublic;

    private String description;

    private String info;

    private Date created;

    private Date modified;

    private long jarSize;

    private String jarId;
    
    private String userId;
    
    public static final String VISIBILITY_ALL = "ALL";
    
    public static final String VISIBILITY_PUBLIC = "PUBLIC";
    
    public static final String VISIBILITY_PRIVATE = "PRIVATE";
    
    public enum FrameworkVisibility {
        ALL, PUBLIC, PRIVATE
    }

    
    public Framework() {
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
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

    public long getJarSize() {
        return jarSize;
    }

    public void setSize(long jarSize) {
        this.jarSize = jarSize;
    }

    public String getJarId() {
        return jarId;
    }

    public void setJarId(String jarId) {
        this.jarId = jarId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 31 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 31 * hash + this.count;
        hash = 31 * hash + (this.enabled ? 1 : 0);
        hash = 31 * hash + (this.isPublic ? 1 : 0);
        hash = 31 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 31 * hash + (this.info != null ? this.info.hashCode() : 0);
        hash = 31 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 31 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 31 * hash + (int) (this.jarSize ^ (this.jarSize >>> 32));
        hash = 31 * hash + (this.jarId != null ? this.jarId.hashCode() : 0);
        hash = 31 * hash + (this.userId != null ? this.userId.hashCode() : 0);
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
        final Framework other = (Framework) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.isPublic != other.isPublic) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) 
                : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.info == null) ? (other.info != null) 
                : !this.info.equals(other.info)) {
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
        if (this.jarSize != other.jarSize) {
            return false;
        }
        if ((this.jarId == null) ? (other.jarId != null) 
                : !this.jarId.equals(other.jarId)) {
            return false;
        }
        if ((this.userId == null) ? (other.userId != null) 
                : !this.userId.equals(other.userId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Framework{" + "id=" + id + ", name=" + name + ", version=" + version 
                + ", label=" + label + ", count=" + count + ", enabled=" + enabled 
                + ", isPublic=" + isPublic + ", description=" + description 
                + ", info=" + info + ", created=" + created + ", modified=" + modified 
                + ", jarSize=" + jarSize + ", jarId=" + jarId + ", userId=" + userId + '}';
    }
}
