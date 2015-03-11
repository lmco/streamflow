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

import backtype.storm.task.TopologyContext;
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
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FrameworkModule extends AbstractModule {
    
    private final org.slf4j.Logger LOG = LoggerFactory.getLogger(FrameworkModule.class);

    private final Topology topology;
    
    private final TopologyComponent component;
    
    private final StreamflowConfig streamflowConfig;
    
    private final TopologyContext context;

    public FrameworkModule(Topology topology, TopologyComponent component,
            StreamflowConfig streamflowConfig, TopologyContext context) {
        this.topology = topology;
        this.component = component;
        this.streamflowConfig = streamflowConfig;
        this.context = context;
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

        // Bind streamflow specific properties in case underlying bolts/resources require them
        bindConstant().annotatedWith(
                Names.named("streamflow.topology.id")).to(topology.getId());
        //bindConstant().annotatedWith(
        //        Names.named("streamflow.topology.name")).to(topology.getName());
        bindConstant().annotatedWith(
                Names.named("streamflow.component.key")).to(component.getKey());
        bindConstant().annotatedWith(
                Names.named("streamflow.component.label")).to(component.getLabel());
        bindConstant().annotatedWith(
                Names.named("streamflow.component.name")).to(component.getName());
        bindConstant().annotatedWith(
                Names.named("streamflow.component.framework")).to(component.getFramework());
        //bindConstant().annotatedWith(
        //        Names.named("streamflow.user.id")).to(topology.getUserId());
        //bindConstant().annotatedWith(
        //        Names.named("streamflow.cluster.id")).to(topology.getClusterId());
        //bindConstant().annotatedWith(
        //        Names.named("streamflow.cluster.name")).to(topology.getClusterName());
    }
    
    @Provides
    public org.slf4j.Logger provideLogger() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern(streamflowConfig.getLogger().getFormatPattern());
        patternLayout.setContext(loggerContext);
        patternLayout.start();
        
        String logPath = streamflowConfig.getLogger().getBaseDir() 
                + File.separator + "topology-" + topology.getId() + ".log";

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
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
        logger.setLevel(Level.toLevel(topology.getLogLevel()));
        
        // Set the context for the topology/component when logging
        MDC.put("topology", topology.getId());
        MDC.put("project", topology.getProjectId());
        MDC.put("component", component.getKey());
        if (context != null) {
            MDC.put("task", component.getName() + "-" + context.getThisTaskIndex());
        } else {
            MDC.put("task", component.getName());
        }
        
        return logger;
    }
}
