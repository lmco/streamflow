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

@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceProperty implements Serializable {

    private String name;

    private String label;

    private String type;

    private String description;

    private String defaultValue;

    private boolean required;

    @Embedded
    private ResourcePropertyOptions options = new ResourcePropertyOptions();

    
    public ResourceProperty() {
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ResourcePropertyOptions getOptions() {
        return options;
    }

    public void setOptions(ResourcePropertyOptions options) {
        this.options = options;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        hash = 97 * hash + (this.required ? 1 : 0);
        hash = 97 * hash + (this.options != null ? this.options.hashCode() : 0);
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
        final ResourceProperty other = (ResourceProperty) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.defaultValue == null) ? (other.defaultValue != null) : !this.defaultValue.equals(other.defaultValue)) {
            return false;
        }
        if (this.required != other.required) {
            return false;
        }
        if (this.options != other.options && (this.options == null || !this.options.equals(other.options))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceProperty{" + "name=" + name + ", label=" + label + ", type=" + type 
                + ", description=" + description + ", defaultValue=" + defaultValue 
                + ", required=" + required + ", options=" + options + '}';
    }
}
