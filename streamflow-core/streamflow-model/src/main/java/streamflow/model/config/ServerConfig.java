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

import java.io.Serializable;

public class ServerConfig implements Serializable {

    private int port = 8080;

    public ServerConfig() {
    }

    public int getPort() {
        if (System.getProperty("server.port") != null) {
            try {
                port = Integer.parseInt(System.getProperty("server.port"));
            } catch (Exception ex) {
            }
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.port;
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
        final ServerConfig other = (ServerConfig) obj;
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServerConfig{ port=" + port + '}';
    }
}
