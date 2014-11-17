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
package streamflow.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import streamflow.datastore.core.KafkaDao;
import streamflow.model.kafka.KafkaCluster;
import streamflow.model.kafka.KafkaTopic;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.util.IDUtils;
import streamflow.service.util.KafkaUtils;

@Singleton
public class KafkaService {
    
    private final KafkaDao kafkaDao;

    @Inject
    public KafkaService(KafkaDao kafkaDao) {
        this.kafkaDao = kafkaDao;
    }
    
    public List<KafkaCluster> listClusters() {
        List<KafkaCluster> clusters = new ArrayList<KafkaCluster>();
        for (KafkaCluster cluster : kafkaDao.findAll()) {
            cluster = getCluster(cluster.getId());
            clusters.add(cluster);
            /*
            if (KafkaUtils.getInstance().isAvailable(cluster.getZookeeperUri())) {
                cluster.setStatus("CONNECTED");
            } else {
                cluster.setStatus("DISCONNECTED");
            }
            */
        }
        
        return clusters;
    }
    
    public KafkaCluster addCluster(KafkaCluster kafkaCluster) {
        if (kafkaCluster == null) {
            throw new EntityInvalidException("The provided kafka cluster was NULL");
        }
        if (kafkaCluster.getName() == null || kafkaCluster.getZookeeperUri() == null) {
            throw new EntityInvalidException("The kafka cluster was missing required fields");
        }
        if (hasCluster(kafkaCluster.getName())) {
            throw new EntityConflictException(
                    "Kafka cluster with the specified name already exists: Name = " 
                            + kafkaCluster.getName());
        }
        
        kafkaCluster.setId(IDUtils.formatId(kafkaCluster.getName()));
        
        return kafkaDao.save(kafkaCluster);
    }
    
    public boolean hasCluster(String kafkaClusterName) {
        return kafkaDao.findByName(kafkaClusterName) != null;
    }
    
    public KafkaCluster getCluster(String kafkaClusterId) {
        KafkaCluster kafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (kafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Kafka cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }
        
        if (KafkaUtils.getInstance().isAvailable(kafkaCluster.getZookeeperUri())) {
            kafkaCluster.setStatus("CONNECTED");
        } else {
            kafkaCluster.setStatus("DISCONNECTED");
        }
        
        kafkaCluster.setBrokers(KafkaUtils.getInstance().listBrokers(kafkaCluster.getZookeeperUri()));
        
        kafkaCluster.setTopics(KafkaUtils.getInstance().listTopics(kafkaCluster.getZookeeperUri()));

        return kafkaCluster;
    }
    
    public boolean deleteCluster(String kafkaClusterId) {
        KafkaCluster kafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (kafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Kafka cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }

        kafkaDao.delete(kafkaCluster);
        
        return true;
    }
    
    public void updateCluster(String kafkaClusterId, KafkaCluster kafkaCluster) {
        KafkaCluster oldKafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (oldKafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Kafka cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }
        
        if (kafkaCluster == null) {
            throw new EntityInvalidException("The provided kafka cluster was NULL");
        }
        if (kafkaCluster.getName() == null || kafkaCluster.getZookeeperUri() == null) {
            throw new EntityInvalidException("The kafka cluster was missing required fields");
        }
        
        // TODO: CHECK FOR EXISTING CLUSTER WITH SAME NAME

        kafkaCluster.setId(kafkaClusterId);

        kafkaDao.update(kafkaCluster);
    }
    
    public List<KafkaTopic> listTopics(String kafkaClusterId) {
        KafkaCluster kafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (kafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }
        
        return KafkaUtils.getInstance().listTopics(kafkaCluster.getZookeeperUri());
    }
    
    public void addTopic(String kafkaClusterId, KafkaTopic topic) {
        KafkaCluster kafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (kafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }
        
        KafkaUtils.getInstance().addTopic(kafkaCluster.getZookeeperUri(), topic);
    }
    
    public void deleteTopic(String kafkaClusterId, String topicName) {
        KafkaCluster kafkaCluster = kafkaDao.findById(kafkaClusterId);
        if (kafkaCluster == null) {
            throw new EntityNotFoundException(
                    "Cluster with the specified ID could not be found: ID = " + kafkaClusterId);
        }
        
        KafkaUtils.getInstance().deleteTopic(kafkaCluster.getZookeeperUri(), topicName);
    }
}
