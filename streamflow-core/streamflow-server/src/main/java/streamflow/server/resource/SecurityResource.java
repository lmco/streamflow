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

import com.google.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import streamflow.model.User;
import streamflow.model.UserCredentials;
import streamflow.model.config.AuthConfig;
import streamflow.service.UserService;

@Path("/security")
public class SecurityResource {
    
    private final AuthConfig authConfig;

    private final UserService userService;

    @Inject
    public SecurityResource(AuthConfig authConfig, UserService userService) {
        this.authConfig = authConfig;
        this.userService = userService;
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User login(UserCredentials credentials) {
        // Login the subject manually using the provided credentials
        Subject subject = SecurityUtils.getSubject();
        
        try {
            subject.login(new UsernamePasswordToken(
                    credentials.getUsername(), credentials.getPassword(), credentials.getRememberMe()));
        
            return userService.getUser((String) subject.getPrincipal());
        } catch (IncorrectCredentialsException ex) {
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).entity("The username/password was invalid")
                            .type(MediaType.TEXT_PLAIN).build());
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).entity(ex.getMessage())
                            .type(MediaType.TEXT_PLAIN).build());
        } catch (Exception ex) {
            throw new WebApplicationException(
                    Response.status(Status.BAD_REQUEST).entity("Login failed")
                            .type(MediaType.TEXT_PLAIN).build());
        }
    }
    
    @GET
    @Path("/logout")
    public Response logout() {
        // Manually logout the user from the session
        SecurityUtils.getSubject().logout();
        
        return Response.ok().build();
    }

    @GET
    @Path("/whoami")
    @Produces(MediaType.APPLICATION_JSON)
    public User whoami() {
        Subject subject = SecurityUtils.getSubject();

        if (subject.getPrincipal() != null) {
            return userService.getUser((String) subject.getPrincipal());
        } else {
            if (authConfig.isEnabled()) {
                throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
            } else {
                throw new WebApplicationException(Response.status(Status.FORBIDDEN).build());
            }
        }
    }
}
