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
package streamflow.server.resource;

import com.google.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import streamflow.model.config.StreamflowConfig;

@Path("/config")
public class ConfigurationResource {

    private final StreamflowConfig streamflowConfig;

    @Inject
    public ConfigurationResource(StreamflowConfig streamflowConfig) {
        this.streamflowConfig = streamflowConfig;
    }

    @GET
    @Path("/app")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamflowConfig getAppConfiguration() {
        return streamflowConfig;
    }
}
