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
import java.util.Collection;
import javax.ws.rs.core.MediaType;
import streamflow.model.Cluster;
import streamflow.model.storm.ClusterSummary;
import streamflow.model.test.IntegrationTest;
import streamflow.service.ClusterService;
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
public class ClusterResourceTest extends JerseyTest {
    
    @Mock
    public static ClusterService clusterServiceMock;

    public ClusterResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(ClusterWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void listClusters() {
        Collection<Cluster> mockedClusters = new ArrayList<Cluster>();
        mockedClusters.add(new Cluster("LOCAL", "Local Cluster"));
        
        doReturn(mockedClusters).when(clusterServiceMock).listClusters();
              
        Collection<Cluster> responseClusters = resource().path("/api/clusters")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Cluster>>(){});
        
        assertEquals("Response clusters should be equal to the mocked clusters",
                mockedClusters, responseClusters);
        
        verify(clusterServiceMock).listClusters();
    }
    
    @Test
    public void createCluster() {
        final Cluster requestCluster = new Cluster("CLUSTER_TO_CREATE", "New Cluster");
        
        when(clusterServiceMock.addCluster(any(Cluster.class))).then(new Answer<Cluster>() {
            @Override
            public Cluster answer(InvocationOnMock invocation) {
                Cluster argCluster = (Cluster) invocation.getArguments()[0];
                
                assertEquals("Service cluster should equal the requested cluster",
                        requestCluster, argCluster);
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return argCluster;
            }
        });
              
        Cluster responseCluster = resource().path("/api/clusters")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(Cluster.class, requestCluster);
        
        assertEquals("Response cluster should match the request cluster", 
                requestCluster, responseCluster);
        
        verify(clusterServiceMock).addCluster(any(Cluster.class));
    }
    
    @Test 
    public void getCluster() {
        final Cluster mockedCluster = new Cluster("CLUSTER_TO_GET", "Retrieved Cluster");
        
        when(clusterServiceMock.getCluster(anyString())).then(new Answer<Cluster>() {
            @Override
            public Cluster answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                
                assertEquals("Service cluster ID should match the requested cluster ID", 
                        argClusterId, mockedCluster.getId());
                
                return mockedCluster;
            }
        });
              
        Cluster responseCluster = resource().path("/api/clusters/" + mockedCluster.getId())
            .accept(MediaType.APPLICATION_JSON).get(Cluster.class);
        
        assertEquals("Response cluster should match the mocked cluster", 
                mockedCluster, responseCluster);
        
        verify(clusterServiceMock).getCluster(anyString());
    }
    
    @Test 
    public void updateCluster() {
        final Cluster requestCluster = new Cluster("CLUSTER_TO_UPDATE", "Updated Cluster");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                Cluster argCluster = (Cluster) invocation.getArguments()[1];
                
                assertEquals("Service cluster ID should match the requested cluster ID",
                        argClusterId, requestCluster.getId());
                assertEquals("Service cluster should match the requested cluster",
                        argCluster, requestCluster);
                
                return null;
            }
        }).when(clusterServiceMock).updateCluster(anyString(), any(Cluster.class));
              
        ClientResponse clientResponse = resource().path("/api/clusters/" + requestCluster.getId())
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestCluster);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(clusterServiceMock).updateCluster(anyString(), any(Cluster.class));
    }
    
    @Test 
    public void deleteCluster() {
        final String requestClusterId = "CLUSTER_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                
                assertEquals("Service cluster ID should match the requested cluster ID", 
                        argClusterId, requestClusterId);
                
                return null;
            }
        }).when(clusterServiceMock).deleteCluster(anyString());
        
        ClientResponse clientResponse = resource().path("/api/clusters/" + requestClusterId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(clusterServiceMock).deleteCluster(anyString());
    }
    
    @Test 
    public void getClusterSummary() {
        final String requestClusterId = "CLUSTER_TO_GET_SUMMARY";
        final ClusterSummary mockedClusterSummary = new ClusterSummary();
        
        when(clusterServiceMock.getClusterSummary(anyString())).then(new Answer<ClusterSummary>() {
            @Override
            public ClusterSummary answer(InvocationOnMock invocation) throws Throwable {
                String argClusterId = (String) invocation.getArguments()[0];
                
                assertEquals("Service cluster ID should match the requested cluster ID", 
                        argClusterId, requestClusterId);
                
                return mockedClusterSummary;
            }
        });
        
        ClusterSummary responseClusterSummary = resource()
            .path("/api/clusters/" + requestClusterId + "/summary")
            .accept(MediaType.APPLICATION_JSON).get(ClusterSummary.class);
        
        assertEquals("Response cluster summary should match the mocked cluster summary", 
                mockedClusterSummary, responseClusterSummary);
        
        verify(clusterServiceMock).getClusterSummary(anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class ClusterWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(ClusterService.class).toInstance(clusterServiceMock);
                    bind(ClusterResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
