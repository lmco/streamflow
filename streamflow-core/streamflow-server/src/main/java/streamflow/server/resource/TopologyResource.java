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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.Topology;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologyLog;
import streamflow.model.TopologyLogCriteria;
import streamflow.model.TopologyLogPage;
import streamflow.model.storm.TopologyInfo;
import streamflow.service.TopologyService;
import org.apache.shiro.SecurityUtils;

@Path("/topologies")
public class TopologyResource {

    private final TopologyService topologyService;

    @Inject
    public TopologyResource(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Topology> listTopologies() {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.listTopologies(userId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Topology createTopology(Topology topology) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.createTopology(topology, userId);
    }

    @GET
    @Path("/{topologyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Topology getTopology(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.getTopology(topologyId, userId);
    }

    @PUT
    @Path("/{topologyId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTopology(@PathParam("topologyId") String topologyId, Topology topology) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        topologyService.updateTopology(topologyId, topology, userId);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{topologyId}")
    public Response deleteTopology(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        topologyService.deleteTopology(topologyId, userId);

        return Response.ok().build();
    }

    @GET
    @Path("/{topologyId}/config")
    @Produces(MediaType.APPLICATION_JSON)
    public TopologyConfig getTopologyConfig(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.getTopology(topologyId, userId).getCurrentConfig();
    }

    @PUT
    @Path("/{topologyId}/config")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTopologyConfig(@PathParam("topologyId") String topologyId,
            TopologyConfig topologyConfig) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        
        Topology topology = topologyService.getTopology(topologyId, userId);
        topology.setCurrentConfig(topologyConfig);

        topologyService.updateTopology(topologyId, topology, userId);

        return Response.ok().build();
    }

    @GET
    @Path("/{topologyId}/config/deployed")
    @Produces(MediaType.APPLICATION_JSON)
    public TopologyConfig getDeployedTopologyConfig(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.getTopology(topologyId, userId).getDeployedConfig();
    }

    @GET
    @Path("/{topologyId}/info")
    @Produces(MediaType.APPLICATION_JSON)
    public TopologyInfo getTopologyInfo(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.getTopologyInfo(topologyId, userId);
    }

    @GET
    @Path("/{topologyId}/submit")
    @Produces(MediaType.APPLICATION_JSON)
    public Topology submitTopology(@PathParam("topologyId") String topologyId,
            @QueryParam("clusterId") String clusterId,
            @QueryParam("logLevel") @DefaultValue("INFO") String logLevel,
            @QueryParam("classLoaderPolicy") @DefaultValue("FRAMEWORK_FIRST") String classLoaderPolicy) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.submitTopology(topologyId, userId, clusterId, logLevel, classLoaderPolicy);
    }

    @GET
    @Path("/{topologyId}/kill")
    public Response killTopology(@PathParam("topologyId") String topologyId,
            @QueryParam("waitTimeSecs") @DefaultValue("0") int waitTimeSecs,
            @QueryParam("async") @DefaultValue("false") boolean async) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        topologyService.killTopology(topologyId, waitTimeSecs, async, userId);

        return Response.ok().build();
    }

    @GET
    @Path("/{topologyId}/clear")
    public Response clearTopology(@PathParam("topologyId") String topologyId) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        topologyService.clearTopology(topologyId, userId);

        return Response.ok().build();
    }

    @POST
    @Path("/{topologyId}/log")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TopologyLogPage getTopologyLogCluster(@PathParam("topologyId") String topologyId,
            TopologyLogCriteria criteria) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        
        return topologyService.getTopologyLogCluster(topologyId, userId, criteria);
    }

    @GET
    @Path("/{topologyId}/log")
    @Produces(MediaType.APPLICATION_JSON)
    public TopologyLog getTopologyLogLocal(@PathParam("topologyId") String topologyId,
            @QueryParam("offset") long offset, @QueryParam("limit") long limit) {
        String userId = (String) SecurityUtils.getSubject().getPrincipal();
        return topologyService.getTopologyLogLocal(topologyId, userId, offset, limit);
    }
}
