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

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.kafka.KafkaCluster;
import streamflow.model.kafka.KafkaTopic;
import streamflow.model.test.IntegrationTest;
import streamflow.service.KafkaService;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
@Category(IntegrationTest.class)
public class KafkaResourceTest extends JerseyTest {

    @Mock
    public static KafkaService kafkaServiceMock;

    public KafkaResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(KafkaWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void addKafkaCluster() {
        final KafkaCluster requestCluster = new KafkaCluster();
        requestCluster.setId("kafkaCluster-1");
        requestCluster.setName("Kafka Cluster 1");
        requestCluster.setZookeeperUri("zookeeper://127.0.0.1:2181");
        
        when(kafkaServiceMock.addCluster(any(KafkaCluster.class))).then(new Answer<KafkaCluster>() {
            @Override
            public KafkaCluster answer(InvocationOnMock invocation) {
                KafkaCluster argCluster = (KafkaCluster) invocation.getArguments()[0];
                
                assertEquals("Service kafka cluster should equal the requested kafka cluster", 
                        requestCluster, argCluster);
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return argCluster;
            }
        });
              
        KafkaCluster responseCluster = resource().path("/api/kafka/clusters")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(KafkaCluster.class, requestCluster);
        
        assertEquals("Response kafka cluster should match the request kafka cluster", 
                requestCluster, responseCluster);
        
        verify(kafkaServiceMock).addCluster(any(KafkaCluster.class));
    }
    
    @Test
    public void listKafkaClusters() {
        KafkaCluster cluster1 = new KafkaCluster();
        cluster1.setId("kafkaCluster-1");
        cluster1.setName("Kafka Cluster 1");
        cluster1.setZookeeperUri("zookeeper://127.0.0.1:2181");
        
        KafkaCluster cluster2 = new KafkaCluster();
        cluster2.setId("kafkaCluster-2");
        cluster2.setName("Kafka Cluster 2");
        cluster2.setZookeeperUri("zookeeper://localhost:2181");
        
        List<KafkaCluster> mockedClusters = new ArrayList<KafkaCluster>();
        mockedClusters.add(cluster1);
        mockedClusters.add(cluster2);
        
        when(kafkaServiceMock.listClusters()).thenReturn(mockedClusters);
              
        List<KafkaCluster> responseClusters = resource().path("/api/kafka/clusters")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<KafkaCluster>>(){});
        
        assertEquals("Response kafka clusters should be equal to the mocked kafka clusters",
                mockedClusters, responseClusters);
        
        verify(kafkaServiceMock).listClusters();
    }
    
    @Test
    public void getKafkaCluster() {
        final KafkaCluster mockedCluster = new KafkaCluster();
        mockedCluster.setId("kafkaCluster-1");
        mockedCluster.setName("Kafka Cluster 1");
        mockedCluster.setZookeeperUri("zookeeper://127.0.0.1:2181");
        
        when(kafkaServiceMock.getCluster(anyString())).then(new Answer<KafkaCluster>() {
            @Override
            public KafkaCluster answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                
                assertEquals("Service kafka cluster ID should match the requested kafka cluster ID", 
                        argClusterId, mockedCluster.getId());
                
                return mockedCluster;
            }
        });
              
        KafkaCluster responseCluster = resource()
                .path("/api/kafka/clusters/" + mockedCluster.getId())
                .accept(MediaType.APPLICATION_JSON).get(KafkaCluster.class);
        
        assertEquals("Response kafka cluster should match the mocked kafka cluster", 
                mockedCluster, responseCluster);
        
        verify(kafkaServiceMock).getCluster(anyString());
    }
    
    @Test
    public void deleteKafkaCluster() {
        final String requestClusterId = "TOPIC_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                
                assertEquals("Service kafka cluster ID should match the requested kafka cluster ID", 
                        argClusterId, requestClusterId);
                
                return null;
            }
        }).when(kafkaServiceMock).deleteCluster(anyString());
        
        ClientResponse clientResponse = resource()
                .path("/api/kafka/clusters/" + requestClusterId)
                .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                200, clientResponse.getStatus());
        
        verify(kafkaServiceMock).deleteCluster(anyString());
    }
    
    @Test
    public void addKafkaTopic() {
        final String requestClusterId = "CLUSTER_TO_GET";
        final KafkaTopic requestTopic = new KafkaTopic();
        requestTopic.setName("Kafka Topic 1");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                KafkaTopic argTopic = (KafkaTopic) invocation.getArguments()[1];
                
                assertEquals("Service kafka cluster ID should match the requested kafka cluster ID", 
                        argClusterId, requestClusterId);
                
                assertEquals("Service kafka topic should equal the requested kafka topic", 
                        requestTopic, argTopic);
                
                // Return the same topic object as provided as an argument (same as real behavior)
                return null;
            }
        }).when(kafkaServiceMock).addTopic(anyString(), any(KafkaTopic.class));
              
        ClientResponse clientResponse = resource()
                .path("/api/kafka/clusters/" + requestClusterId + "/topics")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, requestTopic);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                200, clientResponse.getStatus());
        
        verify(kafkaServiceMock).addTopic(anyString(), any(KafkaTopic.class));
    }
    
    @Test
    public void listKafkaTopics() {
        String requestClusterId = "CLUSTER_TO_GET";
        
        KafkaTopic topic1 = new KafkaTopic();
        topic1.setName("Kafka Topic 1");
        topic1.setNumPartitions(5);
        topic1.setReplicationFactor(2);
        
        KafkaTopic topic2 = new KafkaTopic();
        topic2.setName("Kafka Topic 2");
        topic2.setNumPartitions(10);
        topic2.setReplicationFactor(3);
        
        List<KafkaTopic> mockedTopics = new ArrayList<KafkaTopic>();
        mockedTopics.add(topic1);
        mockedTopics.add(topic2);
        
        when(kafkaServiceMock.listTopics(anyString())).thenReturn(mockedTopics);
              
        List<KafkaTopic> responseTopics = resource()
                .path("/api/kafka/clusters/" + requestClusterId + "/topics")
                .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<KafkaTopic>>(){});
        
        assertEquals("Response kafka topics should be equal to the mocked kafka topics",
                mockedTopics, responseTopics);
        
        verify(kafkaServiceMock).listTopics(anyString());
    }
    
    @Test
    public void deleteKafkaTopic() {
        final String requestClusterId = "CLUSTER_TO_GET";
        final String requestTopicName = "TOPIC_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                String argTopicName = (String) invocation.getArguments()[1];
                
                assertEquals("Service kafka cluster id should match the requested kafka cluster id", 
                        argClusterId, requestClusterId);
                
                assertEquals("Service kafka topic name should match the requested kafka topic name", 
                        argTopicName, requestTopicName);
                
                return null;
            }
        }).when(kafkaServiceMock).deleteTopic(anyString(), anyString());
        
        ClientResponse clientResponse = resource()
                .path("/api/kafka/clusters/" + requestClusterId + "/topics/" + requestTopicName)
                .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                200, clientResponse.getStatus());
        
        verify(kafkaServiceMock).deleteTopic(anyString(), anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class KafkaWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(KafkaService.class).toInstance(kafkaServiceMock);
                    bind(KafkaResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
