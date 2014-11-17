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

public class ExecutorSpecificStats implements Serializable {

    private BoltStats bolt = new BoltStats();

    private SpoutStats spout = new SpoutStats();

    
    public ExecutorSpecificStats() {
    }

    public BoltStats getBolt() {
        return bolt;
    }

    public void setBolt(BoltStats bolt) {
        this.bolt = bolt;
    }

    public SpoutStats getSpout() {
        return spout;
    }

    public void setSpout(SpoutStats spout) {
        this.spout = spout;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.bolt != null ? this.bolt.hashCode() : 0);
        hash = 19 * hash + (this.spout != null ? this.spout.hashCode() : 0);
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
        final ExecutorSpecificStats other = (ExecutorSpecificStats) obj;
        if (this.bolt != other.bolt && (this.bolt == null || !this.bolt.equals(other.bolt))) {
            return false;
        }
        if (this.spout != other.spout && (this.spout == null || !this.spout.equals(other.spout))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExecutorSpecificStats{" + "bolt=" + bolt + ", spout=" + spout + '}';
    }
}
