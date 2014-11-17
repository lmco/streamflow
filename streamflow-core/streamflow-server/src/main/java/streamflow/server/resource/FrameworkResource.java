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
import com.sun.jersey.multipart.FormDataParam;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.Component;
import streamflow.model.Framework;
import streamflow.model.Resource;
import streamflow.model.Serialization;
import streamflow.service.ComponentService;
import streamflow.service.FrameworkService;
import streamflow.service.ResourceService;
import streamflow.service.SerializationService;

@Path("/frameworks")
public class FrameworkResource {

    private final FrameworkService frameworkService;

    private final ComponentService componentService;

    private final ResourceService resourceService;

    private final SerializationService serializationService;

    @Inject
    public FrameworkResource(FrameworkService frameworkService,
            ComponentService componentService, ResourceService resourceService,
            SerializationService serializationService) {
        this.frameworkService = frameworkService;
        this.componentService = componentService;
        this.resourceService = resourceService;
        this.serializationService = serializationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Framework> listFrameworks() {
        return frameworkService.listFrameworks();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Framework saveFramework(@FormDataParam("file") byte[] frameworkJar, 
            @FormDataParam("isPublic") @DefaultValue("false") boolean isPublic) {
        return frameworkService.addFramework(frameworkJar, isPublic);
    }

    @GET
    @Path("/{frameworkId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Framework getFramework(@PathParam("frameworkId") String frameworkId) {
        return frameworkService.getFramework(frameworkId);
    }

    @DELETE
    @Path("/{frameworkId}")
    public Response deleteFramework(@PathParam("frameworkId") String frameworkId) {
        frameworkService.deleteFramework(frameworkId);
        return Response.ok().build();
    }

    @GET
    @Path("/{frameworkId}/jar")
    @Produces("application/java-archive")
    public Response getFrameworkJar(@PathParam("frameworkId") String frameworkId) {
        Framework framework = frameworkService.getFramework(frameworkId);
        byte[] frameworkJar = frameworkService.getFrameworkJar(frameworkId);

        return Response.ok().entity(frameworkJar)
                .header("Content-Disposition", "attachment; filename=\"" + framework.getName() + ".jar\"")
                .header("Cache-Control", "no-cache").build();
    }

    @GET
    @Path("/{frameworkId}/components")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Component> getFrameworkComponents(@PathParam("frameworkId") String frameworkId) {
        return componentService.listComponentsWithFramework(frameworkId);
    }

    @GET
    @Path("/{frameworkId}/resources")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Resource> getFrameworkResources(@PathParam("frameworkId") String frameworkId) {
        return resourceService.listResourcesWithFramework(frameworkId);
    }

    @GET
    @Path("/{frameworkId}/serializations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Serialization> getFrameworkSerializations(@PathParam("frameworkId") String frameworkId) {
        return serializationService.listSerializationsWithFramework(frameworkId);
    }
}
