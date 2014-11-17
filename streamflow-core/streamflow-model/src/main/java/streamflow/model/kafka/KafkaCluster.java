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
package streamflow.model.kafka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

@Entity("kafkaCluster")
public class KafkaCluster implements streamflow.model.util.Entity<String>, Serializable {
    
    @Id
    private String id;
    
    private String name;
    
    private String zookeeperUri;

    private String status = "DISCONNECTED";
    
    private Date modified = new Date();
    
    @Transient
    private List<KafkaBroker> brokers = new ArrayList<KafkaBroker>();
    
    @Transient
    private List<KafkaTopic> topics = new ArrayList<KafkaTopic>();
    
    @Transient
    private List<KafkaGroup> consumerGroups = new ArrayList<KafkaGroup>();
    
    
    public KafkaCluster() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZookeeperUri() {
        return zookeeperUri;
    }

    public void setZookeeperUri(String zookeeperUri) {
        this.zookeeperUri = zookeeperUri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public List<KafkaBroker> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<KafkaBroker> brokers) {
        this.brokers = brokers;
    }

    public List<KafkaTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<KafkaTopic> topics) {
        this.topics = topics;
    }

    public List<KafkaGroup> getConsumerGroups() {
        return consumerGroups;
    }

    public void setConsumerGroups(List<KafkaGroup> consumerGroups) {
        this.consumerGroups = consumerGroups;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 17 * hash + (this.zookeeperUri != null ? this.zookeeperUri.hashCode() : 0);
        hash = 17 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 17 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 17 * hash + (this.brokers != null ? this.brokers.hashCode() : 0);
        hash = 17 * hash + (this.topics != null ? this.topics.hashCode() : 0);
        hash = 17 * hash + (this.consumerGroups != null ? this.consumerGroups.hashCode() : 0);
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
        final KafkaCluster other = (KafkaCluster) obj;
        if ((this.id == null) ? (other.id != null) 
                : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) 
                : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.zookeeperUri == null) ? (other.zookeeperUri != null) 
                : !this.zookeeperUri.equals(other.zookeeperUri)) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) 
                : !this.status.equals(other.status)) {
            return false;
        }
        if (this.modified != other.modified && (this.modified == null 
                || !this.modified.equals(other.modified))) {
            return false;
        }
        if (this.brokers != other.brokers && (this.brokers == null 
                || !this.brokers.equals(other.brokers))) {
            return false;
        }
        if (this.topics != other.topics && (this.topics == null 
                || !this.topics.equals(other.topics))) {
            return false;
        }
        if (this.consumerGroups != other.consumerGroups && (this.consumerGroups == null 
                || !this.consumerGroups.equals(other.consumerGroups))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KafkaCluster{" + "id=" + id + ", name=" + name + ", zookeeperUri=" + zookeeperUri 
                + ", status=" + status + ", modified=" + modified + ", brokers=" + brokers 
                + ", topics=" + topics + ", consumerGroups=" + consumerGroups + '}';
    }
}
