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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import streamflow.model.Cluster;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamflowConfig implements Serializable {
    
    private ServerConfig server = new ServerConfig();

    private ProxyConfig proxy = new ProxyConfig();

    private DatastoreConfig datastore = new DatastoreConfig();

    private LoggerConfig logger = new LoggerConfig();

    private AuthConfig auth = new AuthConfig();

    private MonitorConfig monitor = new MonitorConfig();

    private LocalClusterConfig localCluster = new LocalClusterConfig();

    private List<Cluster> clusters = new ArrayList<>();

    private Cluster selectedCluster;

    public StreamflowConfig() {
    }
    
    public ServerConfig getServer() {
        return server;
    }
    
    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public ProxyConfig getProxy() {
        return proxy;
    }

    public void setProxy(ProxyConfig proxy) {
        this.proxy = proxy;
    }

    public DatastoreConfig getDatastore() {
        return datastore;
    }

    public void setDatastore(DatastoreConfig datastore) {
        this.datastore = datastore;
    }

    public LoggerConfig getLogger() {
        return logger;
    }

    public void setLogger(LoggerConfig logger) {
        this.logger = logger;
    }

    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }

    public LocalClusterConfig getLocalCluster() {
        return localCluster;
    }

    public void setLocalCluster(LocalClusterConfig localCluster) {
        this.localCluster = localCluster;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }

    public Cluster getSelectedCluster() {
        return selectedCluster;
    }

    public void setSelectedCluster(Cluster selectedCluster) {
        this.selectedCluster = selectedCluster;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.server != null ? this.server.hashCode() : 0);
        hash = 29 * hash + (this.proxy != null ? this.proxy.hashCode() : 0);
        hash = 29 * hash + (this.datastore != null ? this.datastore.hashCode() : 0);
        hash = 29 * hash + (this.logger != null ? this.logger.hashCode() : 0);
        hash = 29 * hash + (this.auth != null ? this.auth.hashCode() : 0);
        hash = 29 * hash + (this.monitor != null ? this.monitor.hashCode() : 0);
        hash = 29 * hash + (this.localCluster != null ? this.localCluster.hashCode() : 0);
        hash = 29 * hash + (this.clusters != null ? this.clusters.hashCode() : 0);
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
        final StreamflowConfig other = (StreamflowConfig) obj;
        if (this.server != other.server && (this.server == null 
                || !this.server.equals(other.server))) {
            return false;
        }
        if (this.proxy != other.proxy && (this.proxy == null 
                || !this.proxy.equals(other.proxy))) {
            return false;
        }
        if (this.datastore != other.datastore && (this.datastore == null 
                || !this.datastore.equals(other.datastore))) {
            return false;
        }
        if (this.logger != other.logger && (this.logger == null 
                || !this.logger.equals(other.logger))) {
            return false;
        }
        if (this.auth != other.auth && (this.auth == null 
                || !this.auth.equals(other.auth))) {
            return false;
        }
        if (this.monitor != other.monitor && (this.monitor == null
                || !this.monitor.equals(other.monitor))) {
            return false;
        }
        if (this.localCluster != other.localCluster && (this.localCluster == null 
                || !this.localCluster.equals(other.localCluster))) {
            return false;
        }
        if (this.clusters != other.clusters && (this.clusters == null 
                || !this.clusters.equals(other.clusters))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StreamFlowConfig{" + "server=" + server + ", proxy=" + proxy 
                + ", datastore=" + datastore + ", logger=" + logger 
                + ", auth=" + auth + ", monitor=" + monitor + ", localCluster=" + localCluster
                + ", clusters=" + clusters + '}';
    }
}
