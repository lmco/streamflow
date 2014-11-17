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
public class ComponentConfig implements Serializable {

    private String name;

    private String label;

    private String type;

    private String mainClass;

    private String description;
    
    private String icon;

    @Embedded
    private ArrayList<ComponentProperty> properties = new ArrayList<ComponentProperty>();

    @Embedded
    private ArrayList<ComponentInterface> inputs = new ArrayList<ComponentInterface>();

    @Embedded
    private ArrayList<ComponentInterface> outputs = new ArrayList<ComponentInterface>();

    @Embedded
    private ArrayList<ComponentInterface> resources = new ArrayList<ComponentInterface>();

    
    public ComponentConfig() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<ComponentProperty> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<ComponentProperty> properties) {
        this.properties = properties;
    }

    public ArrayList<ComponentInterface> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<ComponentInterface> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<ComponentInterface> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<ComponentInterface> outputs) {
        this.outputs = outputs;
    }

    public ArrayList<ComponentInterface> getResources() {
        return resources;
    }

    public void setResources(ArrayList<ComponentInterface> resources) {
        this.resources = resources;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 83 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 83 * hash + (this.mainClass != null ? this.mainClass.hashCode() : 0);
        hash = 83 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 83 * hash + (this.icon != null ? this.icon.hashCode() : 0);
        hash = 83 * hash + (this.properties != null ? this.properties.hashCode() : 0);
        hash = 83 * hash + (this.inputs != null ? this.inputs.hashCode() : 0);
        hash = 83 * hash + (this.outputs != null ? this.outputs.hashCode() : 0);
        hash = 83 * hash + (this.resources != null ? this.resources.hashCode() : 0);
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
        final ComponentConfig other = (ComponentConfig) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.mainClass == null) ? (other.mainClass != null) 
                : !this.mainClass.equals(other.mainClass)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) 
                : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.icon == null) ? (other.icon != null) : !this.icon.equals(other.icon)) {
            return false;
        }
        if (this.properties != other.properties 
                && (this.properties == null || !this.properties.equals(other.properties))) {
            return false;
        }
        if (this.inputs != other.inputs 
                && (this.inputs == null || !this.inputs.equals(other.inputs))) {
            return false;
        }
        if (this.outputs != other.outputs 
                && (this.outputs == null || !this.outputs.equals(other.outputs))) {
            return false;
        }
        if (this.resources != other.resources 
                && (this.resources == null || !this.resources.equals(other.resources))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ComponentConfig{" + "name=" + name + ", label=" + label + ", type=" + type 
                + ", mainClass=" + mainClass + ", description=" + description + ", icon=" + icon 
                + ", properties=" + properties + ", inputs=" + inputs + ", outputs=" + outputs 
                + ", resources=" + resources + '}';
    }
}
