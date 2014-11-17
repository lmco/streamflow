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
package streamflow.engine;

import backtype.storm.LocalCluster;
import backtype.storm.generated.KillOptions;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.NotAliveException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import streamflow.engine.topology.TopologySubmitter;
import streamflow.model.Cluster;
import streamflow.model.Topology;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.storm.BoltStats;
import streamflow.model.storm.ClusterSummary;
import streamflow.model.storm.ErrorInfo;
import streamflow.model.storm.ExecutorInfo;
import streamflow.model.storm.ExecutorSpecificStats;
import streamflow.model.storm.ExecutorStats;
import streamflow.model.storm.ExecutorSummary;
import streamflow.model.storm.SpoutStats;
import streamflow.model.storm.SupervisorSummary;
import streamflow.model.storm.TopologyInfo;
import streamflow.model.storm.TopologySummary;
import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.protocol.TProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class StormEngine {

    protected static final Logger LOG = LoggerFactory.getLogger(StormEngine.class);
    
    private final LocalCluster stormCluster;
    
    private final StreamflowConfig streamflowConfig;
    
    private final HashMap<String, Cluster> clusters = new HashMap<String, Cluster>();
    
    @Inject
    public StormEngine(LocalCluster stormCluster, StreamflowConfig streamflowConfig) {
        this.stormCluster = stormCluster;
        this.streamflowConfig = streamflowConfig;
        
        // Add each of the clusters from the application configuration
        if (streamflowConfig.getClusters() != null) {
            for (Cluster cluster : streamflowConfig.getClusters()) {
                clusters.put(cluster.getId(), cluster);
            }
        }

        // Manually add the local cluster and add it to the cluster map
        Cluster localCluster = new Cluster(
                Cluster.LOCAL, "Local", "localhost", 6627, "localhost", 9300, null);
        clusters.put(localCluster.getId(), localCluster);
    }
    
    public Topology submitTopology(Topology topology, Cluster cluster) {
        // Execute topology submission in a thread to maintain separate context class loader for each topology
        TopologySubmitter submitter = new TopologySubmitter(
            topology, cluster, stormCluster, streamflowConfig);
        submitter.start();
        
        try {
            // Wait for the topology to be fully submitted before continuing
            submitter.join();
        } catch (InterruptedException ex) {
            LOG.error("Topology submission aborted: {}", ex.getMessage());
            
            topology = null;
        }
        
        return topology;
    }
    
    public boolean killTopology(Topology topology, int waitTimeSecs) {
        boolean killed = true;
        
        if (isDeployed(topology)) {
            try {
                KillOptions killOptions = new KillOptions();
                killOptions.set_wait_secs(waitTimeSecs);
                
                if (isLocal(topology.getClusterId())) {
                    // Kill the topology on the local cluster
                    stormCluster.killTopologyWithOpts(topology.getId(), killOptions);
                } else {
                    Cluster cluster = clusters.get(topology.getClusterId());

                    TSocket tsocket = new TSocket(cluster.getNimbusHost(), cluster.getNimbusPort());
                    TFramedTransport tTransport = new TFramedTransport(tsocket);
                    TProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
                    Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);
                    tTransport.open();

                    client.killTopologyWithOpts(topology.getId(), killOptions);
                }
                
                // Check for final removal of topology 60 times (60 seconds)
                killed = waitForTopologyRemoval(topology, 60);
                
            } catch (Exception ex) {
                LOG.error("Exception occurred while killing the remote topology: ID = " +
                                topology.getId() + ", Reason = " + ex.getMessage());

                killed = false;
            } 
        }
        
        return killed;
    }
    
    public ClusterSummary getClusterSummary(Cluster cluster) {
        backtype.storm.generated.ClusterSummary summary = null;
        String nimbusConf = null;
        
        if (cluster != null) {
            if (isLocal(cluster.getId())) {
                summary = stormCluster.getClusterInfo();
            } else {
                TSocket tsocket = new TSocket(cluster.getNimbusHost(), cluster.getNimbusPort());
                TFramedTransport tTransport = new TFramedTransport(tsocket);

                try {
                    TProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
                    Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);
                    tTransport.open();

                    summary = client.getClusterInfo();
                    nimbusConf = client.getNimbusConf();
                } catch (Exception ex) {
                    LOG.error("Exception while retrieving cluster summary: {}", ex.getMessage());
                } finally {
                    tTransport.close();
                }
            }
        }
        
        ClusterSummary clusterSummary = null;

        if (summary != null) {
            clusterSummary = new ClusterSummary();
            
            clusterSummary.setNimbusUptimeSecs(summary.get_nimbus_uptime_secs());
            clusterSummary.setNimbusConf(nimbusConf);

            List<SupervisorSummary> supervisors = new ArrayList<SupervisorSummary>();
            for (backtype.storm.generated.SupervisorSummary ss : summary.get_supervisors()) {
                SupervisorSummary supervisor = new SupervisorSummary();
                supervisor.setHost(ss.get_host());
                supervisor.setSupervisorId(ss.get_supervisor_id());
                supervisor.setNumUsedWorkers(ss.get_num_used_workers());
                supervisor.setNumWorkers(ss.get_num_workers());
                supervisor.setUptimeSecs(ss.get_uptime_secs());

                supervisors.add(supervisor);
            }
            clusterSummary.setSupervisors(supervisors);

            List<TopologySummary> topologies = new ArrayList<TopologySummary>();
            for (backtype.storm.generated.TopologySummary ts : summary.get_topologies()) {
                TopologySummary topology = new TopologySummary();
                topology.setId(ts.get_id());
                topology.setName(ts.get_name());
                topology.setStatus(ts.get_status());
                topology.setUptimeSecs(ts.get_uptime_secs());
                topology.setNumExecutors(ts.get_num_executors());
                topology.setNumWorkers(ts.get_num_workers());
                topology.setNumTasks(ts.get_num_tasks());

                topologies.add(topology);
            }
            clusterSummary.setTopologies(topologies);
        }

        return clusterSummary;
    }
    
    public TopologyInfo getTopologyInfo(Topology topology) {
        backtype.storm.generated.TopologyInfo info = null;
        String topologyConf = null;
        
        // If the topology shouldn't be deployed, no need to query the cluster
        if (!isDeployed(topology)) {
            TopologyInfo topologyInfo = new TopologyInfo();
            topologyInfo.setName(topology.getName());
            topologyInfo.setStatus("IDLE");
            
            return topologyInfo;
        }
        
        // Convert the topology ID of the jetstram topology to the id recognized by Storm
        String stormTopologyId = resolveStormTopologyId(topology);
        
        // The topology should be running, but found no matching name. Topology must have been killed
        if (stormTopologyId == null) {
            TopologyInfo topologyInfo = new TopologyInfo();
            topologyInfo.setName(topology.getName());
            topologyInfo.setStatus("KILLED");
            
            return topologyInfo;
        }
        
        if (isLocal(topology.getClusterId())) {
            info = stormCluster.getTopologyInfo(stormTopologyId);
            topologyConf = stormCluster.getTopologyConf(stormTopologyId);
        } else {
            Cluster cluster = clusters.get(topology.getClusterId());
            
            TSocket tsocket = new TSocket(cluster.getNimbusHost(), cluster.getNimbusPort());
            TFramedTransport tTransport = new TFramedTransport(tsocket);

            try {
                TProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
                Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);
                tTransport.open();
                
                info = client.getTopologyInfo(stormTopologyId);
                topologyConf = client.getTopologyConf(stormTopologyId);
                
            } catch (NotAliveException ex) {
                LOG.error("The requested topology was not found in the cluster: ID = {}", 
                        stormTopologyId);
                
                ex.printStackTrace();
            } catch (TException ex) {
                LOG.error("Exception while retrieving the remote topology info: {}", 
                        ex.getMessage());
                
                ex.printStackTrace();
            } finally {
                tTransport.close();
            }
        }

        TopologyInfo topologyInfo = new TopologyInfo();
        topologyInfo.setId(info.get_id());
        topologyInfo.setName(info.get_name());
        topologyInfo.setStatus(info.get_status());
        topologyInfo.setUptimeSecs(info.get_uptime_secs());
        topologyInfo.setTopologyConf(topologyConf);

        for (Map.Entry<String, List<backtype.storm.generated.ErrorInfo>> error
                : info.get_errors().entrySet()) {
            List<ErrorInfo> errorInfoList = new ArrayList<ErrorInfo>();
            for (backtype.storm.generated.ErrorInfo ei : error.getValue()) {
                ErrorInfo errorInfo = new ErrorInfo();
                errorInfo.setError(ei.get_error());
                errorInfo.setErrorTimeSecs(ei.get_error_time_secs());

                errorInfoList.add(errorInfo);
            }

            topologyInfo.getErrors().put(error.getKey(), errorInfoList);
        }

        List<ExecutorSummary> executorSummaries = new ArrayList<ExecutorSummary>();
        for (backtype.storm.generated.ExecutorSummary es : info.get_executors()) {
            ExecutorSummary executor = new ExecutorSummary();
            executor.setComponentId(es.get_component_id());
            executor.setHost(es.get_host());
            executor.setPort(es.get_port());
            executor.setUptimeSecs(es.get_uptime_secs());

            backtype.storm.generated.ExecutorInfo ei = es.get_executor_info();
            if (ei != null) {
                ExecutorInfo executorInfo = new ExecutorInfo();
                executorInfo.setTaskStart(ei.get_task_start());
                executorInfo.setTaskEnd(ei.get_task_end());

                executor.setExecutorInfo(executorInfo);
            }

            backtype.storm.generated.ExecutorStats eStats = es.get_stats();
            if (eStats != null) {
                ExecutorStats stats = new ExecutorStats();
                stats.setEmitted(eStats.get_emitted());
                stats.setTransferred(eStats.get_transferred());

                backtype.storm.generated.ExecutorSpecificStats ess = eStats.get_specific();
                if (ess != null) {
                    ExecutorSpecificStats specific = new ExecutorSpecificStats();

                    if (ess.is_set_bolt()) {
                        backtype.storm.generated.BoltStats bs = ess.get_bolt();
                        if (bs != null) {
                            BoltStats boltStats = new BoltStats();

                            for (Map.Entry<String, Map<backtype.storm.generated.GlobalStreamId, Long>> ae
                                    : bs.get_acked().entrySet()) {
                                Map<String, Long> ackedMap = new HashMap<String, Long>();

                                for (Map.Entry<backtype.storm.generated.GlobalStreamId, Long> aem
                                        : ae.getValue().entrySet()) {
                                    backtype.storm.generated.GlobalStreamId gsi = aem.getKey();

                                    String globalStreamId = gsi.get_componentId() 
                                            + "(" + gsi.get_streamId() + ")";

                                    ackedMap.put(globalStreamId, aem.getValue());
                                }

                                boltStats.getAcked().put(ae.getKey(), ackedMap);
                            }

                            for (Map.Entry<String, Map<backtype.storm.generated.GlobalStreamId, Long>> fe
                                    : bs.get_failed().entrySet()) {
                                Map<String, Long> failedMap = new HashMap<String, Long>();

                                for (Map.Entry<backtype.storm.generated.GlobalStreamId, Long> fem
                                        : fe.getValue().entrySet()) {
                                    backtype.storm.generated.GlobalStreamId gsi = fem.getKey();

                                    String globalStreamId = gsi.get_componentId() 
                                            + "(" + gsi.get_streamId() + ")";

                                    failedMap.put(globalStreamId, fem.getValue());
                                }

                                boltStats.getFailed().put(fe.getKey(), failedMap);
                            }

                            for (Map.Entry<String, Map<backtype.storm.generated.GlobalStreamId, Long>> ee
                                    : bs.get_executed().entrySet()) {
                                Map<String, Long> executedMap = new HashMap<String, Long>();

                                for (Map.Entry<backtype.storm.generated.GlobalStreamId, Long> eem
                                        : ee.getValue().entrySet()) {
                                    backtype.storm.generated.GlobalStreamId gsi = eem.getKey();

                                    String globalStreamId = gsi.get_componentId() 
                                            + "(" + gsi.get_streamId() + ")";

                                    executedMap.put(globalStreamId, eem.getValue());
                                }

                                boltStats.getExecuted().put(ee.getKey(), executedMap);
                            }

                            for (Map.Entry<String, Map<backtype.storm.generated.GlobalStreamId, Double>> ema
                                    : bs.get_execute_ms_avg().entrySet()) {
                                Map<String, Double> executedMap = new HashMap<String, Double>();

                                for (Map.Entry<backtype.storm.generated.GlobalStreamId, Double> emam
                                        : ema.getValue().entrySet()) {
                                    backtype.storm.generated.GlobalStreamId gsi = emam.getKey();

                                    String globalStreamId = gsi.get_componentId() 
                                            + "(" + gsi.get_streamId() + ")";

                                    executedMap.put(globalStreamId, emam.getValue());
                                }

                                boltStats.getExecuteMsAvg().put(ema.getKey(), executedMap);
                            }

                            for (Map.Entry<String, Map<backtype.storm.generated.GlobalStreamId, Double>> pma
                                    : bs.get_process_ms_avg().entrySet()) {
                                Map<String, Double> processMap = new HashMap<String, Double>();

                                for (Map.Entry<backtype.storm.generated.GlobalStreamId, Double> pmam
                                        : pma.getValue().entrySet()) {
                                    backtype.storm.generated.GlobalStreamId gsi = pmam.getKey();

                                    String globalStreamId = gsi.get_componentId() 
                                            + "(" + gsi.get_streamId() + ")";

                                    processMap.put(globalStreamId, pmam.getValue());
                                }

                                boltStats.getProcessMsAvg().put(pma.getKey(), processMap);
                            }

                            specific.setBolt(boltStats);
                        }
                    }

                    if (ess.is_set_spout()) {
                        backtype.storm.generated.SpoutStats ss = ess.get_spout();
                        if (ss != null) {
                            SpoutStats spoutStats = new SpoutStats();
                            spoutStats.setAcked(ss.get_acked());
                            spoutStats.setFailed(ss.get_failed());
                            spoutStats.setCompleteMsAvg(ss.get_complete_ms_avg());

                            specific.setSpout(spoutStats);
                        }
                    }

                    stats.setSpecific(specific);
                }

                executor.setStats(stats);
            }

            executorSummaries.add(executor);
        }

        topologyInfo.setExecutors(executorSummaries);

        return topologyInfo;
    }
    
    private String resolveStormTopologyId(Topology topology) {
        String stormTopologyId = null;
        
        // Get the cluster summary for the cluster where the topology is running
        ClusterSummary clusterSummary = getClusterSummary(clusters.get(topology.getClusterId()));
        
        if (clusterSummary != null) {
            // Iterate over all of the topologies in the cluster to match up the name to the streamflow id
            for (TopologySummary topologySummary : clusterSummary.getTopologies()) {
                // The topology ID should match the storm topology name (check for condition)
                if (topology.getId().equalsIgnoreCase(topologySummary.getName())) {
                    stormTopologyId = topologySummary.getId();
                    break;
                }
            }
        }

        return stormTopologyId;
    }
    
    private boolean waitForTopologyRemoval(Topology topology, int maxNumRetries) throws InterruptedException {
        int numTries = 0;
        
        // Check every second to see if the topology was finally removed from the cluster
        while(resolveStormTopologyId(topology) != null) {
            Thread.sleep(1000);
            
            // If the max number of retries were hit, then just break out regardless
            if (++numTries == maxNumRetries) break;
        }
        
        return resolveStormTopologyId(topology) == null;
    }

    private boolean isLocal(String clusterId) {
        return clusterId.equalsIgnoreCase(Cluster.LOCAL);
    }

    private boolean isDeployed(Topology topology) {
        return !topology.getStatus().equals("IDLE");
    }
}
