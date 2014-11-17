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
package streamflow.service.config;

import com.google.inject.AbstractModule;
import streamflow.service.ClusterService;
import streamflow.service.ComponentService;
import streamflow.service.FrameworkService;
import streamflow.service.KafkaService;
import streamflow.service.LogService;
import streamflow.service.ResourceEntryService;
import streamflow.service.ResourceService;
import streamflow.service.RoleService;
import streamflow.service.SerializationService;
import streamflow.service.TopologyService;
import streamflow.service.FileService;
import streamflow.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(ServiceModule.class);

    @Override
    protected void configure() {
        // Bind each of the core services
        bind(ClusterService.class);
        bind(ComponentService.class);
        bind(FileService.class);
        bind(FrameworkService.class);
        bind(LogService.class);
        bind(ResourceEntryService.class);
        bind(ResourceService.class);
        bind(RoleService.class);
        bind(SerializationService.class);
        bind(TopologyService.class);
        bind(UserService.class);
        bind(KafkaService.class);
    }
}
