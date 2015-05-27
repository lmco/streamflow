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
package streamflow.engine.config;

import backtype.storm.LocalCluster;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import streamflow.engine.StormEngine;
import streamflow.model.config.LocalClusterConfig;
import streamflow.util.config.ConfigLoader;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StormEngine.class);
        
        LocalClusterConfig localClusterConfig = ConfigLoader.getConfig().getLocalCluster();
        if (localClusterConfig != null && localClusterConfig.isEnabled()) {
            bind(LocalCluster.class).annotatedWith(Names.named("LocalCluster")).toInstance(new LocalCluster());
        }
    }
}
