package streamflow.server.config;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import streamflow.model.config.MonitorConfig;
import streamflow.model.config.StreamflowConfig;
import streamflow.server.service.TopologyMonitorService;
import streamflow.util.config.ConfigLoader;

import java.util.Set;

public class GuavaServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        MonitorConfig monitorConfig = ConfigLoader.getConfig().getMonitor();
        if (monitorConfig.isEnabled()) {
            bind(TopologyMonitorService.class);
        }
    }
}
