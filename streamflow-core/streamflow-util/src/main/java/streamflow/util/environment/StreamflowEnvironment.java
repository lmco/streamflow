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
package streamflow.util.environment;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamflowEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(StreamflowEnvironment.class);
    
    private static String streamflowHome = 
            FileUtils.getUserDirectoryPath() + File.separator + ".streamflow";
    
    private StreamflowEnvironment() {
    }
    
    public static void setStreamflowHome(String newStreamflowHome) {
        if (newStreamflowHome != null) {
            streamflowHome = newStreamflowHome;
        }
    }
    
    public static String getStreamflowHome() {
        // If streamflow home was set, update the streamflow home for the the environment
        if (System.getenv("STREAMFLOW_HOME") != null) {
            streamflowHome = System.getenv("STREAMFLOW_HOME");
        } else if (System.getProperty("streamflow.home") != null) {
            streamflowHome = System.getProperty("streamflow.home");
        }
        return streamflowHome;
    }
    
    public static String getConfDir() {
        return getStreamflowHome() + File.separator + "conf";
    }
    
    public static String getDataDir() {
        return getStreamflowHome() + File.separator + "data";
    }
    
    public static String getLibDir() {
        return getStreamflowHome() + File.separator + "lib";
    }
    
    public static String getLogsDir() {
        return getStreamflowHome() + File.separator + "logs";
    }
    
    public static String getTempDir() {
        return getStreamflowHome() + File.separator + "temp";
    }
    
    public static String getTopologiesDir() {
        return getTempDir() + File.separator + "topologies";
    }
    
    public static String getFrameworksDir() {
        return getTempDir() + File.separator + "frameworks";
    }
    
    public static String getFilesDir() {
        return getDataDir() + File.separator + "files";
    }

    public static void initialize() {
        // Create all of the necessary streamflow directories
        new File(getConfDir()).mkdirs();
        new File(getDataDir()).mkdirs();
        new File(getLibDir()).mkdirs();
        new File(getLogsDir()).mkdirs();
        new File(getTempDir()).mkdirs();
        new File(getTopologiesDir()).mkdirs();
        new File(getFrameworksDir()).mkdirs();
        new File(getFilesDir()).mkdirs();
    }
}
