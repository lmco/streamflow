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

public class ExecutorStats implements Serializable {

    private Map<String, Map<String, Long>> emitted = new HashMap<String, Map<String, Long>>();

    private Map<String, Map<String, Long>> transferred = new HashMap<String, Map<String, Long>>();

    private ExecutorSpecificStats specific = new ExecutorSpecificStats();

    
    public ExecutorStats() {
    }

    public Map<String, Map<String, Long>> getEmitted() {
        return emitted;
    }

    public void setEmitted(Map<String, Map<String, Long>> emitted) {
        this.emitted = emitted;
    }

    public Map<String, Map<String, Long>> getTransferred() {
        return transferred;
    }

    public void setTransferred(Map<String, Map<String, Long>> transferred) {
        this.transferred = transferred;
    }

    public ExecutorSpecificStats getSpecific() {
        return specific;
    }

    public void setSpecific(ExecutorSpecificStats specific) {
        this.specific = specific;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.emitted != null ? this.emitted.hashCode() : 0);
        hash = 31 * hash + (this.transferred != null ? this.transferred.hashCode() : 0);
        hash = 31 * hash + (this.specific != null ? this.specific.hashCode() : 0);
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
        final ExecutorStats other = (ExecutorStats) obj;
        if (this.emitted != other.emitted && 
                (this.emitted == null || !this.emitted.equals(other.emitted))) {
            return false;
        }
        if (this.transferred != other.transferred && 
                (this.transferred == null || !this.transferred.equals(other.transferred))) {
            return false;
        }
        if (this.specific != other.specific && 
                (this.specific == null || !this.specific.equals(other.specific))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExecutorStats{" + "emitted=" + emitted + ", transferred=" + transferred 
                + ", specific=" + specific + '}';
    }
}
