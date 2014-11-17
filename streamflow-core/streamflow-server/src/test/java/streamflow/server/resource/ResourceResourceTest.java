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
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.Resource;
import streamflow.model.ResourceConfig;
import streamflow.model.test.IntegrationTest;
import streamflow.service.ResourceService;
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
public class ResourceResourceTest extends JerseyTest {

    @Mock
    public static ResourceService resourceServiceMock;

    public ResourceResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(ResourceWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void listResources() {
        Resource resource1 = new Resource();
        resource1.setId("resource-1");
        resource1.setName("resource-one");
        resource1.setLabel("Resource One");
        resource1.setVersion("1.0.0");
        resource1.setFramework("test-framework");
        resource1.setFrameworkLabel("Test Framework");
        
        Resource resource2 = new Resource();
        resource2.setId("resource-2");
        resource2.setName("resource-two");
        resource2.setLabel("Resource Two");
        resource2.setVersion("1.0.0");
        resource2.setFramework("test-framework");
        resource2.setFrameworkLabel("Test Framework");
        
        List<Resource> mockedResources = new ArrayList<Resource>();
        mockedResources.add(resource1);
        mockedResources.add(resource2);
        
        when(resourceServiceMock.listResources()).thenReturn(mockedResources);
              
        Collection<Resource> responseResources = resource().path("/api/resources")
            .queryParam("visibility", "ALL").accept(MediaType.APPLICATION_JSON)
            .get(new GenericType<Collection<Resource>>(){});
        
        assertEquals("Response resources should match the mocked resources", 
                mockedResources, responseResources);
        
        verify(resourceServiceMock).listResources();
    }
    
    @Test
    public void getResource() {
        final Resource mockedResource = new Resource();
        mockedResource.setId("resource-1");
        mockedResource.setName("resource-one");
        mockedResource.setLabel("Resource One");
        mockedResource.setVersion("1.0.0");
        mockedResource.setFramework("test-framework");
        mockedResource.setFrameworkLabel("Test Framework");
        
        when(resourceServiceMock.getResource(anyString())).then(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
                String argResourceId = (String) invocation.getArguments()[0];
                
                assertEquals("Service resource ID should match the requested resource ID", 
                        argResourceId, mockedResource.getId());
                
                return mockedResource;
            }
        });
              
        Resource responseResource = resource().path("/api/resources/" + mockedResource.getId())
            .accept(MediaType.APPLICATION_JSON).get(Resource.class);
        
        assertEquals("Response resource should match the mocked resource", mockedResource, responseResource);
        
        verify(resourceServiceMock).getResource(anyString());
    }
    
    @Test
    public void getResourceConfig() {
        final Resource mockedResource = new Resource();
        mockedResource.setId("resource-1");
        mockedResource.setName("resource-one");
        mockedResource.setLabel("Resource One");
        mockedResource.setVersion("1.0.0");
        mockedResource.setFramework("test-framework");
        mockedResource.setFrameworkLabel("Test Framework");
        
        ResourceConfig mockedResourceConfig = new ResourceConfig();
        mockedResourceConfig.setName("resource-one");
        mockedResource.setConfig(mockedResourceConfig);
        
        when(resourceServiceMock.getResource(anyString())).then(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
                String argResourceId = (String) invocation.getArguments()[0];
                
                assertEquals("Service resource ID should match the requested resource ID", 
                        argResourceId, mockedResource.getId());
                
                return mockedResource;
            }
        });
              
        ResourceConfig responseResourceConfig = resource()
                .path("/api/resources/" + mockedResource.getId() + "/config")
                .accept(MediaType.APPLICATION_JSON).get(ResourceConfig.class);
        
        assertEquals("Response resource should match the mocked resource", 
                mockedResourceConfig, responseResourceConfig);
        
        verify(resourceServiceMock).getResource(anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class ResourceWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(ResourceService.class).toInstance(resourceServiceMock);
                    bind(ResourceResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
