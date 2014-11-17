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

public class ExecutorSummary implements Serializable {

    private String componentId;

    private String host;

    private int port;

    private int uptimeSecs;

    private ExecutorInfo executorInfo = new ExecutorInfo();

    private ExecutorStats stats = new ExecutorStats();

    
    public ExecutorSummary() {
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUptimeSecs() {
        return uptimeSecs;
    }

    public void setUptimeSecs(int uptimeSecs) {
        this.uptimeSecs = uptimeSecs;
    }

    public ExecutorInfo getExecutorInfo() {
        return executorInfo;
    }

    public void setExecutorInfo(ExecutorInfo executorInfo) {
        this.executorInfo = executorInfo;
    }

    public ExecutorStats getStats() {
        return stats;
    }

    public void setStats(ExecutorStats stats) {
        this.stats = stats;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.componentId != null ? this.componentId.hashCode() : 0);
        hash = 11 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 11 * hash + this.port;
        hash = 11 * hash + this.uptimeSecs;
        hash = 11 * hash + (this.executorInfo != null ? this.executorInfo.hashCode() : 0);
        hash = 11 * hash + (this.stats != null ? this.stats.hashCode() : 0);
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
        final ExecutorSummary other = (ExecutorSummary) obj;
        if ((this.componentId == null) ? (other.componentId != null) 
                : !this.componentId.equals(other.componentId)) {
            return false;
        }
        if ((this.host == null) ? (other.host != null) : !this.host.equals(other.host)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (this.uptimeSecs != other.uptimeSecs) {
            return false;
        }
        if (this.executorInfo != other.executorInfo && (this.executorInfo == null 
                || !this.executorInfo.equals(other.executorInfo))) {
            return false;
        }
        if (this.stats != other.stats && (this.stats == null 
                || !this.stats.equals(other.stats))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExecutorSummary{" + "componentId=" + componentId + ", host=" + host 
                + ", port=" + port + ", uptimeSecs=" + uptimeSecs 
                + ", executorInfo=" + executorInfo + ", stats=" + stats + '}';
    }
}
