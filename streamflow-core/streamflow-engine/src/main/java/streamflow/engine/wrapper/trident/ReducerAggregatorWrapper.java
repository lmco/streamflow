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

import streamflow.engine.framework.FrameworkException;
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.ReducerAggregator;
import storm.trident.tuple.TridentTuple;

public class ReducerAggregatorWrapper extends BaseWrapper<ReducerAggregator> implements ReducerAggregator {

    protected static final Logger LOG = LoggerFactory.getLogger(ReducerAggregatorWrapper.class);
    
    public ReducerAggregatorWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, ReducerAggregator.class);
    }

    @Override
    public Object init() {
        try {
            return getDelegate().init();
        } catch (FrameworkException ex) {
            LOG.error("init() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("init() threw an uncaught exception: ", ex);
            return null;
        }
    }

    @Override
    public Object reduce(Object curr, TridentTuple tuple) {
        try {
            return getDelegate().reduce(curr, tuple);
        } catch (FrameworkException ex) {
            LOG.error("reduce() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("reduce() threw an uncaught exception: ", ex);
            return null;
        }
    }
}
