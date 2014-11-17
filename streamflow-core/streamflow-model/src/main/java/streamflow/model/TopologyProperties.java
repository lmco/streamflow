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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
public class TopologyProperties implements Serializable {
    
    private Boolean debug = false;
    
    private Boolean fallBackOnJavaSerialization = true;
    
    private Integer maxSpoutPending;
    
    private Integer maxTaskParallelism;
    
    private Integer messageTimeoutSecs = 30;
    
    private Integer numAckers;
    
    private Integer numWorkers = 1;
    
    private Boolean skipMissingKryoRegistrations = false;
    
    private Double statsSampleRate = 0.05;
    
    
    public TopologyProperties() {
    }

    public Boolean isDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Boolean isFallBackOnJavaSerialization() {
        return fallBackOnJavaSerialization;
    }

    public void setFallBackOnJavaSerialization(Boolean fallBackOnJavaSerialization) {
        this.fallBackOnJavaSerialization = fallBackOnJavaSerialization;
    }

    public Integer getMaxSpoutPending() {
        return maxSpoutPending;
    }

    public void setMaxSpoutPending(Integer maxSpoutPending) {
        this.maxSpoutPending = maxSpoutPending;
    }

    public Integer getMaxTaskParallelism() {
        return maxTaskParallelism;
    }

    public void setMaxTaskParallelism(Integer maxTaskParallelism) {
        this.maxTaskParallelism = maxTaskParallelism;
    }

    public Integer getMessageTimeoutSecs() {
        return messageTimeoutSecs;
    }

    public void setMessageTimeoutSecs(Integer messageTimeoutSecs) {
        this.messageTimeoutSecs = messageTimeoutSecs;
    }

    public Integer getNumAckers() {
        return numAckers;
    }

    public void setNumAckers(Integer numAckers) {
        this.numAckers = numAckers;
    }

    public Integer getNumWorkers() {
        return numWorkers;
    }

    public void setNumWorkers(Integer numWorkers) {
        this.numWorkers = numWorkers;
    }

    public Boolean isSkipMissingKryoRegistrations() {
        return skipMissingKryoRegistrations;
    }

    public void setSkipMissingKryoRegistrations(Boolean skipMissingKryoRegistrations) {
        this.skipMissingKryoRegistrations = skipMissingKryoRegistrations;
    }

    public Double getStatsSampleRate() {
        return statsSampleRate;
    }

    public void setStatsSampleRate(Double statsSampleRate) {
        this.statsSampleRate = statsSampleRate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.debug != null ? this.debug.hashCode() : 0);
        hash = 83 * hash + (this.fallBackOnJavaSerialization != null 
                ? this.fallBackOnJavaSerialization.hashCode() : 0);
        hash = 83 * hash + (this.maxSpoutPending != null 
                ? this.maxSpoutPending.hashCode() : 0);
        hash = 83 * hash + (this.maxTaskParallelism != null 
                ? this.maxTaskParallelism.hashCode() : 0);
        hash = 83 * hash + (this.messageTimeoutSecs != null 
                ? this.messageTimeoutSecs.hashCode() : 0);
        hash = 83 * hash + (this.numAckers != null ? this.numAckers.hashCode() : 0);
        hash = 83 * hash + (this.numWorkers != null ? this.numWorkers.hashCode() : 0);
        hash = 83 * hash + (this.skipMissingKryoRegistrations != null 
                ? this.skipMissingKryoRegistrations.hashCode() : 0);
        hash = 83 * hash + (this.statsSampleRate != null ? this.statsSampleRate.hashCode() : 0);
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
        final TopologyProperties other = (TopologyProperties) obj;
        if (this.debug != other.debug && (this.debug == null || !this.debug.equals(other.debug))) {
            return false;
        }
        if (this.fallBackOnJavaSerialization != other.fallBackOnJavaSerialization 
                && (this.fallBackOnJavaSerialization == null 
                || !this.fallBackOnJavaSerialization.equals(other.fallBackOnJavaSerialization))) {
            return false;
        }
        if (this.maxSpoutPending != other.maxSpoutPending && (this.maxSpoutPending == null 
                || !this.maxSpoutPending.equals(other.maxSpoutPending))) {
            return false;
        }
        if (this.maxTaskParallelism != other.maxTaskParallelism && (this.maxTaskParallelism == null 
                || !this.maxTaskParallelism.equals(other.maxTaskParallelism))) {
            return false;
        }
        if (this.messageTimeoutSecs != other.messageTimeoutSecs && (this.messageTimeoutSecs == null 
                || !this.messageTimeoutSecs.equals(other.messageTimeoutSecs))) {
            return false;
        }
        if (this.numAckers != other.numAckers && (this.numAckers == null 
                || !this.numAckers.equals(other.numAckers))) {
            return false;
        }
        if (this.numWorkers != other.numWorkers && (this.numWorkers == null 
                || !this.numWorkers.equals(other.numWorkers))) {
            return false;
        }
        if (this.skipMissingKryoRegistrations != other.skipMissingKryoRegistrations 
                && (this.skipMissingKryoRegistrations == null 
                || !this.skipMissingKryoRegistrations.equals(other.skipMissingKryoRegistrations))) {
            return false;
        }
        if (this.statsSampleRate != other.statsSampleRate && (this.statsSampleRate == null 
                || !this.statsSampleRate.equals(other.statsSampleRate))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyProperties{" + "debug=" + debug 
                + ", fallBackOnJavaSerialization=" + fallBackOnJavaSerialization 
                + ", maxSpoutPending=" + maxSpoutPending 
                + ", maxTaskParallelism=" + maxTaskParallelism 
                + ", messageTimeoutSecs=" + messageTimeoutSecs 
                + ", numAckers=" + numAckers + ", numWorkers=" + numWorkers 
                + ", skipMissingKryoRegistrations=" + skipMissingKryoRegistrations 
                + ", statsSampleRate=" + statsSampleRate + '}';
    }
}
