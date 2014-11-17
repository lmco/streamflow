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
import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;

@Embedded
@JsonInclude(Include.NON_NULL)
public class TopologyConnector implements Serializable {

    private String key;

    private String sourceComponentKey;

    private String sourceComponentInterface;

    private String targetComponentKey;

    private String targetComponentInterface;

    private String grouping;

    private String groupingRef;

    
    public TopologyConnector() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSourceComponentKey() {
        return sourceComponentKey;
    }

    public void setSourceComponentKey(String sourceComponentKey) {
        this.sourceComponentKey = sourceComponentKey;
    }

    public String getSourceComponentInterface() {
        return sourceComponentInterface;
    }

    public void setSourceComponentInterface(String sourceComponentInterface) {
        this.sourceComponentInterface = sourceComponentInterface;
    }

    public String getTargetComponentKey() {
        return targetComponentKey;
    }

    public void setTargetComponentKey(String targetComponentKey) {
        this.targetComponentKey = targetComponentKey;
    }

    public String getTargetComponentInterface() {
        return targetComponentInterface;
    }

    public void setTargetComponentInteface(String targetComponentInterface) {
        this.targetComponentInterface = targetComponentInterface;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public String getGroupingRef() {
        return groupingRef;
    }

    public void setGroupingRef(String groupingRef) {
        this.groupingRef = groupingRef;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 67 * hash + (this.sourceComponentKey != null 
                ? this.sourceComponentKey.hashCode() : 0);
        hash = 67 * hash + (this.sourceComponentInterface != null 
                ? this.sourceComponentInterface.hashCode() : 0);
        hash = 67 * hash + (this.targetComponentKey != null 
                ? this.targetComponentKey.hashCode() : 0);
        hash = 67 * hash + (this.targetComponentInterface != null 
                ? this.targetComponentInterface.hashCode() : 0);
        hash = 67 * hash + (this.grouping != null ? this.grouping.hashCode() : 0);
        hash = 67 * hash + (this.groupingRef != null ? this.groupingRef.hashCode() : 0);
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
        final TopologyConnector other = (TopologyConnector) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.sourceComponentKey == null) ? (other.sourceComponentKey != null) 
                : !this.sourceComponentKey.equals(other.sourceComponentKey)) {
            return false;
        }
        if ((this.sourceComponentInterface == null) ? (other.sourceComponentInterface != null) 
                : !this.sourceComponentInterface.equals(other.sourceComponentInterface)) {
            return false;
        }
        if ((this.targetComponentKey == null) ? (other.targetComponentKey != null) 
                : !this.targetComponentKey.equals(other.targetComponentKey)) {
            return false;
        }
        if ((this.targetComponentInterface == null) ? (other.targetComponentInterface != null) 
                : !this.targetComponentInterface.equals(other.targetComponentInterface)) {
            return false;
        }
        if ((this.grouping == null) ? (other.grouping != null) 
                : !this.grouping.equals(other.grouping)) {
            return false;
        }
        if ((this.groupingRef == null) ? (other.groupingRef != null) 
                : !this.groupingRef.equals(other.groupingRef)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyConnector{" + "key=" + key 
                + ", sourceComponentKey=" + sourceComponentKey 
                + ", sourceComponentInterface=" + sourceComponentInterface 
                + ", targetComponentKey=" + targetComponentKey 
                + ", targetComponentInterface=" + targetComponentInterface 
                + ", grouping=" + grouping + ", groupingRef=" + groupingRef + '}';
    }
}
