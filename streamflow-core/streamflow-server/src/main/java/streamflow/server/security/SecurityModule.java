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

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.inject.Exposed;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.util.Properties;
import javax.servlet.ServletContext;
import streamflow.model.config.AuthConfig;
import streamflow.util.config.ConfigLoader;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import streamflow.auth.crowd.CrowdRealm;
import streamflow.auth.crowd.service.CrowdGroupService;
import streamflow.auth.crowd.service.CrowdUserService;
import streamflow.auth.service.GroupService;
import streamflow.auth.service.UserService;

public class SecurityModule extends ShiroWebModule {
    
    private static final Logger LOG = LoggerFactory.getLogger(SecurityModule.class);

    public SecurityModule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    protected final void configureShiroWeb() {
        AuthConfig authConfig = ConfigLoader.getConfig().getAuth();
        
        // Basic auth - change auth scheme to hide native browser basic auth dialog box
        bindConstant().annotatedWith(Names.named("shiro.authcScheme")).to("Streamflow");
        
        // Attempt to load the custom realm class/module and use defaults if necessary
        bind(CrowdUserService.class);
        bind(CrowdGroupService.class);
        expose(CrowdUserService.class);
        expose(CrowdGroupService.class);
        
        // Bind the provided realm to this Shiro context
        bindRealm().to(CrowdRealm.class);
        
        // Only add security based filter settings if auth is enabled
        if (authConfig.isEnabled()) {
            addFilterChain("/logout/", LOGOUT);
            addFilterChain("/api/security/**", ANON);
            addFilterChain("/api/**", AUTHC_BASIC);
        }
        
        // All remaining resources should be anonymous
        addFilterChain("/**", ANON);
    }
    
    @Singleton
    @Provides
    @Exposed
    public CrowdClient provideCrowdClient(AuthConfig authConfig) {
        Properties crowdProperties = new Properties();
        crowdProperties.setProperty("application.name", "streamflow");
        
        String applicationName = authConfig.getProperty("applicationName", String.class);
        if (applicationName == null) {
            applicationName = "streamflow";
        }
        crowdProperties.setProperty("application.name", applicationName);
        
        String applicationPassword = authConfig.getProperty("applicationPassword", String.class);
        if (applicationPassword == null) {
            applicationPassword = "streamflow";
        }
        crowdProperties.setProperty("application.password", applicationPassword);
        
        String applicationLoginUrl = authConfig.getProperty("applicationLoginUrl", String.class);
        if (applicationLoginUrl == null) {
            applicationLoginUrl = "http://localhost:8095/crowd/console";
        }
        crowdProperties.setProperty("application.login.url", applicationLoginUrl);
        
        String crowdServerUrl = authConfig.getProperty("crowdServerUrl", String.class);
        if (crowdServerUrl == null) {
            crowdServerUrl = "http://localhost:8095/crowd/services/";
        }
        crowdProperties.setProperty("crowd.server.url", crowdServerUrl);
        
        String crowdBaseUrl = authConfig.getProperty("crowdBaseUrl", String.class);
        if (crowdBaseUrl == null) {
            crowdBaseUrl = "http://localhost:8095/crowd/";
        }
        crowdProperties.setProperty("crowd.base.url", crowdBaseUrl);
        
        String sessionIsAuthenticated = authConfig.getProperty("sessionIsAuthenticated", String.class);
        if (sessionIsAuthenticated == null) {
            sessionIsAuthenticated = "session.isauthenticated";
        }
        crowdProperties.setProperty("session.isauthenticated", sessionIsAuthenticated);
        
        String sessionTokenKey = authConfig.getProperty("sessionTokenKey", String.class);
        if (sessionTokenKey == null) {
            sessionTokenKey = "session.tokenkey";
        }
        crowdProperties.setProperty("session.tokenkey", sessionTokenKey);
        
        String sessionValidationInterval = authConfig.getProperty("sessionValidationInterval", String.class);
        if (sessionValidationInterval == null) {
            sessionValidationInterval = "0";
        }
        crowdProperties.setProperty("session.validationinterval", sessionValidationInterval);
        
        String sessionLastValidation = authConfig.getProperty("sessionLastValidation", String.class);
        if (sessionLastValidation == null) {
            sessionLastValidation = "session.lastvalidation";
        }
        crowdProperties.setProperty("session.lastvalidation", sessionLastValidation);
        
        String cookieDomain = authConfig.getProperty("cookieDomain", String.class);
        if (cookieDomain != null) {
            crowdProperties.setProperty("cookie.domain", cookieDomain);
        }
        
        String cookieTokenKey = authConfig.getProperty("cookieTokenKey", String.class);
        if (cookieTokenKey != null) {
            crowdProperties.setProperty("cookie.tokenkey", cookieTokenKey);
        }
        
        ClientProperties clientProperties = 
                ClientPropertiesImpl.newInstanceFromProperties(crowdProperties);
        
        return new RestCrowdClientFactory().newInstance(clientProperties);
    }
}
