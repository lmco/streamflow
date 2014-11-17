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
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.ResourceEntry;
import streamflow.model.ResourceEntryConfig;
import streamflow.service.ResourceEntryService;
import org.apache.shiro.SecurityUtils;

@Path("/resources/{resourceId}/entries")
public class ResourceEntryResource {

    private final ResourceEntryService resourceEntryService;

    @Inject
    public ResourceEntryResource(ResourceEntryService resourceEntryService) {
        this.resourceEntryService = resourceEntryService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ResourceEntry> listResourceEntries(@PathParam("resourceId") String resourceId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return resourceEntryService.listResourceEntriesForResource(resourceId, userId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceEntry createResourceEntry(@PathParam("resourceId") String resourceId,
            ResourceEntry resourceEntry) {
        if (resourceEntry.getResource() == null) {
            resourceEntry.setResource(resourceId);
        }

        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return resourceEntryService.addResourceEntry(resourceEntry, userId);
    }

    @GET
    @Path("/{resourceEntryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceEntry getResourceEntry(@PathParam("resourceId") String resourceId,
            @PathParam("resourceEntryId") String resourceEntryId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return resourceEntryService.getResourceEntry(resourceEntryId, userId);
    }

    @DELETE
    @Path("/{resourceEntryId}")
    public Response deleteResourceEntry(@PathParam("resourceId") String resourceId,
            @PathParam("resourceEntryId") String resourceEntryId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        resourceEntryService.deleteResourceEntry(resourceEntryId, userId);

        return Response.ok().build();
    }

    @PUT
    @Path("/{resourceEntryId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateResourceEntry(@PathParam("resourceId") String resourceId,
            @PathParam("resourceEntryId") String resourceEntryId, ResourceEntry resourceEntry) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        resourceEntryService.updateResourceEntry(resourceEntryId, resourceEntry, userId);

        return Response.ok().build();
    }

    @GET
    @Path("/{resourceEntryId}/config")
    @Produces(MediaType.APPLICATION_JSON)
    public ResourceEntryConfig getResourceEntryConfig(@PathParam("resourceId") String resourceId,
            @PathParam("resourceEntryId") String resourceEntryId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return resourceEntryService.getResourceEntry(resourceEntryId, userId).getConfig();
    }

    @PUT
    @Path("/{resourceEntryId}/config")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateResourceEntryConfig(@PathParam("resourceId") String resourceId,
            @PathParam("resourceEntryId") String resourceEntryId, ResourceEntryConfig resourceEntryConfig) {
        ResourceEntry resourceEntry = resourceEntryService.getResourceEntry(resourceEntryId);
        resourceEntry.setConfig(resourceEntryConfig);

        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        resourceEntryService.updateResourceEntry(resourceEntryId, resourceEntry, userId);

        return Response.ok().build();
    }
}
