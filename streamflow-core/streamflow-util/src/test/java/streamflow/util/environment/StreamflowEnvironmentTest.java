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

import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class StreamflowEnvironmentTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test
    public void verifyInitializedDirectories() {
        // Change streamflow home to use the specified temp folder
        StreamflowEnvironment.setStreamflowHome(tempFolder.getRoot().getAbsolutePath());
        StreamflowEnvironment.initialize();
        
        // Test for valid intialized directory structure
        assertTrue("conf directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getConfDir()).isDirectory());
        assertTrue("lib directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getLibDir()).isDirectory());
        assertTrue("data directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getDataDir()).isDirectory());
        assertTrue("temp directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getTempDir()).isDirectory());
        assertTrue("logs directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getLogsDir()).isDirectory());
        assertTrue("files directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getFilesDir()).isDirectory());
        assertTrue("topologies directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getTopologiesDir()).isDirectory());
        assertTrue("frameworks directory created within streamflowHome", 
                FileUtils.getFile(StreamflowEnvironment.getFrameworksDir()).isDirectory());
    }
}
