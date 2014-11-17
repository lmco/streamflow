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
import streamflow.model.Role;
import streamflow.service.RoleService;

@Path("/roles")
public class RoleResource {

    private final RoleService roleService;

    @Inject
    public RoleResource(RoleService roleService) {
        this.roleService = roleService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Role> listRoles() {
        return roleService.listRoles();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Role createRole(Role role) {
        return roleService.createRole(role);
    }

    @GET
    @Path("/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Role getRole(@PathParam("roleId") String roleId) {
        return roleService.getRole(roleId);
    }

    @PUT
    @Path("/{roleId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("roleId") String roleId, Role role) {
        roleService.updateRole(roleId, role);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{roleId}")
    public Response deleteRole(@PathParam("roleId") String roleId) {
        roleService.deleteRole(roleId);
        return Response.ok().build();
    }
}
