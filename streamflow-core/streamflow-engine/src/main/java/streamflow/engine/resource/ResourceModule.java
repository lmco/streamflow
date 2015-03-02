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
package streamflow.engine.resource;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.util.List;
import java.util.Map.Entry;
import streamflow.model.Topology;
import streamflow.model.TopologyResourceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import streamflow.engine.framework.FrameworkUtils;

public class ResourceModule extends AbstractModule {

    protected static final Logger LOG = LoggerFactory.getLogger(ResourceModule.class);

    private final Topology topology;
    
    private final List<TopologyResourceEntry> resourceEntries;

    
    public ResourceModule(Topology topology, List<TopologyResourceEntry> resourceEntries) {
        this.topology = topology;
        this.resourceEntries = resourceEntries;
    }

    @Override
    protected void configure() {
        // Iterate over all of the resource definitions
        for (TopologyResourceEntry resourceEntry : resourceEntries) {
            try {
                // Iterate over each of the properties and bind them as named variables
                for (Entry<String, String> resourceProperty
                        : resourceEntry.getProperties().entrySet()) {
                    bindConstant().annotatedWith(Names.named(resourceProperty.getKey()))
                            .to(resourceProperty.getValue());
                }

                // Use the FrameworkLoader to load the resource module class from the framework
                Class resourceClass = FrameworkUtils.getInstance().loadFrameworkClass(
                        resourceEntry.getFrameworkHash(), resourceEntry.getResourceClass(),
                        topology.getClassLoaderPolicy());

                if (resourceClass.isAssignableFrom(Module.class)) {
                    // Bind the specific resource class to the injector
                    bind(resourceClass);
                }
            } catch (Exception ex) {
                LOG.error("Error loading resource module: ", ex);
            }
        }
    }
}
