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

public class SpoutStats implements Serializable {

    private Map<String, Map<String, Long>> acked = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Long>> failed = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Double>> completeMsAvg = new HashMap<String, Map<String, Double>>();

    
    public SpoutStats() {
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

    public Map<String, Map<String, Double>> getCompleteMsAvg() {
        return completeMsAvg;
    }

    public void setCompleteMsAvg(Map<String, Map<String, Double>> completeMsAvg) {
        this.completeMsAvg = completeMsAvg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.acked != null ? this.acked.hashCode() : 0);
        hash = 23 * hash + (this.failed != null ? this.failed.hashCode() : 0);
        hash = 23 * hash + (this.completeMsAvg != null ? this.completeMsAvg.hashCode() : 0);
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
        final SpoutStats other = (SpoutStats) obj;
        if (this.acked != other.acked && (this.acked == null 
                || !this.acked.equals(other.acked))) {
            return false;
        }
        if (this.failed != other.failed && (this.failed == null 
                || !this.failed.equals(other.failed))) {
            return false;
        }
        if (this.completeMsAvg != other.completeMsAvg && (this.completeMsAvg == null 
                || !this.completeMsAvg.equals(other.completeMsAvg))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SpoutStats{" + "acked=" + acked + ", failed=" + failed 
                + ", completeMsAvg=" + completeMsAvg + '}';
    }
}
