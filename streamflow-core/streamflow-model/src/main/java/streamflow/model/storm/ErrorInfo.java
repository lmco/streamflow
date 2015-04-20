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
package streamflow.model.storm;

import java.io.Serializable;
import java.util.Objects;

public class ErrorInfo implements Serializable {

    private String error;

    private int errorTimeSecs;
    
    private String host;
    
    private int port;

    
    public ErrorInfo() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorTimeSecs() {
        return errorTimeSecs;
    }

    public void setErrorTimeSecs(int errorTimeSecs) {
        this.errorTimeSecs = errorTimeSecs;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.error);
        hash = 37 * hash + this.errorTimeSecs;
        hash = 37 * hash + Objects.hashCode(this.host);
        hash = 37 * hash + this.port;
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
        final ErrorInfo other = (ErrorInfo) obj;
        if (!Objects.equals(this.error, other.error)) {
            return false;
        }
        if (this.errorTimeSecs != other.errorTimeSecs) {
            return false;
        }
        if (!Objects.equals(this.host, other.host)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + "error=" + error + ", errorTimeSecs=" + errorTimeSecs 
                + ", host=" + host + ", port=" + port + '}';
    }
}
