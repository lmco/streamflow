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

import java.util.Map;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import storm.trident.operation.Aggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class AggregatorWrapper extends BaseWrapper<Aggregator> implements Aggregator {

    protected static final Logger LOG = LoggerFactory.getLogger(AggregatorWrapper.class);
    
    public AggregatorWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, Aggregator.class);
    }

    @Override
    public Object init(Object batchId, TridentCollector collector) {
        try {
            return getDelegate().init(batchId, collector);
        } catch (FrameworkException ex) {
            LOG.error("init() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("init() threw an uncaught exception: ", ex);
            return null;
        }
    }

    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        try {
            getDelegate().prepare(conf, context);
        } catch (FrameworkException ex) {
            LOG.error("prepare() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("prepare() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public void aggregate(Object val, TridentTuple tuple, TridentCollector collector) {
        try {
            getDelegate().aggregate(val, tuple, collector);
        } catch (FrameworkException ex) {
            LOG.error("aggregate() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("aggregate() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public void complete(Object val, TridentCollector collector) {
        try {
            getDelegate().complete(val, collector);
        } catch (FrameworkException ex) {
            LOG.error("complete() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("complete() threw an uncaught exception: ", ex);
        }
    }

    @Override
    public void cleanup() {
        try {
            getDelegate().cleanup();
        } catch (FrameworkException ex) {
            LOG.error("cleanup() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("cleanup() threw an uncaught exception: ", ex);
        }
    }
}
