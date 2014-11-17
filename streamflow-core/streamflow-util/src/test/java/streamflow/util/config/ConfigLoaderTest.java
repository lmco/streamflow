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
import java.io.File;
import java.io.IOException;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.config.ProxyConfig;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfigLoaderTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    
    @Test
    public void verfifyStreamflowHomeProvidedConfig() throws IOException {
        // Change streamflow home to use the specified temp folder
        StreamflowEnvironment.setStreamflowHome(tempFolder.getRoot().getAbsolutePath());
        StreamflowEnvironment.initialize();
        
        ProxyConfig mockedProxy = new ProxyConfig();
        mockedProxy.setHost("testHost");
        mockedProxy.setPort(7777);
        
        // Modify the mocked config with the proxy to ensure streamflowConfig is different than default
        StreamflowConfig mockedConfig = new StreamflowConfig();
        mockedConfig.setProxy(mockedProxy);
        
        FileUtils.writeStringToFile(new File(StreamflowEnvironment.getConfDir(), "streamflow.yml"),
                mapper.writeValueAsString(mockedConfig));

        StreamflowConfig loadedConfig = ConfigLoader.getConfig();

        // streamflow.yml file should match the config loaded by the ConfigLoader
        assertEquals("streamflow home config should match ConfigLoader streamflow config",
                mockedConfig, loadedConfig);
    }
    
    @Test
    public void verifyClasspathProvidedConfig() throws IOException {
        // Change streamflow home to use the specified temp folder
        StreamflowEnvironment.setStreamflowHome(tempFolder.getRoot().getAbsolutePath());
        StreamflowEnvironment.initialize();
        
        String yamlConfig = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                    getResourceAsStream("streamflow.yml"), "UTF-8");

        // Parse the yaml configuration using the specified mapping
        StreamflowConfig classpathConfig
                = mapper.readValue(yamlConfig, StreamflowConfig.class);

        StreamflowConfig loadedConfig = ConfigLoader.getConfig();

        // streamflow.yml file should match the config loaded by the ConfigLoader
        assertEquals("Classpath streamflow config should match ConfigLoader streamflow config",
                classpathConfig, loadedConfig);
    }
}
