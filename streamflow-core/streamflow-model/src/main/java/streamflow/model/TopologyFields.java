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
package streamflow.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.mongodb.morphia.annotations.Embedded;
import java.io.Serializable;

@Embedded
@JsonInclude(Include.NON_NULL)
public class TopologyFields implements Serializable {

    private String input;

    private String output;

    
    public TopologyFields() {
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.input != null ? this.input.hashCode() : 0);
        hash = 31 * hash + (this.output != null ? this.output.hashCode() : 0);
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
        final TopologyFields other = (TopologyFields) obj;
        if ((this.input == null) ? (other.input != null) 
                : !this.input.equals(other.input)) {
            return false;
        }
        if ((this.output == null) ? (other.output != null) 
                : !this.output.equals(other.output)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyFields{" + "input=" + input + ", output=" + output + '}';
    }
}
