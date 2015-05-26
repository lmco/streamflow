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
package streamflow.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import streamflow.engine.StormEngine;
import streamflow.model.Cluster;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.storm.ClusterSummary;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;

@Singleton
public class ClusterService {
    
    private final StormEngine stormEngine;

    private final Map<String, Cluster> clusters = new HashMap<>();

    @Inject
    public ClusterService(StormEngine stormEngine, StreamflowConfig streamflowConfig) {
        this.stormEngine = stormEngine;
        
        // Add each of the clusters from the application configuration
        if (streamflowConfig.getClusters() != null) {
            for (Cluster cluster : streamflowConfig.getClusters()) {
                clusters.put(cluster.getId(), cluster);
            }
        }

        if (streamflowConfig.getLocalCluster().isEnabled()) {
            // Generate the local cluster and add it to the cluster map
            Cluster localCluster = new Cluster(
                    Cluster.LOCAL, "Local", "localhost", 6627, "localhost", 9300, null);
            clusters.put(localCluster.getId(), localCluster);
        }
    }

    public Collection<Cluster> listClusters() {
        return clusters.values();
    }

    public Cluster addCluster(Cluster cluster) {
        if (cluster == null) {
            throw new EntityInvalidException("The provided cluster is NULL");
        }
        if (cluster.getDisplayName() == null || cluster.getNimbusHost() == null) {
            throw new EntityInvalidException("The cluster is missing required fields");
        }
        
        clusters.put(cluster.getId(), cluster);

        return cluster;
    }

    public Cluster getCluster(String clusterId) {
        Cluster cluster = clusters.get(clusterId);
        if (cluster == null) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID not found: ID = " + clusterId);
        }

        return cluster;
    }

    public void deleteCluster(String clusterId) {
        if (!clusters.containsKey(clusterId)) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID not found: ID = " + clusterId);
        }

        clusters.remove(clusterId);
    }

    public void updateCluster(String clusterId, Cluster cluster) {
        if (!clusters.containsKey(clusterId)) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID not found: ID = " + clusterId);
        }

        clusters.put(clusterId, cluster);
    }
    
    public ClusterSummary getClusterSummary(String clusterId) {
        Cluster cluster = getCluster(clusterId);
        
        ClusterSummary clusterSummary = stormEngine.getClusterSummary(cluster);
        if (clusterSummary == null) {
            throw new ServiceException("The specified cluster summary could not be retrieved");
        }
        
        return clusterSummary;
    }
}
