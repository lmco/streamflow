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
package streamflow.auth.crowd;

import com.atlassian.crowd.service.client.CrowdClient;
import com.google.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdRealm extends AuthorizingRealm {

    protected static final Logger LOG = LoggerFactory.getLogger(CrowdRealm.class);
    
    private final CrowdClient crowdClient;

    @Inject
    public CrowdRealm(CrowdClient crowdClient) {
        this.crowdClient = crowdClient;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authenticationToken) throws AuthenticationException {
        // Make sure the token is of the property type
        if (!(authenticationToken instanceof UsernamePasswordToken)) {
            throw new AuthenticationException(
                    "The provided token is not a UsernamePasswordToken");
        }
        
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        try {
            crowdClient.authenticateUser(token.getUsername(), new String(token.getPassword()));

            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        }
        catch (Exception iate) {
            throw new AuthenticationException("Unable to obtain authenticate principal " 
                    + token.getUsername() + " in Crowd.", iate);
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        for (Object principal : principalCollection.fromRealm(getName())) {
            if (LOG.isTraceEnabled()) LOG.trace("Collecting roles from " + principal);

            /*
            try {
                for (String group : crowdClient.(principal.toString())) {
                    if (LOG.isTraceEnabled()) LOG.trace("Adding role " + group);

                    authorizationInfo.addRole(group);
                }
            } catch (InvalidAuthorizationTokenException iae) {
                throw new AuthorizationException("Unable to obtain Crowd group memberships for principal " + principal + ".", iae);
            } catch (RemoteException re) {
                throw new AuthorizationException("Unable to obtain Crowd group memberships for principal " + principal + ".", re);
            } catch (ObjectNotFoundException onfe) {
                throw new AuthorizationException("Unable to obtain Crowd group memberships for principal " + principal + ".", onfe);
            }
            */
        }

        return authorizationInfo;
    }

    public void destroy() {
        // TODO: Cleanup any resources that are necessary before shutdown
    }
}
