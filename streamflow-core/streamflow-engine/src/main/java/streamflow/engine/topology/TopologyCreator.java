package streamflow.engine.topology;

import backtype.storm.generated.StormTopology;
import streamflow.engine.framework.FrameworkException;
import streamflow.model.Topology;
import streamflow.model.config.StreamflowConfig;

public interface TopologyCreator {

    StormTopology build(
            Topology topology, 
            StreamflowConfig configuration,
            boolean isClusterMode) 
            throws FrameworkException;
}
