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

@Entity("topology")
public class Topology implements streamflow.model.util.Entity<String>, Serializable {

    public static final String CLUSTER_MODE = "Cluster";

    public static final String LOCAL_MODE = "Local";

    @Id
    private String id;
            
    private String userId;

    private String name;

    private String description;
    
    private String type;

    private Date created = new Date();

    private Date modified = created;

    private boolean legacy;

    @Embedded
    private TopologyConfig currentConfig;

    @Embedded
    private TopologyConfig deployedConfig;

    private String status = "IDLE";

    // DEPLOYED PROPERTIES
    private String projectId;

    private String clusterId;

    private String clusterName;
    
    private String logLevel;
    
    private String classLoaderPolicy;

    private Date submitted;

    private Date killed;

    public enum TopologyType {
        STORM_STANDARD, 
        STORM_TRIDENT
    }
    
    public enum TopologyMode {
        LOCAL,
        CLUSTER
    }

    
    public Topology() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean getLegacy() {
        return legacy;
    }

    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

    public TopologyConfig getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(TopologyConfig currentConfig) {
        this.currentConfig = currentConfig;
    }

    public TopologyConfig getDeployedConfig() {
        return deployedConfig;
    }

    public void setDeployedConfig(TopologyConfig deployedConfig) {
        this.deployedConfig = deployedConfig;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getClassLoaderPolicy() {
        return classLoaderPolicy;
    }

    public void setClassLoaderPolicy(String classLoaderPolicy) {
        this.classLoaderPolicy = classLoaderPolicy;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public Date getKilled() {
        return killed;
    }

    public void setKilled(Date killed) {
        this.killed = killed;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 41 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 41 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 41 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 41 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 41 * hash + (this.legacy ? 1 : 0);
        hash = 41 * hash + (this.currentConfig != null ? this.currentConfig.hashCode() : 0);
        hash = 41 * hash + (this.deployedConfig != null ? this.deployedConfig.hashCode() : 0);
        hash = 41 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 41 * hash + (this.projectId != null ? this.projectId.hashCode() : 0);
        hash = 41 * hash + (this.clusterId != null ? this.clusterId.hashCode() : 0);
        hash = 41 * hash + (this.clusterName != null ? this.clusterName.hashCode() : 0);
        hash = 41 * hash + (this.logLevel != null ? this.logLevel.hashCode() : 0);
        hash = 41 * hash + (this.classLoaderPolicy != null ? this.classLoaderPolicy.hashCode() : 0);
        hash = 41 * hash + (this.submitted != null ? this.submitted.hashCode() : 0);
        hash = 41 * hash + (this.killed != null ? this.killed.hashCode() : 0);
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
        final Topology other = (Topology) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.userId == null) ? (other.userId != null) 
                : !this.userId.equals(other.userId)) {
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
        if (this.legacy != other.legacy) {
            return false;
        }
        if (this.currentConfig != other.currentConfig && (this.currentConfig == null 
                || !this.currentConfig.equals(other.currentConfig))) {
            return false;
        }
        if (this.deployedConfig != other.deployedConfig && (this.deployedConfig == null 
                || !this.deployedConfig.equals(other.deployedConfig))) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) 
                : !this.status.equals(other.status)) {
            return false;
        }
        if ((this.projectId == null) ? (other.projectId != null) 
                : !this.projectId.equals(other.projectId)) {
            return false;
        }
        if ((this.clusterId == null) ? (other.clusterId != null) 
                : !this.clusterId.equals(other.clusterId)) {
            return false;
        }
        if ((this.clusterName == null) ? (other.clusterName != null) 
                : !this.clusterName.equals(other.clusterName)) {
            return false;
        }
        if ((this.logLevel == null) ? (other.logLevel != null) 
                : !this.logLevel.equals(other.logLevel)) {
            return false;
        }
        if ((this.classLoaderPolicy == null) ? (other.classLoaderPolicy != null) 
                : !this.classLoaderPolicy.equals(other.classLoaderPolicy)) {
            return false;
        }
        if (this.submitted != other.submitted && (this.submitted == null 
                || !this.submitted.equals(other.submitted))) {
            return false;
        }
        if (this.killed != other.killed && (this.killed == null 
                || !this.killed.equals(other.killed))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Topology{" + "id=" + id + ", userId=" + userId + ", name=" + name 
                + ", description=" + description + ", type=" + type + ", created=" + created 
                + ", modified=" + modified + ", legacy=" + legacy
                + ", currentConfig=" + currentConfig + ", deployedConfig=" + deployedConfig 
                + ", status=" + status + ", projectId=" + projectId 
                + ", clusterId=" + clusterId + ", clusterName=" + clusterName 
                + ", logLevel=" + logLevel + ", classLoaderPolicy=" + classLoaderPolicy 
                + ", submitted=" + submitted + ", killed=" + killed + '}';
    }
}
