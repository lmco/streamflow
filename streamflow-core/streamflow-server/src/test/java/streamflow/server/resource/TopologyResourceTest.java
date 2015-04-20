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
import streamflow.model.Topology;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologyLog;
import streamflow.model.TopologyLogCriteria;
import streamflow.model.TopologyLogPage;
import streamflow.model.generator.RandomGenerator;
import streamflow.model.storm.TopologyInfo;
import streamflow.model.test.IntegrationTest;
import streamflow.service.TopologyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
@Category(IntegrationTest.class)
public class TopologyResourceTest extends JerseyTest {

    public static TopologyService topologyServiceMock;
    
    public static final String TEST_SUBJECT_ID = "test-user-id";

    public TopologyResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(TopologyWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void createTopologyWhileAuthenticated() {
        final Topology requestTopology = RandomGenerator.randomObject(Topology.class);
        
        doReturn(requestTopology)
                .when(topologyServiceMock)
                .createTopology(requestTopology, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        Topology responseTopology = resource().path("/api/topologies")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(Topology.class, requestTopology);
        
        assertEquals("Response topology should match the request topology", 
                requestTopology, responseTopology);
        
        verify(topologyServiceMock)
                .createTopology(requestTopology, TEST_SUBJECT_ID);
    }
    
    @Test
    public void updateTopologyWhileAuthenticated() {
        final Topology requestTopology = RandomGenerator.randomObject(Topology.class);
        
        doNothing()
                .when(topologyServiceMock)
                .updateTopology(requestTopology.getId(), requestTopology, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        ClientResponse clientResponse = resource().path("/api/topologies/" + requestTopology.getId())
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestTopology);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                clientResponse.getStatus(), 200);
        
        verify(topologyServiceMock)
                .updateTopology(requestTopology.getId(), requestTopology, TEST_SUBJECT_ID);
    }
    
    @Test
    public void listTopologiesWhileAuthenticated() {
        Topology topology1 = RandomGenerator.randomObject(Topology.class);
        topology1.setName("Topology First");
        
        Topology topology2 = RandomGenerator.randomObject(Topology.class);
        topology2.setName("Topology Second");
        
        Topology topology3 = RandomGenerator.randomObject(Topology.class);
        topology3.setName("Topology Third");
        
        List<Topology> mockedTopologies = new ArrayList<Topology>();
        mockedTopologies.add(topology1);
        mockedTopologies.add(topology2);
        mockedTopologies.add(topology3);
        
        doReturn(mockedTopologies)
                .when(topologyServiceMock)
                .listTopologies(TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        List<Topology> responseUsers = resource().path("/api/topologies")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<Topology>>(){});
        
        assertEquals("Response topologies should be equal to the mocked topologies",
                mockedTopologies, responseUsers);
        
        verify(topologyServiceMock)
                .listTopologies(TEST_SUBJECT_ID);
    }
    
    @Test
    public void getTopologyWhileAuthenticated() {
        final Topology mockedTopology = RandomGenerator.randomObject(Topology.class);
        
        doReturn(mockedTopology)
                .when(topologyServiceMock)
                .getTopology(mockedTopology.getId(), TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        Topology responseTopology = resource().path("/api/topologies/" + mockedTopology.getId())
            .accept(MediaType.APPLICATION_JSON).get(Topology.class);
        
        assertEquals("Response topology should match the mocked topology", 
                mockedTopology, responseTopology);
        
        verify(topologyServiceMock).getTopology(anyString(), anyString());
    }
    
    @Test
    public void deleteTopologyWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        
        doNothing()
                .when(topologyServiceMock)
                .deleteTopology(requestTopologyId, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
        
        ClientResponse clientResponse = resource().path("/api/topologies/" + requestTopologyId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(topologyServiceMock)
                .deleteTopology(requestTopologyId, TEST_SUBJECT_ID);
    }
    
    @Test
    public void updateTopologyConfigWhileAuthenticated() {
        final Topology mockedTopology = RandomGenerator.randomObject(Topology.class);
        final TopologyConfig requestTopologyConfig = RandomGenerator.randomObject(TopologyConfig.class);
        
        doReturn(mockedTopology)
                .when(topologyServiceMock)
                .getTopology(mockedTopology.getId(), TEST_SUBJECT_ID);
        doNothing()
                .when(topologyServiceMock)
                .updateTopology(mockedTopology.getId(), mockedTopology, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        ClientResponse clientResponse = resource()
            .path("/api/topologies/" + mockedTopology.getId() + "/config")
            .type(MediaType.APPLICATION_JSON).put(ClientResponse.class, requestTopologyConfig);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(topologyServiceMock)
                .getTopology(mockedTopology.getId(), TEST_SUBJECT_ID);
        verify(topologyServiceMock)
                .updateTopology(mockedTopology.getId(), mockedTopology, TEST_SUBJECT_ID);
    }
    
    @Test
    public void getTopologyConfigWhileAuthenticated() {
        final Topology mockedTopology = RandomGenerator.randomObject(Topology.class);
        
        doReturn(mockedTopology)
                .when(topologyServiceMock)
                .getTopology(mockedTopology.getId(), TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        TopologyConfig responseTopologyConfig = resource()
            .path("/api/topologies/" + mockedTopology.getId() + "/config")
            .accept(MediaType.APPLICATION_JSON).get(TopologyConfig.class);
        
        assertEquals("Response topology should match the mocked topology", 
                mockedTopology.getCurrentConfig(), responseTopologyConfig);
        
        verify(topologyServiceMock)
                .getTopology(mockedTopology.getId(), TEST_SUBJECT_ID);
    }
    
    @Test
    public void getTopologyInfoWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        final TopologyInfo mockedTopologyInfo = RandomGenerator.randomObject(TopologyInfo.class);
        
        doReturn(mockedTopologyInfo)
                .when(topologyServiceMock)
                .getTopologyInfo(requestTopologyId, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        TopologyInfo responseTopologyInfo = resource()
            .path("/api/topologies/" + requestTopologyId + "/info")
            .accept(MediaType.APPLICATION_JSON).get(TopologyInfo.class);
        
        assertEquals("Response topology info should match the mocked topology info", 
                mockedTopologyInfo, responseTopologyInfo);
        
        verify(topologyServiceMock)
                .getTopologyInfo(requestTopologyId, TEST_SUBJECT_ID);
    }
    
    @Test
    public void submitTopologyWhileAuthenticated() {
        final String requestClusterId = "LOCAL";
        final String requestLogLevel = "INFO";
        final String requestClassLoaderPolicy = "FRAMEWORK_FIRST";
        final Topology mockedTopology = RandomGenerator.randomObject(Topology.class);
        
        doReturn(mockedTopology)
                .when(topologyServiceMock)
                .submitTopology(mockedTopology.getId(), TEST_SUBJECT_ID, requestClusterId,
                        requestLogLevel, requestClassLoaderPolicy);
        
        mockAuthenticatedSubject();
              
        Topology responseTopology = resource()
                .path("/api/topologies/" + mockedTopology.getId() + "/submit")
                .queryParam("clusterId", requestClusterId)
                .accept(MediaType.APPLICATION_JSON).get(Topology.class);
        
        assertEquals("Response topology should match the mocked topology", 
                mockedTopology, responseTopology);
        
        verify(topologyServiceMock)
                .submitTopology(mockedTopology.getId(), TEST_SUBJECT_ID, requestClusterId,
                        requestLogLevel, requestClassLoaderPolicy);
    }
    
    @Test
    public void killTopologyWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        final int requestWaitTimeSecs = 10;
        final boolean async = true;
        
        doNothing()
                .when(topologyServiceMock)
                .killTopology(requestTopologyId, requestWaitTimeSecs, async, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        ClientResponse clientResponse = resource()
            .path("/api/topologies/" + requestTopologyId + "/kill")
            .queryParam("waitTimeSecs", Integer.toString(requestWaitTimeSecs))
            .queryParam("async", Boolean.toString(async))
            .get(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                clientResponse.getStatus(), 200);
        
        verify(topologyServiceMock)
                .killTopology(requestTopologyId, requestWaitTimeSecs, async, TEST_SUBJECT_ID);
    }
    
    @Test
    public void clearTopologyWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        
        doNothing()
                .when(topologyServiceMock)
                .clearTopology(requestTopologyId, TEST_SUBJECT_ID);
        
        mockAuthenticatedSubject();
              
        ClientResponse clientResponse = resource()
            .path("/api/topologies/" + requestTopologyId + "/clear")
            .get(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", 
                clientResponse.getStatus(), 200);
        
        verify(topologyServiceMock)
                .clearTopology(requestTopologyId, TEST_SUBJECT_ID);
    }
    
    @Test
    public void getTopologyLogClusterWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        final TopologyLogCriteria requestLogCriteria = RandomGenerator.randomObject(TopologyLogCriteria.class);
        final TopologyLogPage mockedLogPage = RandomGenerator.randomObject(TopologyLogPage.class);
        
        doReturn(mockedLogPage)
                .when(topologyServiceMock)
                .getTopologyLogCluster(requestTopologyId, TEST_SUBJECT_ID, requestLogCriteria);
        
        mockAuthenticatedSubject();
              
        TopologyLogPage responseLogPage = resource()
            .path("/api/topologies/" + requestTopologyId + "/log")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(TopologyLogPage.class, requestLogCriteria);
        
        assertEquals("Response topology log should match the request topology log", 
                mockedLogPage, responseLogPage);
        
        verify(topologyServiceMock)
                .getTopologyLogCluster(requestTopologyId, TEST_SUBJECT_ID, requestLogCriteria);
    }
    
    @Test
    public void getTopologyLogLocalWhileAuthenticated() {
        final String requestTopologyId = "topology-test";
        final TopologyLog mockedLog = RandomGenerator.randomObject(TopologyLog.class);
        
        doReturn(mockedLog)
                .when(topologyServiceMock)
                .getTopologyLogLocal(requestTopologyId, TEST_SUBJECT_ID, 
                        mockedLog.getOffset(), mockedLog.getCount());
        
        mockAuthenticatedSubject();
              
        TopologyLog responseLog = resource()
            .path("/api/topologies/" + requestTopologyId + "/log")
            .queryParam("offset", Long.toString(mockedLog.getOffset()))
            .queryParam("limit", Long.toString(mockedLog.getCount()))
            .accept(MediaType.APPLICATION_JSON)
            .get(TopologyLog.class);
        
        assertEquals("Response topology log should match the request topology log", 
                mockedLog, responseLog);
        
        verify(topologyServiceMock)
                .getTopologyLogLocal(requestTopologyId, TEST_SUBJECT_ID, 
                        mockedLog.getOffset(), mockedLog.getCount());
    }
    
    private void mockAuthenticatedSubject() {
        PowerMockito.mockStatic(SecurityUtils.class);
        
        Subject mockedSubject = new Subject.Builder(new DefaultSecurityManager())
                .principals(new SimplePrincipalCollection(TEST_SUBJECT_ID, "DatastoreRealm"))
                .buildSubject();
        
        when(SecurityUtils.getSubject()).thenReturn(mockedSubject);
    }
    
    private void mockAnonymousSubject() {
        PowerMockito.mockStatic(SecurityUtils.class);
        
        Subject mockedSubject = new Subject.Builder(new DefaultSecurityManager())
                .buildSubject();
        
        when(SecurityUtils.getSubject()).thenReturn(mockedSubject);
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class TopologyWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    topologyServiceMock = mock(TopologyService.class);
                    
                    bind(TopologyService.class).toInstance(topologyServiceMock);
                    bind(TopologyResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}