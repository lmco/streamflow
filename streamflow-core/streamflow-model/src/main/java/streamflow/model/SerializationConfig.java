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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SerializationConfig implements Serializable {

    private String typeClass;

    private String serializerClass;

    
    public SerializationConfig() {
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(String serializerClass) {
        this.serializerClass = serializerClass;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.typeClass != null ? this.typeClass.hashCode() : 0);
        hash = 53 * hash + (this.serializerClass != null ? this.serializerClass.hashCode() : 0);
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
        final SerializationConfig other = (SerializationConfig) obj;
        if ((this.typeClass == null) ? (other.typeClass != null) 
                : !this.typeClass.equals(other.typeClass)) {
            return false;
        }
        if ((this.serializerClass == null) ? (other.serializerClass != null) 
                : !this.serializerClass.equals(other.serializerClass)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SerializationConfig{" + "typeClass=" + typeClass 
                + ", serializerClass=" + serializerClass + '}';
    }
}
