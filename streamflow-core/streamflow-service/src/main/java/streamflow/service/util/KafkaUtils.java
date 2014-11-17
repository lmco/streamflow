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
package streamflow.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import streamflow.model.kafka.KafkaBroker;
import streamflow.model.kafka.KafkaTopic;
import kafka.admin.AdminUtils;
import org.I0Itec.zkclient.ZkClient;

public class KafkaUtils {
    
    private static KafkaUtils singleton;
    
    private final HashMap<String, CuratorFramework> curatorClients = 
            new HashMap<String, CuratorFramework>();
                    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public static final int SESSION_TIMEOUT_MS = 10000;
    
    public static final int CONNECTION_TIMEOUT_MS = 2000;
    
    private KafkaUtils() {
    }
    
    public static KafkaUtils getInstance() {
        if (singleton == null) {
            singleton = new KafkaUtils();
        }
        return singleton;
    }
    
    public boolean isAvailable(String zookeeperUri) {
        List<KafkaBroker> kafkaBrokers = listBrokers(zookeeperUri);
        return (kafkaBrokers != null && !kafkaBrokers.isEmpty());
    }
    
    public List<KafkaBroker> listBrokers(String zookeeperUri) {
        List<KafkaBroker> kafkaBrokers = null;
        
        // Get the updated status for the cluster from zookeeper
        CuratorFramework curatorClient = getCuratorClient(zookeeperUri);
        if (curatorClient != null) {
            try {
                kafkaBrokers = new ArrayList<KafkaBroker>();
                
                for (String brokerId : curatorClient.getChildren().forPath("/brokers/ids")) {
                    byte[] brokerData = curatorClient.getData().forPath("/brokers/ids/" + brokerId);
                    
                    // Parse the broker data provided as a JSON string
                    Map<String, Object> brokerObject = objectMapper.readValue(brokerData, Map.class);
                    
                    KafkaBroker kafkaBroker = new KafkaBroker();
                    kafkaBroker.setId(brokerId);
                    kafkaBroker.setHost((String) brokerObject.get("host"));
                    kafkaBroker.setPort((Integer) brokerObject.get("port"));
                    kafkaBroker.setJmxPort((Integer) brokerObject.get("jmx_port"));
                    
                    kafkaBrokers.add(kafkaBroker);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                
                kafkaBrokers = null;
            }
        }
        
        return kafkaBrokers;
    }
    
    public KafkaBroker getBroker(String zookeeperUri, String brokerId) {
        KafkaBroker kafkaBroker = null;
        
        // Get the updated status for the cluster from zookeeper
        CuratorFramework curatorClient = getCuratorClient(zookeeperUri);
        if (curatorClient != null) {
            try {
                byte[] brokerData = curatorClient.getData().forPath("/brokers/ids/" + brokerId);

                // Parse the broker data provided as a JSON string
                Map<String, Object> brokerObject = objectMapper.readValue(brokerData, Map.class);

                kafkaBroker = new KafkaBroker();
                kafkaBroker.setId(brokerId);
                kafkaBroker.setHost((String) brokerObject.get("host"));
                kafkaBroker.setPort((Integer) brokerObject.get("port"));
                kafkaBroker.setJmxPort((Integer) brokerObject.get("jmx_port"));
                
            } catch (Exception ex) {
                ex.printStackTrace();
                
                kafkaBroker = null;
            }
        }
        
        return kafkaBroker;
    }
    
    public List<KafkaTopic> listTopics(String zookeeperUri) {
        List<KafkaTopic> kafkaTopics = null;
        
        // Get the updated status for the cluster from zookeeper
        CuratorFramework curatorClient = getCuratorClient(zookeeperUri);
        if (curatorClient != null) {
            try {
                kafkaTopics = new ArrayList<KafkaTopic>();
                
                for (String topic : curatorClient.getChildren().forPath("/brokers/topics")) {
                    KafkaTopic kafkaTopic = new KafkaTopic();
                    kafkaTopic.setName(topic);
                    
                    kafkaTopics.add(kafkaTopic);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return kafkaTopics;
    }
    
    public void addTopic(String zookeeperUri, KafkaTopic topic) {
        ZkClient zkClient = getZkClient(zookeeperUri);
        
        Properties topicConfig = new Properties();
        for (Entry<String, String> property : topic.getConfig().entrySet()) {
            topicConfig.setProperty(property.getKey(), property.getValue());
        }
        
        AdminUtils.createTopic(zkClient, topic.getName(), topic.getNumPartitions(), 
                topic.getReplicationFactor(), topicConfig);
    }
    
    public KafkaTopic getTopic(String zookeeperUri, String topic) {
        
        return null;
    }
    
    public void deleteTopic(String zookeeperUri, String topic) {
        ZkClient zkClient = getZkClient(zookeeperUri);
        
        AdminUtils.deleteTopic(zkClient, topic);
    }
    
    public ZkClient getZkClient(String zookeeperUri) {
        return new ZkClient(zookeeperUri, SESSION_TIMEOUT_MS, CONNECTION_TIMEOUT_MS);
    }
    
    public CuratorFramework getCuratorClient(String zookeeperUri) {
        return getCuratorClient(zookeeperUri, zookeeperUri);
    }
    
    public CuratorFramework getCuratorClient(String id, String zookeeperUri) {
        CuratorFramework curatorClient = curatorClients.get(id);
        if (curatorClient == null) {
            try {
                curatorClient = CuratorFrameworkFactory.builder()
                        .connectString(zookeeperUri)
                        .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                        .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                        .retryPolicy(new ExponentialBackoffRetry(1000, 1))
                        .build();
                
                curatorClient.start();
                
                curatorClients.put(id, curatorClient);
            } catch (Exception ex) {
                ex.printStackTrace();
                
                curatorClient = null;
            }
        }
        return curatorClient;
    }
    
    @Override
    public KafkaUtils clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException("KafkaUtils can not be cloned");
    }
}
