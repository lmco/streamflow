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
package streamflow.engine.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import java.io.File;
import java.util.ArrayList;
import streamflow.engine.framework.FrameworkException;
import streamflow.model.Cluster;
import streamflow.model.Topology;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologyConfigProperty;
import streamflow.model.config.StreamflowConfig;
import streamflow.util.environment.StreamflowEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologySubmitter extends Thread {

    protected static final Logger LOG = LoggerFactory.getLogger(TopologySubmitter.class);
    
    private final Topology topology;
    
    private final Cluster targetCluster;
    
    private final LocalCluster localCluster;
    
    private final StreamflowConfig streamflowConfig;

    
    public TopologySubmitter(Topology topology, Cluster targetCluster, 
            LocalCluster localCluster, StreamflowConfig streamflowConfig) {
        this.topology = topology;
        this.targetCluster = targetCluster;
        this.localCluster = localCluster;
        this.streamflowConfig = streamflowConfig;
    }

    @Override
    public void run() {
        try {
            TopologyConfig topologyConfig = topology.getDeployedConfig();

            // Build the storm config using the application and topology configuration
            Config stormConfig = buildStormConfig(
                    streamflowConfig, topologyConfig.getProperties(), isClusterMode(targetCluster));
            
            // Use the factory to get the engine topology for the specified type
            TopologyCreator streamflowTopology = TopologyCreatorFactory.getEngineTopology(topology.getType());

            // Build the Storm topology using the Streamflow topology config as configuration
            StormTopology stormTopology = streamflowTopology.build(
                    topology, streamflowConfig, isClusterMode(targetCluster));

            LOG.info("Submitting Streamflow Topology: Name = " + topology.getName()
                    + ", Cluster ID = " + targetCluster.getId());

            if (isClusterMode(targetCluster)) {
                String topologyJarPath = StreamflowEnvironment.getTopologiesDir() + File.separator
                        + topology.getProjectId() + ".jar";
                
                // Set the required config properties which specify the cluster endpoints
                stormConfig.put(Config.NIMBUS_HOST, targetCluster.getNimbusHost());
                stormConfig.put(Config.NIMBUS_THRIFT_PORT, targetCluster.getNimbusPort());
                
                // StormSubmitter requires that the path to the jar be set as a system property
                System.setProperty("storm.jar", topologyJarPath);
                
                // Submit the topology to the remote cluster using the topology.id for the ID
                StormSubmitter.submitTopology(topology.getId(), stormConfig, stormTopology);
            } else { 
                localCluster.submitTopology(topology.getId(), stormConfig, stormTopology);
            }
        } catch (AlreadyAliveException ex) {
            LOG.error("The specified topology is already running on the cluster: {}", ex);
        } catch (InvalidTopologyException ex) {
            LOG.error("The specified topology is invalid: " + ex);
        } catch (FrameworkException ex) {
            LOG.error("The topology was unable to load a dependent framework: {}", ex);
        } catch (Exception ex) {
            LOG.error("The topology threw an uncaught exception: {}", ex);
        }
    }
    
    private Config buildStormConfig(StreamflowConfig streamflowConfig, 
            ArrayList<TopologyConfigProperty> topologyProperties, boolean isClusterMode) {
        Config stormConfig = new Config();
        
        String proxyHost = streamflowConfig.getProxy().getHost();
        int proxyPort = streamflowConfig.getProxy().getPort();

        if (proxyHost != null && proxyPort > 0) {
            // Add the http proxy information to the config
            stormConfig.put("http.proxy.host", proxyHost);
            stormConfig.put("http.proxy.port", Integer.toString(proxyPort));
        }
        
        // Add each of the user defined properties to the config (may need generic object value and typing system)
        for (TopologyConfigProperty topologyProperty : topologyProperties) {
            stormConfig.put(topologyProperty.getKey(), loadProperty(topologyProperty.getValue()));
        }

        // Kryo Factory is only required for clustered topologies
        if (isClusterMode) {
            // Initialize Storm with the Custom Kryo Factory
            //stormConfig.setKryoFactory(FrameworkKryoFactory.class);
        }
        
        return stormConfig;
    }
    
    private Object loadProperty(String stringValue) {
        Object boundValue;
        
        try {
            boundValue = Long.parseLong(stringValue);
        } catch (NumberFormatException ex1) {
            try {
                boundValue = Double.parseDouble(stringValue);
            } catch (NumberFormatException ex2) {
                try {
                    boundValue = Boolean.parseBoolean(stringValue);
                } catch (NumberFormatException ex3) {
                    boundValue = stringValue;
                }
            }
        }
        
        return boundValue;
    }
    
    private boolean isLocalMode(Cluster cluster) {
        return cluster.getId().equalsIgnoreCase("LOCAL");
    }
    
    private boolean isClusterMode(Cluster cluster) {
        return !isLocalMode(cluster);
    }
}
