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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.Component;
import streamflow.model.ComponentConfig;
import streamflow.model.test.IntegrationTest;
import streamflow.service.ComponentService;
import org.apache.commons.io.IOUtils;
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
public class ComponentResourceTest extends JerseyTest {
    
    @Mock
    public static ComponentService componentServiceMock;

    public ComponentResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(ComponentWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }

    @Test
    public void listComponents() {
        Component mockedComponent = new Component();
        
        List<Component> mockedComponents = new ArrayList<Component>();
        mockedComponents.add(mockedComponent);
        
        when(componentServiceMock.listComponents(anyString())).thenReturn(mockedComponents);
              
        Collection<Component> responseComponents = resource().path("/api/components")
            .queryParam("visibility", "ALL").accept(MediaType.APPLICATION_JSON)
            .get(new GenericType<Collection<Component>>(){});
        
        assertEquals("Response components should match the mocked components", 
                mockedComponents, responseComponents);
        
        verify(componentServiceMock).listComponents(anyString());
    }
    
    @Test
    public void getComponent() {
        final Component mockedComponent=  new Component();
        mockedComponent.setId("COMPONENT_TO_GET");
        
        when(componentServiceMock.getComponent(anyString())).then(new Answer<Component>() {
            @Override
            public Component answer(InvocationOnMock invocation) throws Throwable {
                String argComponentId = (String) invocation.getArguments()[0];
                
                assertEquals("Service component ID should match the requested component ID", 
                        argComponentId, mockedComponent.getId());
                
                return mockedComponent;
            }
        });
              
        Component responseComponent = resource().path("/api/components/" + mockedComponent.getId())
            .accept(MediaType.APPLICATION_JSON).get(Component.class);
        
        assertEquals("Response component should match the mocked component", 
                mockedComponent, responseComponent);
        
        verify(componentServiceMock).getComponent(anyString());
    }
    
    @Test
    public void getComponentConfig() {
        final ComponentConfig mockedComponentConfig = new ComponentConfig();
        mockedComponentConfig.setName("test-component");
        mockedComponentConfig.setLabel("Test Component");
        
        final Component mockedComponent = new Component();
        mockedComponent.setId("COMPONENT_TO_GET");
        mockedComponent.setConfig(mockedComponentConfig);
        
        when(componentServiceMock.getComponent(anyString())).thenReturn(mockedComponent);
              
        ComponentConfig responseComponentConfig = resource()
            .path("/api/components/" + mockedComponent.getId() + "/config")
            .accept(MediaType.APPLICATION_JSON).get(ComponentConfig.class);
        
        assertEquals("Response config should match the mocked config", 
                mockedComponentConfig, responseComponentConfig);
        
        verify(componentServiceMock).getComponent(anyString());
    }
    
    @Test
    public void getComponentIcon() {
        final String mockedComponentId = "COMPONENT_TO_GET";
        
        try {
            final byte[] mockedIconData = IOUtils.toByteArray(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream("component/icons/sample-icon.png"));
            
            when(componentServiceMock.getComponentIcon(anyString())).then(new Answer<byte[]>() {
                @Override
                public byte[] answer(InvocationOnMock invocation) throws Throwable {
                    String argComponentId = (String) invocation.getArguments()[0];
                    
                    assertEquals("Service component ID should match mocked component ID",
                            argComponentId, mockedComponentId);
                    
                    return mockedIconData;
                }
            });
            
            byte[] responseIconData = resource()
                .path("/api/components/" + mockedComponentId + "/icon")
                .accept("image/jpeg").get(byte[].class);
            
            assertTrue("Binary icon content should match mocked icon content",
                Arrays.equals(responseIconData, mockedIconData));
            
            verify(componentServiceMock).getComponentIcon(anyString());
        } catch (Exception ex) {
            fail("Unable to load sample icon for test: " + ex.getMessage());
        }
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class ComponentWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(ComponentService.class).toInstance(componentServiceMock);
                    bind(ComponentResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
