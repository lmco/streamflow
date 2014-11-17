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
import com.google.common.collect.Sets;
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
import streamflow.model.Role;
import streamflow.model.test.IntegrationTest;
import streamflow.service.RoleService;
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
public class RoleResourceTest extends JerseyTest {

    @Mock
    public static RoleService roleServiceMock;

    public RoleResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(RoleWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void createRole() {
        final Role requestRole = new Role();
        requestRole.setId("role-1");
        requestRole.setName("Role 1");
        requestRole.setPermissions(Sets.newHashSet("test:create", "test:updated", "test:delete"));
        
        when(roleServiceMock.createRole(any(Role.class))).then(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock invocation) {
                Role argRole = (Role) invocation.getArguments()[0];
                
                assertEquals("Service role should equal the requested role", requestRole, argRole);
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return argRole;
            }
        });
              
        Role responseRole = resource().path("/api/roles")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(Role.class, requestRole);
        
        assertEquals("Response role should match the request role", 
                requestRole, responseRole);
        
        verify(roleServiceMock).createRole(any(Role.class));
    }
    
    @Test
    public void updateRole() {
        final Role requestRole = new Role();
        requestRole.setId("role-1");
        requestRole.setName("Role 1");
        requestRole.setPermissions(Sets.newHashSet("test:create", "test:updated", "test:delete"));
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argRoleId = (String) invocation.getArguments()[0];
                Role argRole = (Role) invocation.getArguments()[1];
                
                assertEquals("Service role ID should match the requested role ID",
                        argRoleId, requestRole.getId());
                assertEquals("Service role should match the requested role",
                        argRole, requestRole);
                
                return null;
            }
        }).when(roleServiceMock).updateRole(anyString(), any(Role.class));
              
        ClientResponse clientResponse = resource().path("/api/roles/" + requestRole.getId())
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestRole);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(roleServiceMock).updateRole(anyString(), any(Role.class));
    }
    
    @Test
    public void listRoles() {
        Role role1 = new Role();
        role1.setId("role-1");
        role1.setName("Role 1");
        role1.setPermissions(Sets.newHashSet("test:create", "test:updated", "test:delete"));
        
        Role role2 = new Role();
        role2.setId("role-2");
        role2.setName("Role 2");
        role2.setPermissions(Sets.newHashSet("test:read", "test:query", "test:create"));
        
        List<Role> mockedRoles = new ArrayList<Role>();
        mockedRoles.add(role1);
        mockedRoles.add(role2);
        
        when(roleServiceMock.listRoles()).thenReturn(mockedRoles);
              
        List<Role> responseRoles = resource().path("/api/roles")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<Role>>(){});
        
        assertEquals("Response roles should be equal to the mocked roles",
                mockedRoles, responseRoles);
        
        verify(roleServiceMock).listRoles();
    }
    
    @Test
    public void getRole() {
        final Role mockedRole = new Role();
        mockedRole.setId("role-1");
        mockedRole.setName("Role 1");
        mockedRole.setPermissions(Sets.newHashSet("test:create", "test:updated", "test:delete"));
        
        when(roleServiceMock.getRole(anyString())).then(new Answer<Role>() {
            @Override
            public Role answer(InvocationOnMock invocation) throws Throwable {
                String argRoleId = (String) invocation.getArguments()[0];
                
                assertEquals("Service role ID should match the requested role ID", 
                        argRoleId, mockedRole.getId());
                
                return mockedRole;
            }
        });
              
        Role responseRole = resource().path("/api/roles/" + mockedRole.getId())
            .accept(MediaType.APPLICATION_JSON).get(Role.class);
        
        assertEquals("Response role should match the mocked role", mockedRole, responseRole);
        
        verify(roleServiceMock).getRole(anyString());
    }
    
    @Test
    public void deleteRole() {
        final String requestRoleId = "ROLE_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argRoleId = (String) invocation.getArguments()[0];
                
                assertEquals("Service role ID should match the requested role ID", 
                        argRoleId, requestRoleId);
                
                return null;
            }
        }).when(roleServiceMock).deleteRole(anyString());
        
        ClientResponse clientResponse = resource().path("/api/roles/" + requestRoleId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(roleServiceMock).deleteRole(anyString());
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class RoleWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(RoleService.class).toInstance(roleServiceMock);
                    bind(RoleResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
