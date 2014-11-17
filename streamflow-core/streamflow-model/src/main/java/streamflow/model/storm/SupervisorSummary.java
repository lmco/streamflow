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

public class SupervisorSummary implements Serializable {

    private String host;

    private int numUsedWorkers;

    private int numWorkers;

    private String supervisorId;

    private int uptimeSecs;

    
    public SupervisorSummary() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getNumUsedWorkers() {
        return numUsedWorkers;
    }

    public void setNumUsedWorkers(int numUsedWorkers) {
        this.numUsedWorkers = numUsedWorkers;
    }

    public int getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(int numWorkers) {
        this.numWorkers = numWorkers;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
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
        hash = 67 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 67 * hash + this.numUsedWorkers;
        hash = 67 * hash + this.numWorkers;
        hash = 67 * hash + (this.supervisorId != null ? this.supervisorId.hashCode() : 0);
        hash = 67 * hash + this.uptimeSecs;
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
        final SupervisorSummary other = (SupervisorSummary) obj;
        if ((this.host == null) ? (other.host != null) 
                : !this.host.equals(other.host)) {
            return false;
        }
        if (this.numUsedWorkers != other.numUsedWorkers) {
            return false;
        }
        if (this.numWorkers != other.numWorkers) {
            return false;
        }
        if ((this.supervisorId == null) ? (other.supervisorId != null) 
                : !this.supervisorId.equals(other.supervisorId)) {
            return false;
        }
        if (this.uptimeSecs != other.uptimeSecs) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SupervisorSummary{" + "host=" + host + ", numUsedWorkers=" + numUsedWorkers 
                + ", numWorkers=" + numWorkers + ", supervisorId=" + supervisorId 
                + ", uptimeSecs=" + uptimeSecs + '}';
    }
}
