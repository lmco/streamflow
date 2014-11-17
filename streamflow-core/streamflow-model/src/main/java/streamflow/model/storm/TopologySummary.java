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

public class TopologySummary implements Serializable {

    private String id;

    private String name;

    private int numExecutors;

    private int numTasks;

    private int numWorkers;

    private String status;

    private int uptimeSecs;

    
    public TopologySummary() {
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

    public int getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + this.numExecutors;
        hash = 89 * hash + this.numTasks;
        hash = 89 * hash + this.numWorkers;
        hash = 89 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 89 * hash + this.uptimeSecs;
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
        final TopologySummary other = (TopologySummary) obj;
        if ((this.id == null) ? (other.id != null) : 
                !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : 
                !this.name.equals(other.name)) {
            return false;
        }
        if (this.numExecutors != other.numExecutors) {
            return false;
        }
        if (this.numTasks != other.numTasks) {
            return false;
        }
        if (this.numWorkers != other.numWorkers) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) : 
                !this.status.equals(other.status)) {
            return false;
        }
        if (this.uptimeSecs != other.uptimeSecs) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologySummary{" + "id=" + id + ", name=" + name 
                + ", numExecutors=" + numExecutors + ", numTasks=" + numTasks 
                + ", numWorkers=" + numWorkers + ", status=" + status 
                + ", uptimeSecs=" + uptimeSecs + '}';
    }
}
