package streamflow.engine.topology;

import streamflow.engine.topology.storm.StandardTopologyCreator;
import streamflow.engine.topology.trident.TridentTopologyCreator;

public class TopologyCreatorFactory {

    public static TopologyCreator getEngineTopology(String type) {
        if (type.equalsIgnoreCase(StandardTopologyCreator.TYPE)) {
            return new StandardTopologyCreator();
        } else if (type.equalsIgnoreCase(TridentTopologyCreator.TYPE)) {
            return new TridentTopologyCreator();
        } else {
            return null;
        }
    }
}
