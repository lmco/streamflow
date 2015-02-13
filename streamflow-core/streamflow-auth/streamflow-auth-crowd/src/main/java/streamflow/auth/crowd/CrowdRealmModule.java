package streamflow.auth.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.inject.AbstractModule;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.Properties;
import streamflow.auth.crowd.service.CrowdGroupService;
import streamflow.auth.crowd.service.CrowdUserService;
import streamflow.auth.service.GroupService;
import streamflow.auth.service.UserService;
import streamflow.model.config.AuthConfig;

public class CrowdRealmModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(CrowdUserService.class);
        bind(UserService.class).to(CrowdUserService.class);
        expose(CrowdUserService.class);
        
        bind(CrowdGroupService.class);
        bind(GroupService.class).to(CrowdGroupService.class);
        expose(CrowdGroupService.class);
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
