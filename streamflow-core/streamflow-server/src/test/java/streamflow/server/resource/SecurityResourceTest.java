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
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import streamflow.model.User;
import streamflow.model.generator.RandomGenerator;
import streamflow.model.test.IntegrationTest;
import streamflow.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
@Category(IntegrationTest.class)
public class SecurityResourceTest extends JerseyTest {

    public static UserService userServiceMock;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    public static final String TEST_SUBJECT_ID = "test-user-id";

    public SecurityResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(AuthWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void checkWhoamiWithUser() {
        final User mockedUser = RandomGenerator.randomObject(User.class);
        mockedUser.setPassword(null);
        mockedUser.setPasswordSalt(null);
        
        mockAuthenticatedSubject();
        
        doReturn(mockedUser).when(userServiceMock).getUser(TEST_SUBJECT_ID);
        
        User responseUser = resource().path("/api/security/whoami")
            .accept(MediaType.APPLICATION_JSON).get(User.class);
        
        assertEquals("Response user should match the mocked user", 
                mockedUser, responseUser);
        
        verify(userServiceMock).getUser(TEST_SUBJECT_ID);
    }
    
    @Test
    public void checkWhoamiWithAnonymous() {
        mockAnonymousSubject();
        
        doReturn(null).when(userServiceMock).getUser(null);
        
        ClientResponse clientResponse = resource().path("/api/security/whoami")
            .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        
        assertEquals("Response status for an unauthenticated user should be unauthorized (403)", 
                Status.FORBIDDEN.getStatusCode(), clientResponse.getStatus());
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
    
    public static class AuthWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    userServiceMock = mock(UserService.class);
                    
                    bind(UserService.class).toInstance(userServiceMock);
                    bind(SecurityResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
