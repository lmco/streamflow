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

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import java.util.HashMap;
import java.util.Map;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.hook.SpoutMetricsHook;
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichSpoutWrapper extends BaseWrapper<IRichSpout> implements IRichSpout {

    protected static final Logger LOG = LoggerFactory.getLogger(RichSpoutWrapper.class);

    public RichSpoutWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, IRichSpout.class);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.context = context;
        
        try {
            // Register the metrics hook for this bolt to track statistics
            context.addTaskHook(new SpoutMetricsHook());
            
            getDelegate().open(conf, context, collector);
        } catch (FrameworkException ex) {
            LOG.error("open() not delegated due to a Framework exception: ", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            getDelegate().close();
        } catch (FrameworkException ex) {
            LOG.error("close() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void activate() {
        try {
            getDelegate().activate();
        } catch (FrameworkException ex) {
            LOG.error("activate() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void deactivate() {
        try {
            getDelegate().deactivate();
        } catch (FrameworkException ex) {
            LOG.error("deactivate() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void nextTuple() {
        try {
            getDelegate().nextTuple();
        } catch (FrameworkException ex) {
            LOG.error("nextTuple() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void ack(Object msgId) {
        try {
            getDelegate().ack(msgId);
        } catch (FrameworkException ex) {
            LOG.error("ack() not delegated due to a Framework exception: ", ex);
        }
    }

    @Override
    public void fail(Object msgId) {
        try {
            getDelegate().fail(msgId);
        } catch (FrameworkException ex) {
            LOG.error("fail() not delegated due to a Framework exception: ", ex);
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
