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

import java.io.Serializable;

public class LocalClusterConfig implements Serializable {

    private boolean enabled = true;

    public LocalClusterConfig() {
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(System.getProperty("localCluster.enabled", Boolean.toString(enabled)));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.enabled ? 1 : 0);
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
        final LocalClusterConfig other = (LocalClusterConfig) obj;
        if (this.enabled != other.enabled) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LocalClusterConfig{" + "enabled=" + enabled + '}';
    }
}
