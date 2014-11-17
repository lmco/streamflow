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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import streamflow.model.kafka.KafkaCluster;
import streamflow.model.kafka.KafkaTopic;
import streamflow.service.KafkaService;

@Path("/kafka")
public class KafkaResource {

    private final KafkaService kafkaService;

    @Inject
    public KafkaResource(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GET
    @Path("/clusters")
    @Produces(MediaType.APPLICATION_JSON)
    public List<KafkaCluster> listKafkaClusters() {
        return kafkaService.listClusters();
    }

    @POST
    @Path("/clusters")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public KafkaCluster addKafkaCluster(KafkaCluster kafkaCluster) {
        return kafkaService.addCluster(kafkaCluster);
    }
    
    @GET
    @Path("/clusters/{clusterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public KafkaCluster getKafkaCluster(@PathParam("clusterId") String clusterId) {
        return kafkaService.getCluster(clusterId);
    }

    @DELETE
    @Path("/clusters/{clusterId}")
    public Response removeKafkaCluster(@PathParam("clusterId") String clusterId) {
        kafkaService.deleteCluster(clusterId);
        return Response.ok().build();
    }

    @GET
    @Path("/clusters/{clusterId}/topics")
    @Produces(MediaType.APPLICATION_JSON)
    public List<KafkaTopic> listKafkaTopics(@PathParam("clusterId") String clusterId) {
        return kafkaService.listTopics(clusterId);
    }

    @POST
    @Path("/clusters/{clusterId}/topics")
    public Response addKafkaTopic(@PathParam("clusterId") String clusterId, KafkaTopic topic) {
        try {
            kafkaService.addTopic(clusterId, topic);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Exception occured while adding the topic: " 
                    + ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("/clusters/{clusterId}/topics/{topicName}")
    public Response deleteKafkaTopic(@PathParam("clusterId") String clusterId, 
            @PathParam("topicName") String topicName) {
        try {
            kafkaService.deleteTopic(clusterId, topicName);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Exception occured while deleting the topic: " 
                    + ex.getMessage()).build();
        }
    }
}
