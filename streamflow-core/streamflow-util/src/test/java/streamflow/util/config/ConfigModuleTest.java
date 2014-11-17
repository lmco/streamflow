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
package streamflow.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import streamflow.model.config.DatastoreConfig;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.config.LoggerConfig;
import streamflow.model.config.ProxyConfig;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigModuleTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    public void loadConfigModuleAndValidateConfiguration() throws Exception {
        // Change streamflow home to use the specified temp folder
        StreamflowEnvironment.setStreamflowHome(tempFolder.getRoot().getAbsolutePath());
        StreamflowEnvironment.initialize();
        
        StreamflowConfig mockedConfig = new StreamflowConfig();
        
        // Write the mocked config to file so it is loaded by the config loader
        FileUtils.writeStringToFile(new File(StreamflowEnvironment.getConfDir(), "streamflow.yml"),
                mapper.writeValueAsString(mockedConfig));

        Injector injector = Guice.createInjector(new ConfigModule()); 

        StreamflowConfig streamflowConfig = injector.getInstance(StreamflowConfig.class);

        assertEquals("Mocked streamflow config and injected streamflow config should match",
                mockedConfig, streamflowConfig);

        ProxyConfig proxyConfig = injector.getInstance(ProxyConfig.class);

        assertEquals("Mocked proxy config and injected proxy config should match",
                mockedConfig.getProxy(), proxyConfig);

        LoggerConfig loggerConfig = injector.getInstance(LoggerConfig.class);

        assertEquals("Mocked logger config and injected logger config should match",
                mockedConfig.getLogger(), loggerConfig);

        DatastoreConfig datastoreConfig = injector.getInstance(DatastoreConfig.class);

        assertEquals("Mocked datastore config and injected datastore config should match",
                mockedConfig.getDatastore(), datastoreConfig);
    }
}
