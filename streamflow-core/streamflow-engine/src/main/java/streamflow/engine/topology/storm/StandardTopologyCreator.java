package streamflow.engine.topology.storm;

import backtype.storm.LocalDRPC;
import backtype.storm.drpc.DRPCSpout;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import java.util.Map;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.topology.TopologyCreator;
import streamflow.engine.wrapper.storm.BasicBoltWrapper;
import streamflow.engine.wrapper.storm.RichBoltWrapper;
import streamflow.engine.wrapper.storm.RichSpoutWrapper;
import streamflow.model.Cluster;
import streamflow.model.Component;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.TopologyConnector;
import streamflow.model.config.StreamflowConfig;
import org.apache.commons.lang.StringUtils;

public class StandardTopologyCreator implements TopologyCreator {
    
    public static final String TYPE = "STANDARD";
    
    @Override
    public StormTopology build(Topology topology, StreamflowConfig configuration, boolean isClusterMode) 
            throws FrameworkException {
        TopologyBuilder builder = new TopologyBuilder();

        // Iterate over all of the nodes to add them to the topology
        for (TopologyComponent component : topology.getDeployedConfig().getComponents().values()) {

            if (component.getType().equalsIgnoreCase(Component.STORM_SPOUT_TYPE)) {
                SpoutDeclarer spoutDeclarer;

                // Handle the special case for the DRPC Spout which requires
                // potential startup of a local DRPC server
                if (component.getMainClass().equals(DRPCSpout.class.getName())) {
                    // Attempt to get the name of the DRPC function
                    String drpcFunction = component.getProperties().get("drpc-function");
                    if (StringUtils.isBlank(drpcFunction)) {
                        drpcFunction = topology.getId();
                    }

                    if (configuration.getSelectedCluster().getId().equals(Cluster.LOCAL)) {
                        // Local cluster deploys require manual startup of a DRPC server
                        LocalDRPC drpcServer = new LocalDRPC();

                        // Create the DRPC spout specifying the name of the DRPC function
                        DRPCSpout drpcSpout = new DRPCSpout(drpcFunction, drpcServer);

                        spoutDeclarer = builder.setSpout(
                                component.getKey(), drpcSpout, component.getParallelism());
                    } else {
                        // Create the DRPC spout using the DRPC server on the cluster
                        DRPCSpout drpcSpout = new DRPCSpout(drpcFunction);

                        spoutDeclarer = builder.setSpout(
                                component.getKey(), drpcSpout, component.getParallelism());
                    }
                } else {
                    RichSpoutWrapper richSpoutWrapper = new RichSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    // Add the spout instance to the topology
                    spoutDeclarer = builder.setSpout(
                            component.getKey(), richSpoutWrapper, component.getParallelism());
                }

                // Add the properties for the specific component as component specific properties
                for (Map.Entry<String, String> componentProperty
                        : component.getProperties().entrySet()) {
                    spoutDeclarer.addConfiguration(
                            componentProperty.getKey(), componentProperty.getValue());
                }
            } else if (component.getType().equalsIgnoreCase(Component.STORM_BOLT_TYPE)) {
                BoltDeclarer boltDeclarer;

                try {
                    // Attempt to load the bolt as a RichBolt
                    RichBoltWrapper richBoltWrapper = new RichBoltWrapper(
                            topology, component, isClusterMode, configuration);

                    // Add the spout instance to the topology
                    boltDeclarer = builder.setBolt(
                            component.getKey(), richBoltWrapper, component.getParallelism());
                } catch (FrameworkException ex) {
                    // Attempt to load the bolt as a BasicBolt
                    BasicBoltWrapper basicBoltWrapper = new BasicBoltWrapper(
                            topology, component, isClusterMode, configuration);

                    // Add the spout instance to the topology
                    boltDeclarer = builder.setBolt(
                            component.getKey(), basicBoltWrapper, component.getParallelism());
                }

                // Add the properties for the specific component as component specific properties
                for (Map.Entry<String, String> componentProperty : component.getProperties().entrySet()) {
                    boltDeclarer.addConfiguration(
                            componentProperty.getKey(), componentProperty.getValue());
                }

                // Iterate over each of the edges to see if it is the target
                for (TopologyConnector connector : topology.getDeployedConfig().getConnectors().values()) {
                    // The current edge is the target for the edge
                    if (connector.getTargetComponentKey().equals(component.getKey())) {
                        String grouping = connector.getGrouping();

                        if (grouping.equalsIgnoreCase("Shuffle")) {
                            boltDeclarer.shuffleGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface());
                        } else if (grouping.equalsIgnoreCase("Fields")) {
                            boltDeclarer.fieldsGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface(),
                                    new Fields(connector.getGroupingRef()));
                        } else if (grouping.equalsIgnoreCase("All")) {
                            boltDeclarer.allGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface());
                        } else if (grouping.equalsIgnoreCase("Global")) {
                            boltDeclarer.globalGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface());
                        } else if (grouping.equalsIgnoreCase("None")) {
                            boltDeclarer.noneGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface());
                        } else if (grouping.equalsIgnoreCase("Direct")) {
                            boltDeclarer.directGrouping(
                                    connector.getSourceComponentKey(),
                                    connector.getSourceComponentInterface());
                        }
                    }
                }
            }
        }

        // Build the topology using the topology configured in the builder
        return builder.createTopology();
    }
}
