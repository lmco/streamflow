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

public class ErrorInfo implements Serializable {

    private String error;

    private int errorTimeSecs;

    
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.error != null ? this.error.hashCode() : 0);
        hash = 17 * hash + this.errorTimeSecs;
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
        if ((this.error == null) ? (other.error != null) : !this.error.equals(other.error)) {
            return false;
        }
        if (this.errorTimeSecs != other.errorTimeSecs) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + "error=" + error + ", errorTimeSecs=" + errorTimeSecs + '}';
    }
}
