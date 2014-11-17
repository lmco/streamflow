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
package streamflow.engine.framework;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import java.io.File;
import java.util.Map.Entry;
import streamflow.model.Cluster;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FrameworkModule extends AbstractModule {

    private final Topology topology;
    
    private final TopologyComponent component;
    
    private final boolean isCluster;
    
    private final StreamflowConfig streamflowConfig;

    public FrameworkModule(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig streamflowConfig) {
        this.topology = topology;
        this.component = component;
        this.isCluster = isCluster;
        this.streamflowConfig = streamflowConfig;
    }

    @Override
    protected void configure() {
        // Iterate over each of the properties and bind the named properties
        for (Entry<String, String> propertyEntry : component.getProperties().entrySet()) {
            bindConstant().annotatedWith(Names.named(propertyEntry.getKey()))
                    .to(propertyEntry.getValue());
        }

        if (streamflowConfig.getProxy().getHost() != null) {
            bindConstant().annotatedWith(Names.named("http.proxy.host")).to(
                    streamflowConfig.getProxy().getHost());
        }

        if (streamflowConfig.getProxy().getPort() > 0) {
            bindConstant().annotatedWith(Names.named("http.proxy.port")).to(
                    streamflowConfig.getProxy().getPort());
        }

        if (component.getKey() != null) {
            bindConstant().annotatedWith(
                    Names.named("streamflow.component.key")).to(component.getKey());
        }

        if (topology.getId() != null) {
            bindConstant().annotatedWith(
                    Names.named("streamflow.topology.id")).to(topology.getId());
        }

        // Bind configuration values for the cluster if values are needed
        Cluster cluster = streamflowConfig.getSelectedCluster();

        if (cluster != null) {
            if (cluster.getId() != null) {
                bindConstant().annotatedWith(
                        Names.named("streamflow.cluster.id")).to(cluster.getId());
            }

            if (cluster.getDisplayName() != null) {
                bindConstant().annotatedWith(
                        Names.named("streamflow.cluster.displayName")).to(cluster.getDisplayName());
            }

            if (cluster.getJmsURI() != null) {
                bindConstant().annotatedWith(
                        Names.named("streamflow.cluster.jmsUri")).to(cluster.getJmsURI());
            }
        }
    }
    
    @Provides
    public org.slf4j.Logger provideLogger() {
        
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern(streamflowConfig.getLogger().getFormatPattern());
        patternLayout.setContext(loggerContext);
        patternLayout.start();
        
        // TODO: NEED TO ENSURE THE PROJECT ID IS PASSED TO THE FRAMEWORK MODULE
        
        String logPath = streamflowConfig.getLogger().getBaseDir() 
                + File.separator + topology.getId() + ".log";

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setName("FILE");
        fileAppender.setFile(logPath);
        fileAppender.setContext(loggerContext);
        fileAppender.setLayout(patternLayout);
        fileAppender.setAppend(true);
        fileAppender.start();
        
        Logger logger = loggerContext.getLogger(component.getMainClass());
        logger.detachAndStopAllAppenders();
        logger.addAppender(fileAppender);
        logger.setAdditive(false);
        logger.setLevel(Level.DEBUG);
        
        // Set the context for the topology/component when logging
        MDC.put("topology", topology.getId());
        MDC.put("component", component.getKey());
        MDC.put("project", topology.getProjectId());
        
        return logger;
    }
}
