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
package streamflow.bolt.core;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;

public class TupleLogger extends BaseRichBolt {

    private Logger logger;
    
    private ObjectMapper objectMapper;
    
    private int counter = 0;
    
    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void prepare(Map config, TopologyContext context,
            OutputCollector collector) {
        logger.info("Tuple Logger Started");
        
        // Initialize the object mapper with specific features
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            // Serialize the activity object to a JSON string
            logger.info("Printing Tuple (" + ++counter + "): ");
            logger.info(objectMapper.writeValueAsString(tuple));
        } catch (JsonProcessingException ex) {
        }
    }
    
    @Override
    public void cleanup() {
        logger.info("Tuple Logger Stopped");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
    }
}
