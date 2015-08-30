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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MonitorConfig implements Serializable {

    static Logger LOG = LoggerFactory.getLogger(MonitorConfig.class);

    private boolean enabled = false;

    private int pollingInterval = 60;

    public MonitorConfig() {
    }

    public boolean isEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("monitor.enabled", Boolean.toString(enabled)));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPollingInterval() {
        if (System.getProperty("monitor.pollingInterval") != null) {
            try {
                pollingInterval = Integer.parseInt(System.getProperty("monitor.pollingInterval"));
            } catch (Exception ex) {
            }
        }
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.enabled ? 1 : 0);
        hash = 43 * hash + this.pollingInterval;
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
        final MonitorConfig other = (MonitorConfig) obj;
        if (this.enabled != other.enabled) {
            return false;
        }
        if (this.pollingInterval != other.pollingInterval) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MonitorConfig{" + "enabled=" + enabled + ", pollingInterval=" + pollingInterval + '}';
    }
}
