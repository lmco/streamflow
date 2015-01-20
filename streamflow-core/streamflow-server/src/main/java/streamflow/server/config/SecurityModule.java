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
        
        // Attempt to load the custom realm class/module and use defaults if necessary
        Class<AuthorizingRealm> realmClass = loadRealmClass(authConfig.getRealmClass());
        AbstractModule realmModule = loadRealmModule(authConfig.getModuleClass());
        
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
    
    protected Class<AuthorizingRealm> loadRealmClass(String realmClassString) {
        Class realmClass = DatastoreRealm.class;
        
        if (StringUtils.isNotBlank(realmClassString)) {
            try {
                LOG.info("Loading custom realm class: " + realmClassString);
                
                Class loadedRealmClass = Thread.currentThread().getContextClassLoader()
                        .loadClass(realmClassString);

                // Make sure the datastore module class is an actual Guice AbstractModule
                if (AuthorizingRealm.class.isAssignableFrom(loadedRealmClass)) {
                    realmClass = loadedRealmClass;

                    LOG.info("Successfully loaded custom realm class: " + realmClassString);
                } else {
                    LOG.error("The custom realm class does not extend AuthorizingRealm: {}",
                            realmClassString);
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
    
    protected AbstractModule loadRealmModule(String realmModuleClassString) {
        AbstractModule realmModule = new DatastoreRealmModule();
        
        if (StringUtils.isNotBlank(realmModuleClassString)) {
            try {
                LOG.info("Loading custom realm module: " + realmModuleClassString);
                
                Class datastoreModuleClass = Thread.currentThread().getContextClassLoader()
                        .loadClass(realmModuleClassString);

                // Make sure the datastore module class is an actual Guice AbstractModule
                if (AbstractModule.class.isAssignableFrom(datastoreModuleClass)) {
                    realmModule = (AbstractModule) datastoreModuleClass.newInstance();

                    LOG.info("Successfully loaded custom realm module: " + realmModuleClassString);
                } else {
                    LOG.error("The provided custom realm module class does not extend AbstractModule: {}",
                            realmModuleClassString);
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
