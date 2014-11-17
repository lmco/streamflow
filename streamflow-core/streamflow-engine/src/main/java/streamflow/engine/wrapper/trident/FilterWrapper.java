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
import storm.trident.operation.Filter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class FilterWrapper extends BaseWrapper<Filter> implements Filter {
    
    protected static final Logger LOG = LoggerFactory.getLogger(FilterWrapper.class);
    
    public FilterWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, Filter.class);
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
    public boolean isKeep(TridentTuple tuple) {
        try {
            return getDelegate().isKeep(tuple);
        } catch (FrameworkException ex) {
            LOG.error("isKeep() not delegated due to a Framework exception: ", ex);
            return false;
        } catch (Exception ex) {
            LOG.error("isKeep() threw an uncaught exception: ", ex);
            return false;
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
