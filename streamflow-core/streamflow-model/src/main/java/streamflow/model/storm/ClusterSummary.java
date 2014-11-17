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
import java.util.ArrayList;
import java.util.List;

public class ClusterSummary implements Serializable {

    private int nimbusUptimeSecs;

    private String nimbusConf;

    private List<SupervisorSummary> supervisors = new ArrayList<SupervisorSummary>();

    private List<TopologySummary> topologies = new ArrayList<TopologySummary>();

    
    public ClusterSummary() {
    }

    public int getNimbusUptimeSecs() {
        return nimbusUptimeSecs;
    }

    public void setNimbusUptimeSecs(int nimbusUptimeSecs) {
        this.nimbusUptimeSecs = nimbusUptimeSecs;
    }

    public List<SupervisorSummary> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<SupervisorSummary> supervisors) {
        this.supervisors = supervisors;
    }

    public List<TopologySummary> getTopologies() {
        return topologies;
    }

    public void setTopologies(List<TopologySummary> topologies) {
        this.topologies = topologies;
    }

    public String getNimbusConf() {
        return nimbusConf;
    }

    public void setNimbusConf(String nimbusConf) {
        this.nimbusConf = nimbusConf;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.nimbusUptimeSecs;
        hash = 47 * hash + (this.nimbusConf != null ? this.nimbusConf.hashCode() : 0);
        hash = 47 * hash + (this.supervisors != null ? this.supervisors.hashCode() : 0);
        hash = 47 * hash + (this.topologies != null ? this.topologies.hashCode() : 0);
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
        final ClusterSummary other = (ClusterSummary) obj;
        if (this.nimbusUptimeSecs != other.nimbusUptimeSecs) {
            return false;
        }
        if ((this.nimbusConf == null) ? (other.nimbusConf != null) 
                : !this.nimbusConf.equals(other.nimbusConf)) {
            return false;
        }
        if (this.supervisors != other.supervisors && (this.supervisors == null 
                || !this.supervisors.equals(other.supervisors))) {
            return false;
        }
        if (this.topologies != other.topologies && (this.topologies == null 
                || !this.topologies.equals(other.topologies))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ClusterSummary{" + "nimbusUptimeSecs=" + nimbusUptimeSecs 
                + ", nimbusConf=" + nimbusConf + ", supervisors=" + supervisors 
                + ", topologies=" + topologies + '}';
    }
}
