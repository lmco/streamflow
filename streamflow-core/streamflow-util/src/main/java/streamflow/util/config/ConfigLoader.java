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
import streamflow.model.config.StreamflowConfig;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader {

    public static Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    public static StreamflowConfig getConfig() {
        String yamlConfig = null;

        File yamlConfigFile = new File(StreamflowEnvironment.getConfDir(), "streamflow.yml");

        if (yamlConfigFile.exists()) {
            try {
                yamlConfig = FileUtils.readFileToString(yamlConfigFile, "UTF-8");

                LOG.info("Using user provided streamflow.yml: " + yamlConfigFile.getAbsolutePath());
            } catch (Exception ex) {
            }
        }

        if (yamlConfig == null) {
            try {
                yamlConfig = IOUtils.toString(Thread.currentThread().getContextClassLoader().
                        getResourceAsStream("streamflow.yml"), "UTF-8");

                LOG.info("Using classpath provided streamflow.yml");
            } catch (Exception ex) {
            }
        }

        if (yamlConfig == null) {
            LOG.info("streamflow.yml not found anywhere... using default configuration");

            return new StreamflowConfig();
        } else {
            // Parse the YAML configuration using the Jackson YAML factory
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            try {
                // Parse the yaml configuration using the specified mapping
                return objectMapper.readValue(yamlConfig, StreamflowConfig.class);
                
            } catch (Exception ex) {
                LOG.error("Unable to parse provided streamflow.yml configuration. "
                        + "Using default values: ", ex);

                // If a parsing error is encountered, use the default Streamflow configuration
                return new StreamflowConfig();
            }
        }
    }
}
