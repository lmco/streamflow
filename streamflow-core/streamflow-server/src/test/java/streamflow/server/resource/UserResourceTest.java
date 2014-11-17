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
import streamflow.model.PasswordChange;
import streamflow.model.User;
import streamflow.model.test.IntegrationTest;
import streamflow.service.UserService;
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
public class UserResourceTest extends JerseyTest {

    @Mock
    public static UserService userServiceMock;

    public UserResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(UserWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void createUser() {
        final User requestUser = new User();
        requestUser.setId("user-1");
        requestUser.setUsername("user1");
        requestUser.setFirstName("First Name");
        requestUser.setLastName("Last Name");
        requestUser.setEmail("user1@test.com");
        
        when(userServiceMock.createUser(any(User.class))).then(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) {
                User argUser = (User) invocation.getArguments()[0];
                
                assertEquals("Service user should equal the requested user", requestUser, argUser);
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return argUser;
            }
        });
              
        User responseUser = resource().path("/api/users")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .post(User.class, requestUser);
        
        assertEquals("Response user should match the request user", 
                requestUser, responseUser);
        
        verify(userServiceMock).createUser(any(User.class));
    }
    
    @Test
    public void updateUser() {
        final User requestUser = new User();
        requestUser.setId("user-1");
        requestUser.setUsername("user1");
        requestUser.setFirstName("First Name");
        requestUser.setLastName("Last Name");
        requestUser.setEmail("user1@test.com");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argUserId = (String) invocation.getArguments()[0];
                User argUser = (User) invocation.getArguments()[1];
                
                assertEquals("Service user ID should match the requested user ID",
                        argUserId, requestUser.getId());
                assertEquals("Service user should match the requested user",
                        argUser, requestUser);
                
                return null;
            }
        }).when(userServiceMock).updateUser(anyString(), any(User.class));
              
        ClientResponse clientResponse = resource().path("/api/users/" + requestUser.getId())
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestUser);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(userServiceMock).updateUser(anyString(), any(User.class));
    }
    
    @Test
    public void listUsers() {
        User user1 = new User();
        user1.setId("user-1");
        user1.setUsername("user1");
        user1.setFirstName("First Name 1");
        user1.setLastName("Last Name 1");
        user1.setEmail("user1@test.com");
        
        User user2 = new User();
        user2.setId("user-2");
        user2.setUsername("user2");
        user2.setFirstName("First Name 2");
        user2.setLastName("Last Name 2");
        user2.setEmail("user2@test.com");
        
        List<User> mockedUsers = new ArrayList<User>();
        mockedUsers.add(user1);
        mockedUsers.add(user2);
        
        when(userServiceMock.listUsers()).thenReturn(mockedUsers);
              
        List<User> responseUsers = resource().path("/api/users")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<User>>(){});
        
        assertEquals("Response users should be equal to the mocked users",
                mockedUsers, responseUsers);
        
        verify(userServiceMock).listUsers();
    }
    
    @Test
    public void getUser() {
        final User mockedUser = new User();
        mockedUser.setId("user-1");
        mockedUser.setUsername("user1");
        mockedUser.setFirstName("First Name 1");
        mockedUser.setLastName("Last Name 1");
        mockedUser.setEmail("user1@test.com");
        
        when(userServiceMock.getUser(anyString())).then(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                String argUserId = (String) invocation.getArguments()[0];
                
                assertEquals("Service user ID should match the requested user ID", 
                        argUserId, mockedUser.getId());
                
                return mockedUser;
            }
        });
              
        User responseUser = resource().path("/api/users/" + mockedUser.getId())
            .accept(MediaType.APPLICATION_JSON).get(User.class);
        
        assertEquals("Response user should match the mocked user", mockedUser, responseUser);
        
        verify(userServiceMock).getUser(anyString());
    }
    
    @Test
    public void deleteUser() {
        final String requestUserId = "USER_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argUserId = (String) invocation.getArguments()[0];
                
                assertEquals("Service user ID should match the requested user ID", 
                        argUserId, requestUserId);
                
                return null;
            }
        }).when(userServiceMock).deleteUser(anyString());
        
        ClientResponse clientResponse = resource().path("/api/users/" + requestUserId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(userServiceMock).deleteUser(anyString());
    }
    
    @Test
    public void changeUserPassword() {
        final String requestUserId = "USER_TO_CHANGE";
        final PasswordChange requestPassword = new PasswordChange();
        requestPassword.setCurrentPassword("OLD_PASSWORD");
        requestPassword.setNewPassword("NEW_PASSWORD");
        requestPassword.setConfirmPassword("NEW_PASSWORD");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argUserId = (String) invocation.getArguments()[0];
                PasswordChange argPassword = (PasswordChange) invocation.getArguments()[1];
                
                assertEquals("Service user id should match the requested user id",
                        argUserId, requestUserId);
                assertEquals("Service password change should match the requested password change",
                        argPassword, requestPassword);
                
                return null;
            }
        }).when(userServiceMock).updateUserPassword(anyString(), any(PasswordChange.class));
              
        ClientResponse clientResponse = resource().path("/api/users/" + requestUserId + "/password")
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestPassword);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(userServiceMock).updateUserPassword(anyString(), any(PasswordChange.class));
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class UserWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(UserService.class).toInstance(userServiceMock);
                    bind(UserResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
