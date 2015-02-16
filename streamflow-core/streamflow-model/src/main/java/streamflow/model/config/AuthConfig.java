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
package streamflow.model.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthConfig implements Serializable {
    
    static Logger LOG = LoggerFactory.getLogger(AuthConfig.class);

    private boolean enabled = false;
    
    private String realmClass = "streamflow.server.security.DatastoreRealm";
    
    private String moduleClass = "streamflow.server.security.DatastoreRealmModule";
    
    private final Map<String, Object> properties = new HashMap<String, Object>();

    
    public AuthConfig() {
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("auth.enabled", Boolean.toString(enabled)));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRealmClass() {
        return System.getProperty("auth.realmClass", realmClass);
    }

    public void setRealmClass(String realmClass) {
        this.realmClass = realmClass;
    }

    public String getModuleClass() {
        return System.getProperty("auth.moduleClass", moduleClass);
    }

    public void setModuleClass(String moduleClass) {
        this.moduleClass = moduleClass;
    }
    
    @JsonAnyGetter
    public Map<String, Object> properties() {
        return properties;
    }
    
    @JsonAnySetter
    public void property(String name, Object value) {
        this.properties.put(name, value);
    }
    
    public <T> T getProperty(String name, Class<T> clazz) {
        T value = null;
        
        // If the system property is found, add it to the properties object
        if (System.getProperties().containsKey("auth." + name)) {
            properties.put(name, System.getProperties().get("auth." + name));
        }
        
        if (properties.containsKey(name)) {
            try {
                // Attempt to type cast the Object to the specified Class
                value = (T) properties.get(name);
            } catch (Exception ex) {
                LOG.error("Unable to bind the property to the provided type: "
                    + "Property = {}, Class = {}, Exception = {}", 
                        name, clazz.getName(), ex.getMessage());
            }
        }
        
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.enabled ? 1 : 0);
        hash = 43 * hash + (this.realmClass != null ? this.realmClass.hashCode() : 0);
        hash = 43 * hash + (this.moduleClass != null ? this.moduleClass.hashCode() : 0);
        hash = 43 * hash + (this.properties != null ? this.properties.hashCode() : 0);
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
        final AuthConfig other = (AuthConfig) obj;
        if (this.enabled != other.enabled) {
            return false;
        }
        if ((this.realmClass == null) ? (other.realmClass != null) 
                : !this.realmClass.equals(other.realmClass)) {
            return false;
        }
        if ((this.moduleClass == null) ? (other.moduleClass != null) 
                : !this.moduleClass.equals(other.moduleClass)) {
            return false;
        }
        if (this.properties != other.properties && 
                (this.properties == null || !this.properties.equals(other.properties))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AuthConfig{" + "enabled=" + enabled + ", realmClass=" + realmClass 
                + ", moduleClass=" + moduleClass + ", properties=" + properties + '}';
    }
}
