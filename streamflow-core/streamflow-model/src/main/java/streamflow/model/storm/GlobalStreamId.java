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

public class GlobalStreamId implements Serializable {

    private String componentId;

    private String streamId;

    
    public GlobalStreamId() {
    }

    public GlobalStreamId(String componentId, String streamId) {
        this.componentId = componentId;
        this.streamId = streamId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.componentId != null ? this.componentId.hashCode() : 0);
        hash = 73 * hash + (this.streamId != null ? this.streamId.hashCode() : 0);
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
        final GlobalStreamId other = (GlobalStreamId) obj;
        if ((this.componentId == null) ? (other.componentId != null) : 
                !this.componentId.equals(other.componentId)) {
            return false;
        }
        if ((this.streamId == null) ? (other.streamId != null) : 
                !this.streamId.equals(other.streamId)) {
            return false;
        }
        return true;
    }

    public int compareTo(GlobalStreamId other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int comparison = Boolean.valueOf(this.componentId != null).compareTo(other.componentId != null);
        if (comparison != 0) {
            return comparison;
        }
        if (this.componentId != null) {
            comparison = this.componentId.compareTo(other.componentId);
            if (comparison != 0) {
                return comparison;
            }
        }

        comparison = Boolean.valueOf(this.streamId != null).compareTo(other.streamId != null);
        if (comparison != 0) {
            return comparison;
        }
        if (this.streamId != null) {
            comparison = this.streamId.compareTo(other.streamId);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
}
