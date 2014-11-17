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

import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.HashMap;

@Embedded
public class ResourceEntryConfig implements Serializable {

    private String description;

    @Embedded
    private HashMap<String, String> properties = new HashMap<String, String>();

    
    public ResourceEntryConfig() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 71 * hash + (this.properties != null ? this.properties.hashCode() : 0);
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
        final ResourceEntryConfig other = (ResourceEntryConfig) obj;
        if ((this.description == null) ? (other.description != null) 
                : !this.description.equals(other.description)) {
            return false;
        }
        if (this.properties != other.properties && (this.properties == null 
                || !this.properties.equals(other.properties))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceEntryConfig{" + "description=" + description 
                + ", properties=" + properties + '}';
    }
}
