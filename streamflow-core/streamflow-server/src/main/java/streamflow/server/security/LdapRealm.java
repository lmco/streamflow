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

import java.util.Set;

import javax.naming.AuthenticationNotSupportedException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.security.auth.login.AccountException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.ldap.UnsupportedAuthenticationMechanismException;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import streamflow.model.Role;
import streamflow.model.User;
import streamflow.service.RoleService;
import streamflow.service.UserService;

import org.apache.isis.security.shiro.IsisLdapRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapRealm extends IsisLdapRealm {

	// class hierarchy
	LdapRealm aaa;
	IsisLdapRealm aab;
	org.apache.shiro.realm.ldap.JndiLdapRealm abc;
	org.apache.shiro.realm.AuthorizingRealm def;
		org.apache.shiro.authz.Authorizer def2;
		org.apache.shiro.util.Initializable def3;
		org.apache.shiro.authz.permission.PermissionResolverAware def4;
		org.apache.shiro.authz.permission.RolePermissionResolverAware def5;
	org.apache.shiro.realm.AuthenticatingRealm ghi;
	    org.apache.shiro.util.Initializable ghi2; 
	org.apache.shiro.realm.CachingRealm jkl;
	    org.apache.shiro.realm.Realm jkl2;
	    org.apache.shiro.util.Nameable jkl3;
	    org.apache.shiro.cache.CacheManagerAware jkl4;
	    org.apache.shiro.authc.LogoutAware jkl5;
		  
	protected static final Logger LOG = LoggerFactory.getLogger(LdapRealm.class);

    // these cant be used here, since LDAP is read-only
    private final UserService userService;
    private final RoleService roleService;

	@Inject
    public LdapRealm(UserService userService, RoleService roleService)
    {
        this.userService = userService;
        this.roleService = roleService;
    }
	
	// just a hook so i can debug the result of authen/author
	
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		
		//Subject subject = SecurityUtils.getSubject();
    	
    	/****** ORIGINAL *******/
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
        /******************************* END ORIGINAL ******************************/
    	

        /************************ JNDI ************************/
        
        
        
        
        /******************* END JNDI **************************************/
        
    	//AuthenticationInfo info = super.doGetAuthenticationInfo(token);
        return info;
    }
    
    // This documentation is from JndiLdapRealm
    /**
     * Method that should be implemented by subclasses to build an
     * {@link AuthorizationInfo} object by querying the LDAP context for the
     * specified principal.</p>
     *
     * @param principals          the principals of the Subject whose AuthenticationInfo should be queried from the LDAP server.
     * @param ldapContextFactory factory used to retrieve LDAP connections.
     * @return an {@link AuthorizationInfo} instance containing information retrieved from the LDAP server.
     * @throws NamingException if any LDAP errors occur during the search.
     * 
     * protected AuthorizationInfo doGetAuthorizationInfo
     * protected AuthorizationInfo queryForAuthorizationInfo(
     */
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

    	AuthorizationInfo info = super.doGetAuthorizationInfo(principals);
    	
    	LdapContextFactory ctx = this.getContextFactory();
    	
    	try {
			LdapContext lctx = ctx.getSystemLdapContext();
			
			NamingEnumeration<NameClassPair> ne = lctx.list("the-name");
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
        // Build the authorization info using the roles for the specified user
    	User user = new User();
        SimpleAuthorizationInfo info2 = new SimpleAuthorizationInfo();
        for (String userRole : user.getRoles()) {
            // Associate the role with the current principal
            info2.addRole(userRole);

            Role role = roleService.getRole(userRole);

            // Add all of the permissions for the specified role
            info2.addStringPermissions(role.getPermissions());
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

    /**** MAY BE USEFUL LATER?? ****************/
    protected AuthenticationInfo doGetAuthenticationInfo2(AuthenticationToken token) throws AuthenticationException, AccountException 
    {
    	UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    	
    	String username = upToken.getUsername();
    	
    	if (username == null) 
    	{
    		throw new AccountException("Null usernames are not allowed by this realm.");
    	}
    	
    	String password = "password";
    	return new SimpleAuthenticationInfo(username, password, this.getName());
    }

    protected AuthorizationInfo doGetAuthorizationInfo2(PrincipalCollection principals) 
    {
    	if (principals == null) 
    	{
    		throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
    	}
    	String username = (String)
    	principals.fromRealm(getName()).iterator().next();
    	Set<String> roleNames = ImmutableSet.of();
    	if (username != null) 
    	{
    		roleNames = ImmutableSet.of("admin", "user");
    	}
    	return new SimpleAuthorizationInfo(roleNames);
    }

}


