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
public class TopologySerialization implements Serializable {

    private String typeClass;

    private String serializerClass;

    private String version;

    private String framework;
    
    private String frameworkHash;
    

    public TopologySerialization() {
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFrameworkHash() {
        return frameworkHash;
    }

    public void setFrameworkHash(String frameworkHash) {
        this.frameworkHash = frameworkHash;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.typeClass != null ? this.typeClass.hashCode() : 0);
        hash = 61 * hash + (this.serializerClass != null ? this.serializerClass.hashCode() : 0);
        hash = 61 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 61 * hash + (this.framework != null ? this.framework.hashCode() : 0);
        hash = 61 * hash + (this.frameworkHash != null ? this.frameworkHash.hashCode() : 0);
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
        final TopologySerialization other = (TopologySerialization) obj;
        if ((this.typeClass == null) ? (other.typeClass != null) 
                : !this.typeClass.equals(other.typeClass)) {
            return false;
        }
        if ((this.serializerClass == null) ? (other.serializerClass != null) 
                : !this.serializerClass.equals(other.serializerClass)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.framework == null) ? (other.framework != null) 
                : !this.framework.equals(other.framework)) {
            return false;
        }
        if ((this.frameworkHash == null) ? (other.frameworkHash != null) 
                : !this.frameworkHash.equals(other.frameworkHash)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologySerialization{" + "typeClass=" + typeClass 
                + ", serializerClass=" + serializerClass + ", version=" + version 
                + ", framework=" + framework + ", frameworkHash=" + frameworkHash + '}';
    }
}
