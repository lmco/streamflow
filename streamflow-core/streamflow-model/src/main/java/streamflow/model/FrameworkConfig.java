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
public class FrameworkConfig implements Serializable {

    private String name;

    private String version;

    private String label;

    private String description;

    @Embedded
    private ArrayList<ComponentConfig> components = new ArrayList<ComponentConfig>();

    @Embedded
    private ArrayList<ResourceConfig> resources = new ArrayList<ResourceConfig>();

    @Embedded
    private ArrayList<SerializationConfig> serializations = new ArrayList<SerializationConfig>();

    
    public FrameworkConfig() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<ComponentConfig> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<ComponentConfig> components) {
        this.components = components;
    }

    public ArrayList<ResourceConfig> getResources() {
        return resources;
    }

    public void setResources(ArrayList<ResourceConfig> resources) {
        this.resources = resources;
    }

    public ArrayList<SerializationConfig> getSerializations() {
        return serializations;
    }

    public void setSerializations(ArrayList<SerializationConfig> serializations) {
        this.serializations = serializations;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 23 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 23 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 23 * hash + (this.components != null ? this.components.hashCode() : 0);
        hash = 23 * hash + (this.resources != null ? this.resources.hashCode() : 0);
        hash = 23 * hash + (this.serializations != null ? this.serializations.hashCode() : 0);
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
        final FrameworkConfig other = (FrameworkConfig) obj;
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
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
        if (this.components != other.components && (this.components == null 
                || !this.components.equals(other.components))) {
            return false;
        }
        if (this.resources != other.resources && (this.resources == null 
                || !this.resources.equals(other.resources))) {
            return false;
        }
        if (this.serializations != other.serializations && (this.serializations == null 
                || !this.serializations.equals(other.serializations))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FrameworkConfig{" + "name=" + name + ", version=" + version + ", label=" + label 
                + ", description=" + description + ", components=" + components 
                + ", resources=" + resources + ", serializations=" + serializations + '}';
    }
}
