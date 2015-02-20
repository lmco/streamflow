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
import java.util.ArrayList;
import java.util.HashMap;

@Embedded
@JsonInclude(Include.NON_NULL)
public class TopologyComponent implements Serializable {

    private String key;

    private String type;

    private String label;

    private String name;

    private String framework;
    
    private String frameworkHash;

    private String version;

    private String mainClass;

    private int parallelism;

    private double posX;

    private double posY;

    @Embedded
    private HashMap<String, String> properties = new HashMap<String, String>();

    @Embedded
    private HashMap<String, String> propertyTypes = new HashMap<String, String>();

    @Embedded
    private ArrayList<TopologyResourceEntry> resources = new ArrayList<TopologyResourceEntry>();
    
    @Embedded
    private TopologyFields fields = new TopologyFields();

    
    public TopologyComponent() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
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

    public ArrayList<TopologyResourceEntry> getResources() {
        return resources;
    }

    public void setResources(ArrayList<TopologyResourceEntry> resources) {
        this.resources = resources;
    }

    public TopologyFields getFields() {
        return fields;
    }

    public void setFields(TopologyFields fields) {
        this.fields = fields;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 11 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 11 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 11 * hash + (this.frameworkHash != null ? this.frameworkHash.hashCode() : 0);
        hash = 11 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 11 * hash + (this.mainClass != null ? this.mainClass.hashCode() : 0);
        hash = 11 * hash + this.parallelism;
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.posX) 
                ^ (Double.doubleToLongBits(this.posX) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.posY) 
                ^ (Double.doubleToLongBits(this.posY) >>> 32));
        hash = 11 * hash + (this.properties != null ? this.properties.hashCode() : 0);
        hash = 11 * hash + (this.propertyTypes != null ? this.propertyTypes.hashCode() : 0);
        hash = 11 * hash + (this.resources != null ? this.resources.hashCode() : 0);
        hash = 11 * hash + (this.fields != null ? this.fields.hashCode() : 0);
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
        final TopologyComponent other = (TopologyComponent) obj;
        if ((this.key == null) ? (other.key != null) 
                : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) 
                : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) 
                : !this.label.equals(other.label)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
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
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.mainClass == null) ? (other.mainClass != null) 
                : !this.mainClass.equals(other.mainClass)) {
            return false;
        }
        if (this.parallelism != other.parallelism) {
            return false;
        }
        if (Double.doubleToLongBits(this.posX) != Double.doubleToLongBits(other.posX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.posY) != Double.doubleToLongBits(other.posY)) {
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
        if (this.resources != other.resources && (this.resources == null 
                || !this.resources.equals(other.resources))) {
            return false;
        }
        if (this.fields != other.fields && (this.fields == null 
                || !this.fields.equals(other.fields))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyComponent{" + "key=" + key + ", type=" + type 
                + ", label=" + label + ", name=" + name + ", framework=" + framework 
                + ", frameworkHash=" + frameworkHash + ", version=" + version 
                + ", mainClass=" + mainClass + ", parallelism=" + parallelism 
                + ", posX=" + posX + ", posY=" + posY + ", properties=" + properties 
                + ", propertyTypes=" + propertyTypes + ", resources=" + resources 
                + ", fields=" + fields + '}';
    }
}
