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
import streamflow.model.util.DateDeserializer;
import streamflow.model.util.DateSerializer;

@Entity("serialization")
public class Serialization implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String typeClass;

    private String serializerClass;

    private int priority;

    private String framework;
    
    private String frameworkLabel;

    private String version;

    private Date created;

    private Date modified;

    
    public Serialization() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(String serializerClass) {
        this.serializerClass = serializerClass;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFrameworkLabel() {
        return frameworkLabel;
    }

    public void setFrameworkLabel(String frameworkLabel) {
        this.frameworkLabel = frameworkLabel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 23 * hash + (this.typeClass != null ? this.typeClass.hashCode() : 0);
        hash = 23 * hash + (this.serializerClass != null ? this.serializerClass.hashCode() : 0);
        hash = 23 * hash + this.priority;
        hash = 23 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 23 * hash + (this.frameworkLabel != null ? this.frameworkLabel.hashCode() : 0);
        hash = 23 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 23 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 23 * hash + (this.modified != null ? this.modified.hashCode() : 0);
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
        final Serialization other = (Serialization) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.typeClass == null) ? (other.typeClass != null) 
                : !this.typeClass.equals(other.typeClass)) {
            return false;
        }
        if ((this.serializerClass == null) ? (other.serializerClass != null) 
                : !this.serializerClass.equals(other.serializerClass)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        if ((this.framework == null) ? (other.framework != null) 
                : !this.framework.equals(other.framework)) {
            return false;
        }
        if ((this.frameworkLabel == null) ? (other.frameworkLabel != null) 
                : !this.frameworkLabel.equals(other.frameworkLabel)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
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
        return true;
    }

    @Override
    public String toString() {
        return "Serialization{" + "id=" + id + ", typeClass=" + typeClass 
                + ", serializerClass=" + serializerClass + ", priority=" + priority 
                + ", framework=" + framework + ", frameworkLabel=" + frameworkLabel 
                + ", version=" + version + ", created=" + created + ", modified=" + modified + '}';
    }
}
