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
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.Component;
import streamflow.model.Framework;
import streamflow.model.Resource;
import streamflow.model.Serialization;
import streamflow.model.test.IntegrationTest;
import streamflow.service.ComponentService;
import streamflow.service.FrameworkService;
import streamflow.service.ResourceService;
import streamflow.service.SerializationService;
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
public class FrameworkResourceTest extends JerseyTest {

    @Mock
    public static FrameworkService frameworkServiceMock;
    
    @Mock
    public static ComponentService componentServiceMock;
    
    @Mock
    public static ResourceService resourceServiceMock;
    
    @Mock
    public static SerializationService serializationServiceMock;

    public FrameworkResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(FrameworkWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void listFrameworks() {
        Framework framework1 = new Framework();
        framework1.setId("framework-1");
        framework1.setName("framework-first");
        framework1.setLabel("First Framework");
        framework1.setCount(7);
        framework1.setEnabled(true);
        
        Framework framework2 = new Framework();
        framework2.setId("framework-2");
        framework2.setName("framework-second");
        framework2.setLabel("Second Framework");
        framework2.setCount(10);
        framework2.setEnabled(false);
        
        List<Framework> mockedFrameworks = new ArrayList<Framework>();
        mockedFrameworks.add(framework1);
        mockedFrameworks.add(framework2);
        
        when(frameworkServiceMock.listFrameworks()).thenReturn(mockedFrameworks);
              
        Collection<Framework> responseFrameworks = resource().path("/api/frameworks")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<Collection<Framework>>(){});
        
        assertEquals("Response clusters should be equal to the mocked clusters",
                mockedFrameworks, responseFrameworks);
        
        verify(frameworkServiceMock).listFrameworks();
    }
    
    @Test
    public void saveFramework() {
        final byte[] mockedFrameworkJar = "FRAMEWORK FILE CONTENTS".getBytes();
        
        final Framework mockedFramework = new Framework();
        mockedFramework.setId("test-framework");
        mockedFramework.setName("test-framework");
        mockedFramework.setLabel("Test Framework");
        mockedFramework.setPublic(true);
        
        when(frameworkServiceMock.addFramework(any(byte[].class), anyBoolean())).then(new Answer<Framework>() {
            @Override
            public Framework answer(InvocationOnMock invocation) {
                byte[] argFrameworkJar = (byte[]) invocation.getArguments()[0];
                boolean argIsPublic = (Boolean) invocation.getArguments()[1];
                
                assertTrue("Service framework jar should equal the requested framework jar",
                        Arrays.equals(mockedFrameworkJar, argFrameworkJar));
                assertEquals("Service framework visibility should equal the requested framework visibility",
                        mockedFramework.isPublic(), argIsPublic);
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return mockedFramework;
            }
        });
        
        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("isPublic", Boolean.toString(mockedFramework.isPublic()));
        formData.field("file", mockedFrameworkJar, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        
        Framework responseFramework = resource().path("/api/frameworks")
                .accept(MediaType.APPLICATION_JSON).type(MediaType.MULTIPART_FORM_DATA)
                .post(Framework.class, formData);
        
        assertEquals("Response framework should equal the mocked framework",
                        mockedFramework, responseFramework);
        
        verify(frameworkServiceMock).addFramework(any(byte[].class), anyBoolean());
    }
    
    @Test
    public void getFramework() {
        final Framework mockedFramework=  new Framework();
        mockedFramework.setId("FRAMEWORK_TO_GET");
        
        when(frameworkServiceMock.getFramework(anyString())).then(new Answer<Framework>() {
            @Override
            public Framework answer(InvocationOnMock invocation) throws Throwable {
                String argFrameworkId = (String) invocation.getArguments()[0];
                
                assertEquals("Service framework ID should match the requested framework ID", 
                        argFrameworkId, mockedFramework.getId());
                
                return mockedFramework;
            }
        });
              
        Framework responseFramework = resource().path("/api/frameworks/" + mockedFramework.getId())
            .accept(MediaType.APPLICATION_JSON).get(Framework.class);
        
        assertEquals("Response framework should match the mocked framework", 
                mockedFramework, responseFramework);
        
        verify(frameworkServiceMock).getFramework(anyString());
    }
    
    @Test
    public void deleteFramework() {
        final String requestFrameworkId = "FRAMEWORK_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argFrameworkId = (String) invocation.getArguments()[0];
                
                assertEquals("Service framework ID should match the requested framework ID", 
                        argFrameworkId, requestFrameworkId);
                
                return null;
            }
        }).when(frameworkServiceMock).deleteFramework(anyString());
        
        ClientResponse clientResponse = resource().path("/api/frameworks/" + requestFrameworkId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(frameworkServiceMock).deleteFramework(anyString());
    }
    
    @Test
    public void getFrameworkJar() {
        final Framework mockedFramework =  new Framework();
        mockedFramework.setId("FRAMEWORK_TO_GET");
        mockedFramework.setName("test-framework");
        mockedFramework.setLabel("Test Framework");
        
        final byte[] mockedFrameworkJar = "FRAMEWORK CONTENT GOES HERE".getBytes();
        
        when(frameworkServiceMock.getFramework(anyString())).thenReturn(mockedFramework);
        
        when(frameworkServiceMock.getFrameworkJar(anyString())).then(new Answer<byte[]>() {
            @Override
            public byte[] answer(InvocationOnMock invocation) throws Throwable {
                String argFrameworkId = (String) invocation.getArguments()[0];
                
                assertEquals("Service framework ID should match the requested framework ID", 
                        mockedFramework.getId(), argFrameworkId);
                
                return mockedFrameworkJar;
            }
        });
              
        byte[] responseFrameworkJar = resource().path("/api/frameworks/" + mockedFramework.getId() + "/jar")
            .accept("application/java-archive").get(byte[].class);
        
        assertTrue("Response framework should match the mocked framework", 
                Arrays.equals(mockedFrameworkJar, responseFrameworkJar));
        
        verify(frameworkServiceMock).getFramework(anyString());
        verify(frameworkServiceMock).getFrameworkJar(anyString());
    }
    
    @Test
    public void getFrameworkComponents() {
        Component mockedComponent1 = new Component();
        mockedComponent1.setId("component-1");
        mockedComponent1.setName("component-1");
        mockedComponent1.setLabel("Component 1");
        mockedComponent1.setFramework("test-framework");
        mockedComponent1.setFrameworkLabel("Test Framework");
        
        Component mockedComponent2 = new Component();
        mockedComponent2.setId("component-2");
        mockedComponent2.setName("component-2");
        mockedComponent2.setLabel("Component 2");
        mockedComponent2.setFramework("test-framework");
        mockedComponent2.setFrameworkLabel("Test Framework");
        
        List<Component> mockedComponents = new ArrayList<Component>(); 
        mockedComponents.add(mockedComponent1);
        mockedComponents.add(mockedComponent2);
        
        when(componentServiceMock.listComponentsWithFramework(anyString()))
                .thenReturn(mockedComponents);
        
        List<Component> responseComponents = resource().path("/api/frameworks/" 
                + mockedComponent1.getFramework() + "/components").accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Component>>(){});
        
        assertEquals("Response components should match the mocked components",
                mockedComponents, responseComponents);
        
        verify(componentServiceMock).listComponentsWithFramework(anyString());
    }
    
    @Test
    public void getFrameworkResources() {
        Resource mockedResource1 = new Resource();
        mockedResource1.setId("resource-1");
        mockedResource1.setName("resource-1");
        mockedResource1.setLabel("Resource 1");
        mockedResource1.setFramework("test-framework");
        mockedResource1.setFrameworkLabel("Test Framework");
        
        Resource mockedResource2 = new Resource();
        mockedResource2.setId("resource-2");
        mockedResource2.setName("resource-2");
        mockedResource2.setLabel("Resource 2");
        mockedResource2.setFramework("test-framework");
        mockedResource2.setFrameworkLabel("Test Framework");
        
        List<Resource> mockedResources = new ArrayList<Resource>(); 
        mockedResources.add(mockedResource1);
        mockedResources.add(mockedResource2);
        
        when(resourceServiceMock.listResourcesWithFramework(anyString()))
                .thenReturn(mockedResources);
        
        List<Resource> responseResources = resource().path("/api/frameworks/" 
                + mockedResource1.getFramework() + "/resources").accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Resource>>(){});
        
        assertEquals("Response resources should match the mocked resources",
                mockedResources, responseResources);
        
        verify(resourceServiceMock).listResourcesWithFramework(anyString());
    }
    
    @Test
    public void getFrameworkSerializations() {
        Serialization mockedSerialization1 = new Serialization();
        mockedSerialization1.setId("serialization-1");
        mockedSerialization1.setTypeClass("streamflow.type.class.one");
        mockedSerialization1.setSerializerClass("streamflow.serializer.class.one");
        mockedSerialization1.setFramework("test-framework");
        mockedSerialization1.setFrameworkLabel("Test Framework");
        
        Serialization mockedSerialization2 = new Serialization();
        mockedSerialization2.setId("serialization-2");
        mockedSerialization1.setTypeClass("streamflow.type.class.two");
        mockedSerialization1.setSerializerClass("streamflow.serializer.class.two");
        mockedSerialization2.setFramework("test-framework");
        mockedSerialization2.setFrameworkLabel("Test Framework");
        
        List<Serialization> mockedSerializations = new ArrayList<Serialization>(); 
        mockedSerializations.add(mockedSerialization1);
        mockedSerializations.add(mockedSerialization2);
        
        when(serializationServiceMock.listSerializationsWithFramework(anyString()))
                .thenReturn(mockedSerializations);
        
        List<Serialization> responseSerializations = resource().path("/api/frameworks/" 
                + mockedSerialization1.getFramework() + "/serializations").accept(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Serialization>>(){});
        
        assertEquals("Response serializations should match the mocked serializations",
                mockedSerializations, responseSerializations);
        
        verify(serializationServiceMock).listSerializationsWithFramework(anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class FrameworkWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(FrameworkService.class).toInstance(frameworkServiceMock);
                    bind(ComponentService.class).toInstance(componentServiceMock);
                    bind(ResourceService.class).toInstance(resourceServiceMock);
                    bind(SerializationService.class).toInstance(serializationServiceMock);
                    bind(FrameworkResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
