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
public class Cluster implements Serializable {

    private String id;

    private String displayName;

    private String nimbusHost = "localhost";

    private Integer nimbusPort = 6627;
    
    private String version = "0.9.5";

    private String logServerHost = "localhost";
    
    private Integer logServerPort = 9200;

    private String jmsURI;

    public static final String LOCAL = "LOCAL";
    

    public Cluster() {
    }
    
    public Cluster(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Cluster(String id, String displayName, String nimbusHost,
            int nimbusPort, String logServerHost, int logServerPort, String jmsURI) {
        this.id = id;
        this.displayName = displayName;
        this.nimbusHost = nimbusHost;
        this.nimbusPort = nimbusPort;
        this.logServerHost = logServerHost;
        this.logServerPort = logServerPort;
        this.jmsURI = jmsURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNimbusHost() {
        return nimbusHost;
    }

    public void setNimbusHost(String nimbusHost) {
        this.nimbusHost = nimbusHost;
    }

    public Integer getNimbusPort() {
        return nimbusPort;
    }

    public void setNimbusPort(Integer nimbusPort) {
        this.nimbusPort = nimbusPort;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLogServerHost() {
        return logServerHost;
    }

    public void setLogServerHost(String logServerHost) {
        this.logServerHost = logServerHost;
    }

    public int getLogServerPort() {
        return logServerPort;
    }

    public void setLogServerPort(int logServerPort) {
        this.logServerPort = logServerPort;
    }

    public String getJmsURI() {
        return jmsURI;
    }

    public void setJmsURI(String jmsURI) {
        this.jmsURI = jmsURI;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.displayName != null ? this.displayName.hashCode() : 0);
        hash = 79 * hash + (this.nimbusHost != null ? this.nimbusHost.hashCode() : 0);
        hash = 79 * hash + (this.nimbusPort != null ? this.nimbusPort.hashCode() : 0);
        hash = 79 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 79 * hash + (this.logServerHost != null ? this.logServerHost.hashCode() : 0);
        hash = 79 * hash + (this.logServerPort != null ? this.logServerPort.hashCode() : 0);
        hash = 79 * hash + (this.jmsURI != null ? this.jmsURI.hashCode() : 0);
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
        final Cluster other = (Cluster) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.displayName == null) ? (other.displayName != null) 
                : !this.displayName.equals(other.displayName)) {
            return false;
        }
        if ((this.nimbusHost == null) ? (other.nimbusHost != null) 
                : !this.nimbusHost.equals(other.nimbusHost)) {
            return false;
        }
        if ((this.nimbusPort == null) ? (other.nimbusPort != null) 
                : !this.nimbusPort.equals(other.nimbusPort)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) 
                : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.logServerHost == null) ? (other.logServerHost != null) 
                : !this.logServerHost.equals(other.logServerHost)) {
            return false;
        }
        if ((this.logServerPort == null) ? (other.logServerPort != null) 
                : !this.logServerPort.equals(other.logServerPort)) {
            return false;
        }
        if ((this.jmsURI == null) ? (other.jmsURI != null) 
                : !this.jmsURI.equals(other.jmsURI)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cluster{" + "id=" + id + ", displayName=" + displayName 
                + ", nimbusHost=" + nimbusHost + ", nimbusPort=" + nimbusPort 
                + ", version=" + version + ", logServerHost=" + logServerHost 
                + ", logServerPort=" + logServerPort + ", jmsURI=" + jmsURI + "}";
    }
}
