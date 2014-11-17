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

import streamflow.server.resource.TopologyResource;
import streamflow.server.resource.SerializationResource;
import streamflow.server.resource.UserResource;
import streamflow.server.resource.FileResource;
import streamflow.server.resource.ConfigurationResource;
import streamflow.server.resource.SecurityResource;
import streamflow.server.resource.ComponentResource;
import streamflow.server.resource.RoleResource;
import streamflow.server.resource.ResourceEntryResource;
import streamflow.server.resource.FrameworkResource;
import streamflow.server.resource.ClusterResource;
import streamflow.server.resource.ResourceResource;
import streamflow.server.resource.KafkaResource;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import java.util.HashMap;
import java.util.Map;
import streamflow.server.exception.ServiceExceptionMapper;

public class JerseyModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        // hook Jackson into Jersey as the POJO <-> JSON mapper
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

        // Add custom exception mapper to map Service exception cleanly to responses
        bind(ServiceExceptionMapper.class);
        
        // Manually register all Jersey resources
        bind(SecurityResource.class);
        bind(ClusterResource.class);
        bind(ComponentResource.class);
        bind(ConfigurationResource.class);
        bind(FrameworkResource.class);
        bind(KafkaResource.class);
        bind(ResourceEntryResource.class);
        bind(ResourceResource.class);
        bind(RoleResource.class);
        bind(SerializationResource.class);
        bind(TopologyResource.class);
        bind(FileResource.class);
        bind(UserResource.class);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");

        serve("/api/*").with(GuiceContainer.class, params);
    }
}
