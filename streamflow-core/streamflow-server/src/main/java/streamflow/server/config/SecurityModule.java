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
package streamflow.server.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import javax.servlet.ServletContext;
import streamflow.model.config.AuthConfig;
import streamflow.server.security.DatastoreRealm;
import streamflow.server.security.DatastoreRealmModule;
import streamflow.util.config.ConfigLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.AuthorizingRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        
        // 12 hour session timeout in milliseconds
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(43200000L);
        
        // Attempt to load the custom realm class/module and use defaults if necessary
        Class<AuthorizingRealm> realmClass = loadRealmClass(authConfig);
        AbstractModule realmModule = loadRealmModule(authConfig);
        
        // Bind the provided realm to this Shiro context
        bindRealm().to(realmClass);
        
        // Install the realm module class to fulfill any dependencies required by the realm
        install(realmModule);
        
        // Only add security based filter settings if auth is enabled
        if (authConfig.isEnabled()) {
            addFilterChain("/logout/", LOGOUT);
            addFilterChain("/api/security/**", ANON);
            addFilterChain("/api/**", AUTHC_BASIC);
        }
        
        // All remaining resources should be anonymous
        addFilterChain("/**", ANON);
    }
    
    protected Class<AuthorizingRealm> loadRealmClass(AuthConfig authConfig) {
        Class realmClass = DatastoreRealm.class;
        
        if (StringUtils.isNotBlank(authConfig.getRealmClass())) {
            try {
                LOG.info("Loading custom realm class: " + authConfig.getRealmClass());
                
                Class loadedRealmClass = Thread.currentThread().getContextClassLoader()
                        .loadClass(authConfig.getRealmClass());

                // Make sure the datastore module class is an actual Guice AbstractModule
                if (AuthorizingRealm.class.isAssignableFrom(loadedRealmClass)) {
                    realmClass = loadedRealmClass;

                    LOG.info("Successfully loaded custom realm class: " + authConfig.getRealmClass());
                } else {
                    LOG.error("The custom realm class does not extend AuthorizingRealm: {}",
                            authConfig.getRealmClass());
                    LOG.info("Using default DatastoreRealm implementation");
                }
            } catch (Exception ex) {
                LOG.error("An exception occurred while loading the custom realm class", ex);
                LOG.info("Using default DatastoreRealm implementation");
            } 
        } else {
            LOG.info("Using default DatastoreRealm implementation");
        }
        
        return realmClass;
    }
    
    protected AbstractModule loadRealmModule(AuthConfig authConfig) {
        AbstractModule realmModule = new DatastoreRealmModule();
        
        if (StringUtils.isNotBlank(authConfig.getModuleClass())) {
            try {
                LOG.info("Loading custom realm module: " + authConfig.getModuleClass());
                
                Class datastoreModuleClass = Thread.currentThread().getContextClassLoader()
                        .loadClass(authConfig.getModuleClass());

                // Make sure the datastore module class is an actual Guice AbstractModule
                if (AbstractModule.class.isAssignableFrom(datastoreModuleClass)) {
                    realmModule = (AbstractModule) datastoreModuleClass.newInstance();

                    LOG.info("Successfully loaded custom realm module: " + authConfig.getModuleClass());
                } else {
                    LOG.error("The provided custom realm module class does not extend AbstractModule: {}",
                            authConfig.getModuleClass());
                    LOG.info("Using default DatastoreRealmModule implementation");
                }
            } catch (Exception ex) {
                LOG.error("An exception occurred while loading the custom realm module", ex);
                LOG.info("Using default DatastoreRealmModule implementation");
            } 
        } else {
            LOG.info("Using default DatastoreRealmModule implementation");
        }
        
        return realmModule;
    }
}
