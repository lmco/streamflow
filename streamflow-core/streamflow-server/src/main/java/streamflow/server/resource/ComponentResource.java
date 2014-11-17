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
import java.util.Collection;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.Component;
import streamflow.model.ComponentConfig;
import streamflow.model.Framework;
import streamflow.service.ComponentService;

@Path("/components")
public class ComponentResource {

    private final ComponentService componentService;

    @Inject
    public ComponentResource(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Component> listComponents(@QueryParam("visibility") 
            @DefaultValue(Framework.VISIBILITY_ALL) String visibility) {
        return componentService.listComponents(visibility);
    }

    @GET
    @Path("/{componentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Component getComponent(@PathParam("componentId") String componentId) {
        return componentService.getComponent(componentId);
    }

    @GET
    @Path("/{componentId}/config")
    @Produces(MediaType.APPLICATION_JSON)
    public ComponentConfig getComponentConfig(@PathParam("componentId") String componentId) {
        return componentService.getComponent(componentId).getConfig();
    }

    @GET
    @Path("/{componentId}/icon")
    @Produces("image/jpeg")
    public Response getComponentIcon(@PathParam("componentId") String componentId) {
        return Response.ok(componentService.getComponentIcon(componentId)).build();
    }
}
