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
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.Serialization;
import streamflow.model.test.IntegrationTest;
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
public class SerializationResourceTest extends JerseyTest {

    @Mock
    public static SerializationService serializationServiceMock;

    public SerializationResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(SerializationWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void listSerialiations() {
        Serialization serialization1 = new Serialization();
        serialization1.setId("serialization-1");
        
        Serialization serialization2 = new Serialization();
        serialization2.setId("serialization-2");
        
        List<Serialization> mockedSerializations = new ArrayList<Serialization>();
        mockedSerializations.add(serialization1);
        mockedSerializations.add(serialization2);
        
        when(serializationServiceMock.listSerializations()).thenReturn(mockedSerializations);
              
        List<Serialization> responseSerializations = resource().path("/api/serializations")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<Serialization>>(){});
        
        assertEquals("Response serializations should be equal to the mocked serializations",
                mockedSerializations, responseSerializations);
        
        verify(serializationServiceMock).listSerializations();
    }
    
    @Test
    public void getSerialization() {
        final Serialization mockedSerialization = new Serialization();
        mockedSerialization.setId("serialization-1");
        
        when(serializationServiceMock.getSerialization(anyString())).then(new Answer<Serialization>() {
            @Override
            public Serialization answer(InvocationOnMock invocation) throws Throwable {
                String argSerializationId = (String) invocation.getArguments()[0];
                
                assertEquals("Service serialization ID should match the requested serialization ID", 
                        argSerializationId, mockedSerialization.getId());
                
                return mockedSerialization;
            }
        });
              
        Serialization responseSerialization = resource().path("/api/serializations/" 
                + mockedSerialization.getId())
            .accept(MediaType.APPLICATION_JSON).get(Serialization.class);
        
        assertEquals("Response serialization should match the mocked serialization", 
                mockedSerialization, responseSerialization);
        
        verify(serializationServiceMock).getSerialization(anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class SerializationWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(SerializationService.class).toInstance(serializationServiceMock);
                    bind(SerializationResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
