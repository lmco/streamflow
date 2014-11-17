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

@Entity("resource")
public class Resource implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String name;

    private String label;

    private Date created;

    private Date modified;

    @Embedded
    private ResourceConfig config;

    private String framework;
    
    private String frameworkLabel;

    private String version;

    
    public Resource() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public ResourceConfig getConfig() {
        return config;
    }

    public void setConfig(ResourceConfig config) {
        this.config = config;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 89 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 89 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 89 * hash + (this.config != null ? this.config.hashCode() : 0);
        hash = 89 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 89 * hash + (this.frameworkLabel != null ? this.frameworkLabel.hashCode() : 0);
        hash = 89 * hash + (this.version != null ? this.version.hashCode() : 0);
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
        final Resource other = (Resource) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) 
                : !this.label.equals(other.label)) {
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
        return true;
    }

    @Override
    public String toString() {
        return "Resource{" + "id=" + id + ", name=" + name + ", label=" + label 
                + ", created=" + created + ", modified=" + modified + ", config=" + config 
                + ", framework=" + framework + ", frameworkLabel=" + frameworkLabel 
                + ", version=" + version + '}';
    }
}
