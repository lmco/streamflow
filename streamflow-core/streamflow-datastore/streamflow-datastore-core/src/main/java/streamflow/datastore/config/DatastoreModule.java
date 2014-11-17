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
package streamflow.datastore.config;

import com.google.inject.AbstractModule;
import streamflow.model.config.DatastoreConfig;
import streamflow.util.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastoreModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(DatastoreModule.class);

    @Override
    protected void configure() {
        DatastoreConfig datastoreConfig = ConfigLoader.getConfig().getDatastore();
        
        try {
            Class datastoreModuleClass = DatastoreModule.class.getClassLoader()
                    .loadClass(datastoreConfig.getModuleClass());
            
            // Make sure the datastore module class is an actual Guice AbstractModule
            if (AbstractModule.class.isAssignableFrom(datastoreModuleClass)) {
                AbstractModule datastoreConcreteModule = 
                        (AbstractModule) datastoreModuleClass.newInstance();
                
                // Install the concrete module for the datastore to initialize bindings
                install(datastoreConcreteModule);
            } else {
                LOG.error("Provided datastore module class does not extend AbstractModule: {}",
                        datastoreConfig.getModuleClass());
            }
        } catch (ClassNotFoundException ex) {
            LOG.error("Unable to load datastore module class. Entities will not be saved: ", ex);
        } catch (InstantiationException ex) {
            LOG.error("Unable to load datastore module class. Entities will not be saved: ", ex);
        } catch (IllegalAccessException ex) {
            LOG.error("Unable to load datastore module class. Entities will not be saved: ", ex);
        }
    }
}
