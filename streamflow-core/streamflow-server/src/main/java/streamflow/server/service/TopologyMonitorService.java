package streamflow.server.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import streamflow.model.Topology;
import streamflow.model.config.MonitorConfig;
import streamflow.service.TopologyService;

import java.util.concurrent.TimeUnit;

@Singleton
public class TopologyMonitorService extends AbstractScheduledService {

    public static final Logger LOG = LoggerFactory.getLogger(TopologyMonitorService.class);

    private TopologyService topologyService;
    private MonitorConfig monitorConfig;

    @Inject
    public TopologyMonitorService(TopologyService topologyService, MonitorConfig monitorConfig) {
        this.topologyService = topologyService;
        this.monitorConfig = monitorConfig;
    }

    @Override
    protected void startUp() throws Exception {
        LOG.info("Topology Status Monitor Started...");
    }

    @Override
    protected void runOneIteration() throws Exception {
        // Iterate over all of the topologies for each user to check live status
        for (Topology topology : topologyService.listAllTopologies()) {
            try {
                // Get the current live status of the topology
                String topologyStatusDesired = topology.getStatus();
                String topologyStatusActual = topologyService.getTopology(topology.getId(), topology.getUserId()).getStatus();

                if (topologyStatusDesired.equalsIgnoreCase("ACTIVE")) {
                    // Topology should be submitted, but isn't active so resubmit to desired state
                    if (!topologyStatusActual.equalsIgnoreCase("ACTIVE")) {
                        LOG.warn("Topology has a desired state of ACTIVE but is not currently deployed. "
                                + "Redeploying topology... ID = " + topology.getId() + ", Name = " + topology.getName());

                        // Resubmit the topology using the same settings as originally submitted
                        Topology submittedTopology = topologyService.submitTopology(
                                topology.getId(), topology.getUserId(), topology.getClusterId(),
                                topology.getLogLevel(), topology.getClassLoaderPolicy());

                        if (topology != null && topology.getStatus().equalsIgnoreCase("ACTIVE")) {
                            LOG.info("Topology redeploy succeeded: ID = " + topology.getId() + ", Name = " + topology.getName());
                        } else {
                            LOG.error("Topology redeploy failed: ID = " + topology.getId() + ", Name = " + topology.getName());
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.error("An exception occurred while checking topology status: ID = "
                        + topology.getId() + ", Name = " + topology.getName(), ex);
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(monitorConfig.getPollingInterval(), monitorConfig.getPollingInterval(), TimeUnit.SECONDS);
    }
}
