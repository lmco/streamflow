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
package streamflow.server.security;

import streamflow.model.Role;
import streamflow.model.User;
import streamflow.service.RoleService;
import streamflow.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.elasticsearch.common.collect.Sets;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatastoreRealmTest {
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    public UserService userServiceMock;
    
    @Mock
    public RoleService roleServiceMock;
    
    @Mock
    public CredentialsMatcher credentialsMatcher;
    
    private DatastoreRealm realm;
    
    private User mockedUser;
    
    private Role mockedRole;
    
    @Before
    public void setUp() {
        realm = new DatastoreRealm(userServiceMock, roleServiceMock, credentialsMatcher);
        
        mockedUser = new User();
        mockedUser.setId("testUserId");
        mockedUser.setUsername("testUsername");
        mockedUser.setEmail("testUser@test.com");
        mockedUser.setFirstName("Test");
        mockedUser.setLastName("User");
        mockedUser.setPassword("password");
        mockedUser.setPasswordSalt("passwordSalt");
        mockedUser.setRoles(Sets.newHashSet("test"));
        mockedUser.setEnabled(true);
        
        mockedRole = new Role();
        mockedRole.setId("test");
        mockedRole.setName("test");
        mockedRole.setPermissions(Sets.newHashSet("test:create", "test:delete", "test:list"));
        
        when(userServiceMock.getUserByEmail(mockedUser.getEmail())).thenReturn(mockedUser);
        when(userServiceMock.getUserByUsername(mockedUser.getUsername())).thenReturn(mockedUser);
        when(userServiceMock.getUser(mockedUser.getId())).thenReturn(mockedUser);
        when(roleServiceMock.getRole(mockedRole.getId())).thenReturn(mockedRole);
    }
    
    @Test
    public void invalidAuthenticationTokenType() {
        expectedEx.expect(AuthenticationException.class);
        expectedEx.expectMessage("The provided token is not a UsernamePasswordToken");
        
        realm.doGetAuthenticationInfo(null);
    }
    
    @Test
    public void missingAuthenticationCredentials() {
        expectedEx.expect(AuthenticationException.class);
        expectedEx.expectMessage("The provided token does not contain a username");
        
        realm.doGetAuthenticationInfo(new UsernamePasswordToken());
    }
    
    @Test
    public void invalidAuthenticationCredentials() {
        expectedEx.expect(AuthenticationException.class);
        expectedEx.expectMessage("The username/password was invalid");
        
        realm.doGetAuthenticationInfo(new UsernamePasswordToken("unknownUser", "password"));
    }
    
    @Test
    public void disabledUserAuthentication() {
        mockedUser.setEnabled(false);
        
        expectedEx.expect(AuthenticationException.class);
        expectedEx.expectMessage("The user account is disabled");
        
        realm.doGetAuthenticationInfo(new UsernamePasswordToken(
                mockedUser.getUsername(), mockedUser.getPassword()));
    }
    
    @Test
    public void validAuthenticationCredentialsUsingUsername() {
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(
                new UsernamePasswordToken(mockedUser.getUsername(), mockedUser.getPassword()));
        
        assertNotNull("Auth info should not be null with valid username", authInfo);
        
        assertTrue("Users ID, username, and email should be include in the auth info principals", 
                authInfo.getPrincipals().asSet().containsAll(Sets.newHashSet(
                        mockedUser.getId(), mockedUser.getUsername(), mockedUser.getEmail())));
    }
    
    @Test
    public void validAuthenticationCredentialsUsingEmail() {
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(
                new UsernamePasswordToken(mockedUser.getUsername(), mockedUser.getPassword()));
        
        assertNotNull("Auth info should not be null with valid email", authInfo);
        
        assertTrue("Users ID, username, and email should be include in the auth info principals", 
                authInfo.getPrincipals().asSet().containsAll(Sets.newHashSet(
                        mockedUser.getId(), mockedUser.getUsername(), mockedUser.getEmail())));
    }
    
    @Test
    public void validateAuthorizationRolesAndPersmissions() {
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(
                new UsernamePasswordToken(mockedUser.getUsername(), mockedUser.getPassword()));
        
        AuthorizationInfo authzInfo = realm.doGetAuthorizationInfo(authInfo.getPrincipals());
        
        assertTrue("Authz info should contain all the roles specified for the user",
                authzInfo.getRoles().containsAll(mockedUser.getRoles()));
        
        assertTrue("Authz info should contain all the permissions specified for the user",
                authzInfo.getStringPermissions().containsAll(mockedRole.getPermissions()));
    }
}
