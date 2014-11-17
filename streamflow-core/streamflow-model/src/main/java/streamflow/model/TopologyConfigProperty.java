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

import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;

@Embedded
public class TopologyConfigProperty implements Serializable {

    private String key;

    private String value;

    
    public TopologyConfigProperty() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
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
        final TopologyConfigProperty other = (TopologyConfigProperty) obj;
        if ((this.key == null) ? (other.key != null) 
                : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) 
                : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyConfigProperty{" + "key=" + key + ", value=" + value + '}';
    }
}
