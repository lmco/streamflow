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
import streamflow.model.Cluster;
import streamflow.model.storm.ClusterSummary;
import streamflow.service.ClusterService;

@Path("/clusters")
public class ClusterResource {

    private final ClusterService clusterService;

    @Inject
    public ClusterResource(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Cluster> listClusters() {
        return clusterService.listClusters();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Cluster createCluster(Cluster cluster) {
        return clusterService.addCluster(cluster);
    }

    @GET
    @Path("/{clusterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Cluster getCluster(@PathParam("clusterId") String clusterId) {
        return clusterService.getCluster(clusterId);
    }

    @PUT
    @Path("/{clusterId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCluster(@PathParam("clusterId") String clusterId, Cluster cluster) {
        clusterService.updateCluster(clusterId, cluster);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{clusterId}")
    public Response deleteCluster(@PathParam("clusterId") String clusterId) {
        clusterService.deleteCluster(clusterId);
        return Response.ok().build();
    }

    @GET
    @Path("/{clusterId}/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public ClusterSummary getClusterSummary(@PathParam("clusterId") String clusterId) {
        return clusterService.getClusterSummary(clusterId);
    }
}
