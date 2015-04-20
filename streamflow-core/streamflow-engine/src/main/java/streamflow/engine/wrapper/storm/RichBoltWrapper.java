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
package streamflow.engine.wrapper.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import java.util.HashMap;
import java.util.Map;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.hook.BoltMetricsHook;
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichBoltWrapper extends BaseWrapper<IRichBolt> implements IRichBolt {

    protected static final Logger LOG = LoggerFactory.getLogger(RichBoltWrapper.class);
    
    public RichBoltWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, IRichBolt.class);
    }

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.context = context;
        
        try {
            // Register the metrics hook for this bolt to track statistics
            context.addTaskHook(new BoltMetricsHook());

            getDelegate().prepare(conf, context, collector);
        } catch (FrameworkException ex) {
            LOG.error("prepare() not delegated due to a Framework exception: ", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            getDelegate().execute(tuple);
        } catch (FrameworkException ex) {
            LOG.error("execute() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void cleanup() {
        try {
            getDelegate().cleanup();
        } catch (FrameworkException ex) {
            LOG.error("cleanup() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        try {
            getDelegate().declareOutputFields(declarer);
        } catch (FrameworkException ex) {
            LOG.error("declareOutputFields() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        try {
            return getDelegate().getComponentConfiguration();
        } catch (FrameworkException ex) {
            LOG.error("getComponentConfiguration() not delegated due to a Framework exception: ", ex);
            return new HashMap();
        }
    }
}
