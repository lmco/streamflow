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
package streamflow.util.config;

import com.google.inject.AbstractModule;
import streamflow.model.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(ConfigModule.class);

    @Override
    protected void configure() {
        StreamflowConfig streamflowConfig = ConfigLoader.getConfig();
        
        bind(StreamflowConfig.class).toInstance(streamflowConfig);
        bind(ServerConfig.class).toInstance(streamflowConfig.getServer());
        bind(AuthConfig.class).toInstance(streamflowConfig.getAuth());
        bind(MonitorConfig.class).toInstance(streamflowConfig.getMonitor());
        bind(ProxyConfig.class).toInstance(streamflowConfig.getProxy());
        bind(LoggerConfig.class).toInstance(streamflowConfig.getLogger());
        bind(LocalClusterConfig.class).toInstance(streamflowConfig.getLocalCluster());
        bind(DatastoreConfig.class).toInstance(streamflowConfig.getDatastore());
    }
}
