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
import java.util.HashMap;
import java.util.Map;

public class BoltStats implements Serializable {

    private Map<String, Map<String, Long>> acked = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Long>> failed = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Long>> executed = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Double>> executeMsAvg = new HashMap<String, Map<String, Double>>();

    private Map<String, Map<String, Double>> processMsAvg = new HashMap<String, Map<String, Double>>();

    
    public BoltStats() {
    }

    public Map<String, Map<String, Long>> getAcked() {
        return acked;
    }

    public void setAcked(Map<String, Map<String, Long>> acked) {
        this.acked = acked;
    }

    public Map<String, Map<String, Long>> getFailed() {
        return failed;
    }

    public void setFailed(Map<String, Map<String, Long>> failed) {
        this.failed = failed;
    }

    public Map<String, Map<String, Long>> getExecuted() {
        return executed;
    }

    public void setExecuted(Map<String, Map<String, Long>> executed) {
        this.executed = executed;
    }

    public Map<String, Map<String, Double>> getExecuteMsAvg() {
        return executeMsAvg;
    }

    public void setExecuteMsAvg(Map<String, Map<String, Double>> executeMsAvg) {
        this.executeMsAvg = executeMsAvg;
    }

    public Map<String, Map<String, Double>> getProcessMsAvg() {
        return processMsAvg;
    }

    public void setProcessMsAvg(Map<String, Map<String, Double>> processMsAvg) {
        this.processMsAvg = processMsAvg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.acked != null ? this.acked.hashCode() : 0);
        hash = 43 * hash + (this.failed != null ? this.failed.hashCode() : 0);
        hash = 43 * hash + (this.executed != null ? this.executed.hashCode() : 0);
        hash = 43 * hash + (this.executeMsAvg != null ? this.executeMsAvg.hashCode() : 0);
        hash = 43 * hash + (this.processMsAvg != null ? this.processMsAvg.hashCode() : 0);
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
        final BoltStats other = (BoltStats) obj;
        if (this.acked != other.acked && (this.acked == null 
                || !this.acked.equals(other.acked))) {
            return false;
        }
        if (this.failed != other.failed && (this.failed == null 
                || !this.failed.equals(other.failed))) {
            return false;
        }
        if (this.executed != other.executed && (this.executed == null 
                || !this.executed.equals(other.executed))) {
            return false;
        }
        if (this.executeMsAvg != other.executeMsAvg && (this.executeMsAvg == null 
                || !this.executeMsAvg.equals(other.executeMsAvg))) {
            return false;
        }
        if (this.processMsAvg != other.processMsAvg && (this.processMsAvg == null 
                || !this.processMsAvg.equals(other.processMsAvg))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BoltStats{" + "acked=" + acked + ", failed=" + failed + ", executed=" + executed 
                + ", executeMsAvg=" + executeMsAvg + ", processMsAvg=" + processMsAvg + '}';
    }
}
