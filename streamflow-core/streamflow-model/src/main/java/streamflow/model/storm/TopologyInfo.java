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
package streamflow.model.storm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopologyInfo implements Serializable {

    private String id;

    private String name;

    private String status;

    private int uptimeSecs;

    private String topologyConf;

    private Map<String, List<ErrorInfo>> errors = new HashMap<String, List<ErrorInfo>>();

    private List<ExecutorSummary> executors = new ArrayList<ExecutorSummary>();

    
    public TopologyInfo() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUptimeSecs() {
        return uptimeSecs;
    }

    public void setUptimeSecs(int uptimeSecs) {
        this.uptimeSecs = uptimeSecs;
    }

    public String getTopologyConf() {
        return topologyConf;
    }

    public void setTopologyConf(String topologyConf) {
        this.topologyConf = topologyConf;
    }

    public Map<String, List<ErrorInfo>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<ErrorInfo>> errors) {
        this.errors = errors;
    }

    public List<ExecutorSummary> getExecutors() {
        return executors;
    }

    public void setExecutors(List<ExecutorSummary> executors) {
        this.executors = executors;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 71 * hash + this.uptimeSecs;
        hash = 71 * hash + (this.topologyConf != null ? this.topologyConf.hashCode() : 0);
        hash = 71 * hash + (this.errors != null ? this.errors.hashCode() : 0);
        hash = 71 * hash + (this.executors != null ? this.executors.hashCode() : 0);
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
        final TopologyInfo other = (TopologyInfo) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) 
                : !this.status.equals(other.status)) {
            return false;
        }
        if (this.uptimeSecs != other.uptimeSecs) {
            return false;
        }
        if ((this.topologyConf == null) ? (other.topologyConf != null) 
                : !this.topologyConf.equals(other.topologyConf)) {
            return false;
        }
        if (this.errors != other.errors && (this.errors == null 
                || !this.errors.equals(other.errors))) {
            return false;
        }
        if (this.executors != other.executors && (this.executors == null 
                || !this.executors.equals(other.executors))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyInfo{" + "id=" + id + ", name=" + name + ", status=" + status 
                + ", uptimeSecs=" + uptimeSecs + ", topologyConf=" + topologyConf 
                + ", errors=" + errors + ", executors=" + executors + '}';
    }
}
