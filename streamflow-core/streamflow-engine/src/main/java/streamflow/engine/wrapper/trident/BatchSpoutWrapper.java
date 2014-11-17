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
package streamflow.engine.wrapper.trident;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
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
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

public class BatchSpoutWrapper extends BaseWrapper<IBatchSpout> implements IBatchSpout {

    protected static final Logger LOG = LoggerFactory.getLogger(BatchSpoutWrapper.class);
    
    public BatchSpoutWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, IBatchSpout.class);
    }

    @Override
    public void open(Map conf, TopologyContext context) {
        try {
            // Register the metrics hook for this bolt to track statistics
            context.addTaskHook(new SpoutMetricsHook());
            
            getDelegate().open(conf, context);
        } catch (FrameworkException ex) {
            LOG.error("open() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("open() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public void emitBatch(long batchId, TridentCollector collector) {
        try {
            getDelegate().emitBatch(batchId, collector);
        } catch (FrameworkException ex) {
            LOG.error("emitBatch() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("emitBatch() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public void ack(long batchId) {
        try {
            getDelegate().ack(batchId);
        } catch (FrameworkException ex) {
            LOG.error("ack() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("ack() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public Fields getOutputFields() {
        try {
            return getDelegate().getOutputFields();
        } catch (FrameworkException ex) {
            LOG.error("getOutputFields() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("getOutputFields() threw an uncaught exception: ", ex);
            return null;
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
    public Map<String, Object> getComponentConfiguration() {
        try {
            return getDelegate().getComponentConfiguration();
        } catch (FrameworkException ex) {
            LOG.error("getComponentConfiguration() not delegated due to a Framework exception: ", ex);
            return new HashMap();
        }
    }
}
