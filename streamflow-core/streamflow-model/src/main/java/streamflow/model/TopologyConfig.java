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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Embedded
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopologyConfig implements Serializable {

    @Embedded
    private HashMap<String, TopologyComponent> components = 
            new HashMap<String, TopologyComponent>();

    @Embedded
    private HashMap<String, TopologyConnector> connectors = 
            new HashMap<String, TopologyConnector>();

    @Embedded
    private ArrayList<TopologySerialization> serializations = 
            new ArrayList<TopologySerialization>();
    
    @Embedded
    private ArrayList<TopologyConfigProperty> properties = 
            new ArrayList<TopologyConfigProperty>();

    
    public TopologyConfig() {
    }

    public HashMap<String, TopologyComponent> getComponents() {
        return components;
    }

    public void setComponents(HashMap<String, TopologyComponent> components) {
        this.components = components;
    }

    public HashMap<String, TopologyConnector> getConnectors() {
        return connectors;
    }

    public void setConnectors(HashMap<String, TopologyConnector> connectors) {
        this.connectors = connectors;
    }

    public ArrayList<TopologySerialization> getSerializations() {
        return serializations;
    }

    public void setSerializations(ArrayList<TopologySerialization> serializations) {
        this.serializations = serializations;
    }

    public ArrayList<TopologyConfigProperty> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<TopologyConfigProperty> properties) {
        this.properties = properties;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.components != null ? this.components.hashCode() : 0);
        hash = 97 * hash + (this.connectors != null ? this.connectors.hashCode() : 0);
        hash = 97 * hash + (this.serializations != null ? this.serializations.hashCode() : 0);
        hash = 97 * hash + (this.properties != null ? this.properties.hashCode() : 0);
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
        final TopologyConfig other = (TopologyConfig) obj;
        if (this.components != other.components && (this.components == null 
                || !this.components.equals(other.components))) {
            return false;
        }
        if (this.connectors != other.connectors && (this.connectors == null 
                || !this.connectors.equals(other.connectors))) {
            return false;
        }
        if (this.serializations != other.serializations && (this.serializations == null 
                || !this.serializations.equals(other.serializations))) {
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
        return "TopologyConfig{" + "components=" + components + ", connectors=" + connectors 
                + ", serializations=" + serializations + ", properties=" + properties + '}';
    }
}
