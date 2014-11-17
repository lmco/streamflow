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
package streamflow.model.kafka;

import java.util.HashMap;
import java.util.Map;

public class KafkaTopic {

    private String name;
    
    private int numPartitions = 8;
    
    private int replicationFactor = 2;
    
    private Map<String, String> config = new HashMap<String, String>();
    
    public KafkaTopic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumPartitions() {
        return numPartitions;
    }

    public void setNumPartitions(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + this.numPartitions;
        hash = 11 * hash + this.replicationFactor;
        hash = 11 * hash + (this.config != null ? this.config.hashCode() : 0);
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
        final KafkaTopic other = (KafkaTopic) obj;
        if ((this.name == null) ? (other.name != null) : 
                !this.name.equals(other.name)) {
            return false;
        }
        if (this.numPartitions != other.numPartitions) {
            return false;
        }
        if (this.replicationFactor != other.replicationFactor) {
            return false;
        }
        if (this.config != other.config && 
                (this.config == null || !this.config.equals(other.config))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KafkaTopic{" + "name=" + name + ", numPartitions=" + numPartitions 
                + ", replicationFactor=" + replicationFactor + ", config=" + config + '}';
    }
}
