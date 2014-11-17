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

import com.google.inject.Inject;
import streamflow.model.Role;
import streamflow.model.User;
import streamflow.service.RoleService;
import streamflow.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastoreRealm extends AuthorizingRealm {

    protected static final Logger LOG = LoggerFactory.getLogger(DatastoreRealm.class);

    private final UserService userService;

    private final RoleService roleService;

    private final CredentialsMatcher credentialsMatcher;

    @Inject
    public DatastoreRealm(UserService userService, RoleService roleService,
            CredentialsMatcher credentialsMatcher) {
        this.userService = userService;
        this.roleService = roleService;
        this.credentialsMatcher = credentialsMatcher;
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return credentialsMatcher;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        // Make sure the token is of the property type
        if (!(token instanceof UsernamePasswordToken)) {
            //LOG.error("The provided token is not a UsernamePasswordToken");

            throw new AuthenticationException(
                    "The provided token is not a UsernamePasswordToken");
        }

        // Retrieve the username from the token
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();

        if (username == null) {
            //LOG.error("The provided token does not contain a username");

            throw new AuthenticationException(
                    "The provided token does not contain a username");
        }

        User user = getUserByUsernameOrEmail(username);
        if (user == null) {
            LOG.warn("User with the specified username does not exist: " + username);
            
            throw new AuthenticationException("The username/password was invalid");
        }

        // Make sure the user account is enabled
        if (!user.getEnabled()) {
            //LOG.error("User account with the specified username is disabled: {}", username);

            throw new AuthenticationException("The user account is disabled");
        }

        // Generate the authentication info using the passsword and salt
        SimpleAccount info = new SimpleAccount(username, user.getPassword(),
                new SimpleByteSource(user.getPasswordSalt()), getName());

        // Associate the principals with the authentication info
        SimplePrincipalCollection principals = new SimplePrincipalCollection();
        principals.add(user.getId(), getName());
        principals.add(user.getUsername(), getName());
        principals.add(user.getEmail(), getName());
        info.setPrincipals(principals);
        
        return info;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        if (principalCollection == null) {
            //LOG.error("The provided PrincipalCollection is null");
            return null;
        }

        Object principalId = getAvailablePrincipal(principalCollection);
        if (principalId == null) {
            //LOG.error("The provided PrincipalCollection does not have any available principals");
            return null;
        }

        User user = getUserByUsernameOrEmail((String) principalId);
        if (user == null) {
            //LOG.error("User with the specified username/email does not exist: " + principalId);

            throw new AuthorizationException(
                    "User with the specified username/email does not exist: " + principalId);
        }

        // Build the authorization info using the roles for the specified user
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (String userRole : user.getRoles()) {
            // Associate the role with the current principal
            info.addRole(userRole);

            Role role = roleService.getRole(userRole);

            // Add all of the permissions for the specified role
            info.addStringPermissions(role.getPermissions());
        }

        return info;
    }

    private User getUserByUsernameOrEmail(String username) {
        User user = userService.getUserByUsername(username);

        if (user == null) {
            user = userService.getUserByEmail(username);
            
            if (user == null) {
                try {
                    user = userService.getUser(username);
                } catch (Exception ex) {
                    user = null;
                }
            }
        }

        return user;
    }

    public void destroy() {
        // TODO: Cleanup any resources that are necessary before shutdown
    }
}
