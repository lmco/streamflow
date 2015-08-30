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
package streamflow.server.config;

//import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.apache.storm.guava.util.concurrent.ServiceManager;
import streamflow.datastore.config.DatastoreModule;
import streamflow.engine.config.EngineModule;
import streamflow.server.service.TopologyMonitorService;
import streamflow.service.config.ServiceModule;
import streamflow.util.config.ConfigModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import streamflow.util.environment.StreamflowEnvironment;

@WebListener
public class WebConfig extends GuiceServletContextListener {

    private ServletContext servletContext;

    @Override
    protected Injector getInjector() {
        // Initialize the Streamflow Environment using STREAMFLOW_HOME if available
        StreamflowEnvironment.setStreamflowHome(System.getenv("STREAMFLOW_HOME"));
        StreamflowEnvironment.initialize();
        
        Injector injector = Guice.createInjector(new ConfigModule(), new DatastoreModule(),
                new ServiceModule(), new GuavaServiceModule(), new EngineModule(), new JerseyModule(),
                new SecurityModule(servletContext), ShiroWebModule.guiceFilterModule());

        // Initialize the service manager to manage daemon services
        //ServiceManager manager = injector.getInstance(ServiceManager.class);
        //manager.startAsync().awaitHealthy();
        TopologyMonitorService topologyMonitorService = injector.getInstance(TopologyMonitorService.class);
        topologyMonitorService.startAsync().awaitRunning();

        return injector;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
    }
}
