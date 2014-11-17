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
import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

public class CombinerAggregatorWrapper extends BaseWrapper<CombinerAggregator> implements CombinerAggregator {

    protected static final Logger LOG = LoggerFactory.getLogger(CombinerAggregatorWrapper.class);
    
    public CombinerAggregatorWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, CombinerAggregator.class);
    }

    @Override
    public Object init(TridentTuple tuple) {
        try {
            return getDelegate().init(tuple);
        } catch (FrameworkException ex) {
            LOG.error("init() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("init() threw an uncaught exception: ", ex);
            return null;
        }
    }

    @Override
    public Object combine(Object val1, Object val2) {
        try {
            return getDelegate().combine(val1, val2);
        } catch (FrameworkException ex) {
            LOG.error("combine() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("combine() threw an uncaught exception: ", ex);
            return null;
        }
    }

    @Override
    public Object zero() {
        try {
            return getDelegate().zero();
        } catch (FrameworkException ex) {
            LOG.error("zero() not delegated due to a Framework exception: ", ex);
            return null;
        } catch (Exception ex) {
            LOG.error("zero() threw an uncaught exception: ", ex);
            return null;
        }
    }
}
