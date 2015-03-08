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
package streamflow.model.config;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.FileUtils;

public class LoggerConfig implements Serializable {

    private String level = "INFO";

    private String baseDir = FileUtils.getUserDirectoryPath()
            + File.separator + ".streamflow" + File.separator + "logs";

    private String formatPattern = 
            "%d{yyyy-MM-dd'T'HH:mm:ss.sss'Z',GMT} %p %X{topology} %X{project} %X{task} %X{component} %c - %m%n";

    public LoggerConfig() {
    }

    public String getLevel() {
        return System.getProperty("logger.level", level);
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBaseDir() {
        return System.getProperty("logger.baseDir", baseDir);
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getFormatPattern() {
        return System.getProperty("logger.formatPattern", formatPattern);
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.level != null ? this.level.hashCode() : 0);
        hash = 31 * hash + (this.baseDir != null ? this.baseDir.hashCode() : 0);
        hash = 31 * hash + (this.formatPattern != null ? this.formatPattern.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoggerConfig other = (LoggerConfig) obj;
        if ((this.level == null) ? (other.level != null) 
                : !this.level.equals(other.level)) {
            return false;
        }
        if ((this.baseDir == null) ? (other.baseDir != null) 
                : !this.baseDir.equals(other.baseDir)) {
            return false;
        }
        if ((this.formatPattern == null) ? (other.formatPattern != null) 
                : !this.formatPattern.equals(other.formatPattern)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LoggerConfig{" + "level=" + level + ", baseDir=" + baseDir 
                + ", formatPattern=" + formatPattern + '}';
    }
}
