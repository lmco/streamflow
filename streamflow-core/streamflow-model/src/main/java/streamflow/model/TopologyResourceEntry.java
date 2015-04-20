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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.HashMap;

@Embedded
@JsonInclude(Include.NON_NULL)
public class TopologyResourceEntry implements Serializable {

    private String id;

    private String name;

    private String description;

    private String version;

    private String framework;
    
    private String frameworkHash;

    private String resource;

    private String resourceClass;

    private HashMap<String, String> properties = new HashMap<String, String>();

    private HashMap<String, String> propertyTypes = new HashMap<String, String>();

    
    public TopologyResourceEntry() {
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFrameworkHash() {
        return frameworkHash;
    }

    public void setFrameworkHash(String frameworkHash) {
        this.frameworkHash = frameworkHash;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(String resourceClass) {
        this.resourceClass = resourceClass;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public HashMap<String, String> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(HashMap<String, String> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 61 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 61 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 61 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 61 * hash + (this.frameworkHash != null ? this.frameworkHash.hashCode() : 0);
        hash = 61 * hash + (this.resource != null ? this.resource.hashCode() : 0);
        hash = 61 * hash + (this.resourceClass != null ? this.resourceClass.hashCode() : 0);
        hash = 61 * hash + (this.properties != null ? this.properties.hashCode() : 0);
        hash = 61 * hash + (this.propertyTypes != null ? this.propertyTypes.hashCode() : 0);
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
        final TopologyResourceEntry other = (TopologyResourceEntry) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
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
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.framework == null) ? (other.framework != null) 
                : !this.framework.equals(other.framework)) {
            return false;
        }
        if ((this.frameworkHash == null) ? (other.frameworkHash != null) 
                : !this.frameworkHash.equals(other.frameworkHash)) {
            return false;
        }
        if ((this.resource == null) ? (other.resource != null) 
                : !this.resource.equals(other.resource)) {
            return false;
        }
        if ((this.resourceClass == null) ? (other.resourceClass != null) 
                : !this.resourceClass.equals(other.resourceClass)) {
            return false;
        }
        if (this.properties != other.properties && (this.properties == null 
                || !this.properties.equals(other.properties))) {
            return false;
        }
        if (this.propertyTypes != other.propertyTypes && (this.propertyTypes == null 
                || !this.propertyTypes.equals(other.propertyTypes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyResourceEntry{" + "id=" + id + ", name=" + name 
                + ", description=" + description + ", version=" + version 
                + ", framework=" + framework + ", frameworkHash=" + frameworkHash
                + ", resource=" + resource + ", resourceClass=" + resourceClass 
                + ", properties=" + properties + ", propertyTypes=" + propertyTypes + '}';
    }
}
