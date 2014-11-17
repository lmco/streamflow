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

@Entity("component")
public class Component implements streamflow.model.util.Entity<String>, Serializable {

    // Standard Storm Types
    public static final String STORM_SPOUT_TYPE = "storm-spout";
    public static final String STORM_BOLT_TYPE = "storm-bolt";
    public static final String STORM_RESOURCE_TYPE = "storm-resource";
   
    // Trident Spout Types
    public static final String TRIDENT_SPOUT_TYPE = "trident-spout";
    public static final String TRIDENT_BATCH_SPOUT_TYPE = "trident-batch-spout";
    public static final String TRIDENT_PARTITIONED_SPOUT_TYPE = "trident-partitioned-spout";
    public static final String TRIDENT_OPAQUE_PARTITIONED_SPOUT_TYPE = "trident-opaque-partitioned-spout";
    
    // Trident Function and Filter Types
    public static final String TRIDENT_FUNCTION_TYPE = "trident-function";
    public static final String TRIDENT_FILTER_TYPE = "trident-filter";
    
    // Trident Aggregator Types
    public static final String TRIDENT_AGGREGATOR_TYPE = "trident-aggregator";
    public static final String TRIDENT_COMBINER_AGGREGATOR_TYPE = "trident-combiner-aggregator";
    public static final String TRIDENT_REDUCER_AGGREGATOR_TYPE = "trident-reducer-aggregator";
    
    // Trident Persistent State Types
    
    // Trident Non-Implemented Types
    public static final String TRIDENT_DRPC_TYPE = "trident-drpc";
    public static final String TRIDENT_PROJECT_TYPE = "trident-project";
    public static final String TRIDENT_MERGE_TYPE = "trident-merge";
    public static final String TRIDENT_JOIN_TYPE = "trident-join";
    public static final String TRIDENT_CHAINED_AGG_START_TYPE = "trident-chained-agg";
    public static final String TRIDENT_CHAINED_AGG_END_TYPE = "trident-chain-end";
    public static final String TRIDENT_AGG_PARTITION_TYPE = "trident-agg-partition";

    @Id
    private String id;

    private String name;

    private String version;

    private String framework;

    private String frameworkLabel;

    private String label;

    private String type;

    private Date created;

    private Date modified;
    
    private String iconId;

    @Embedded
    private ComponentConfig config;
    
    
    public enum ComponentType {
        // Standard Components
        STORM_SPOUT, STORM_BOLT, STORM_RESOURCE,

        // Trident Spouts
        TRIDENT_SPOUT, TRIDENT_BATCH_SPOUT, TRIDENT_PARTITIONED_SPOUT, TRIDENT_OPAQUE_PARTITIONED_SPOUT,

        // Trident Functions/Filters
        TRIDENT_FUNCTION, TRIDENT_FILTER,

        // Trident Combiner/Reducer/Aggregator
        TRIDENT_AGGREGATOR, TRIDENT_COMBINER_AGGREGATOR, TRIDENT_REDUCER_AGGREGATOR,

        // Trident Non-Implementable Types
        TRIDENT_DRPC, TRIDENT_PROJECT, TRIDENT_MERGE, TRIDENT_JOIN,
        TRIDENT_CHAINED_AGG_START, TRIDENT_CHAINED_AGG_END, TRIDENT_AGG_PARTITION
    }

    
    public Component() {
        this.created = new Date();
        this.modified = this.created;
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

    public ComponentConfig getConfig() {
        return config;
    }

    public void setConfig(ComponentConfig config) {
        this.config = config;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 97 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 97 * hash + (this.frameworkLabel != null ? this.frameworkLabel.hashCode() : 0);
        hash = 97 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 97 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 97 * hash + (this.iconId != null ? this.iconId.hashCode() : 0);
        hash = 97 * hash + (this.config != null ? this.config.hashCode() : 0);
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
        final Component other = (Component) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
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
        if ((this.frameworkLabel == null) ? (other.frameworkLabel != null) 
                : !this.frameworkLabel.equals(other.frameworkLabel)) {
            return false;
        }
        if ((this.label == null) ? (other.label != null) 
                : !this.label.equals(other.label)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) 
                : !this.type.equals(other.type)) {
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
        if ((this.iconId == null) ? (other.iconId != null) 
                : !this.iconId.equals(other.iconId)) {
            return false;
        }
        if (this.config != other.config 
                && (this.config == null || !this.config.equals(other.config))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Component{" + "id=" + id + ", name=" + name + ", version=" + version 
                + ", framework=" + framework + ", frameworkLabel=" + frameworkLabel 
                + ", label=" + label + ", type=" + type + ", created=" + created 
                + ", modified=" + modified + ", iconId=" + iconId + ", config=" + config + "}";
    }
}
