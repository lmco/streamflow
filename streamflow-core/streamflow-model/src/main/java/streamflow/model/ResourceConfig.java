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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.ArrayList;

@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceConfig implements Serializable {

    private String name;

    private String label;

    private String description;

    private String resourceClass;

    @Embedded
    private ArrayList<ResourceProperty> properties = new ArrayList<ResourceProperty>();

    
    public ResourceConfig() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(String resourceClass) {
        this.resourceClass = resourceClass;
    }

    public ArrayList<ResourceProperty> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<ResourceProperty> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 29 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 29 * hash + (this.resourceClass != null ? this.resourceClass.hashCode() : 0);
        hash = 29 * hash + (this.properties != null ? this.properties.hashCode() : 0);
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
        final ResourceConfig other = (ResourceConfig) obj;
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) 
                : !this.label.equals(other.label)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) 
                : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.resourceClass == null) ? (other.resourceClass != null) 
                : !this.resourceClass.equals(other.resourceClass)) {
            return false;
        }
        if (this.properties != other.properties 
                && (this.properties == null || !this.properties.equals(other.properties))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceConfig{" + "name=" + name + ", label=" + label 
                + ", description=" + description + ", resourceClass=" + resourceClass 
                + ", properties=" + properties + '}';
    }
}
