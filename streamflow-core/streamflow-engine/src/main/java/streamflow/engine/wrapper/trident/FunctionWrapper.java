package streamflow.engine.wrapper.trident;

import java.util.Map;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.wrapper.BaseWrapper;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.config.StreamflowConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class FunctionWrapper extends BaseWrapper<Function> implements Function {
    
    protected static final Logger LOG = LoggerFactory.getLogger(FunctionWrapper.class);
    
    public FunctionWrapper(Topology topology, TopologyComponent component,
            boolean isCluster, StreamflowConfig configuration) throws FrameworkException {
        super(topology, component, isCluster, configuration, Function.class);
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
    public void execute(TridentTuple tuple, TridentCollector collector) {
        try {
            getDelegate().execute(tuple, collector);
        } catch (FrameworkException ex) {
            LOG.error("execute() not delegated due to a Framework exception: ", ex);
        } catch (Exception ex) {
            LOG.error("execute() threw an uncaught exception: ", ex);
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
