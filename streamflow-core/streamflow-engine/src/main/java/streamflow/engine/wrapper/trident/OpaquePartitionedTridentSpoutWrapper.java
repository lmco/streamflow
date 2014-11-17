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
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.spout.IOpaquePartitionedTridentSpout;

public class OpaquePartitionedTridentSpoutWrapper extends BaseWrapper<IOpaquePartitionedTridentSpout> 
        implements IOpaquePartitionedTridentSpout {

    protected static final Logger LOG = LoggerFactory.getLogger(OpaquePartitionedTridentSpoutWrapper.class);
    
    public OpaquePartitionedTridentSpoutWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, IOpaquePartitionedTridentSpout.class);
    }

    @Override
    public Coordinator getCoordinator(Map conf, TopologyContext context) {
        try {
            return getDelegate().getCoordinator(conf, context);
        } catch (FrameworkException ex) {
            LOG.error("getCoordinator() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("getCoordinator() threw an uncaught exception: ", ex);
            return null;
        }
    }

    @Override
    public Emitter getEmitter(Map conf, TopologyContext context) {
        try {
            return getDelegate().getEmitter(conf, context);
        } catch (FrameworkException ex) {
            LOG.error("getEmitter() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("getEmitter() threw an uncaught exception: ", ex);
            return null;
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
    public Map<String, Object> getComponentConfiguration() {
        try {
            return getDelegate().getComponentConfiguration();
        } catch (FrameworkException ex) {
            LOG.error("getComponentConfiguration() not delegated due to a Framework exception: ", ex);
            return new HashMap();
        } catch (Exception ex) {
            LOG.error("getComponentConfiguration() threw an uncaught exception: ", ex);
            return new HashMap();
        }
    }
}
