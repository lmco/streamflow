package streamflow.engine.topology.trident;

import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import java.util.ArrayList;
import java.util.List;
import streamflow.engine.framework.FrameworkException;
import streamflow.engine.topology.TopologyCreator;
import streamflow.engine.wrapper.storm.RichSpoutWrapper;
import streamflow.engine.wrapper.trident.AggregatorWrapper;
import streamflow.engine.wrapper.trident.BatchSpoutWrapper;
import streamflow.engine.wrapper.trident.CombinerAggregatorWrapper;
import streamflow.engine.wrapper.trident.FilterWrapper;
import streamflow.engine.wrapper.trident.FunctionWrapper;
import streamflow.engine.wrapper.trident.ReducerAggregatorWrapper;
import streamflow.engine.wrapper.trident.TridentSpoutWrapper;
import streamflow.model.Component;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologyConnector;
import streamflow.model.config.StreamflowConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.fluent.ChainedAggregatorDeclarer;
import storm.trident.fluent.GroupedStream;
import storm.trident.fluent.IAggregatableStream;

public class TridentTopologyCreator implements TopologyCreator {
    
    protected static Logger LOG = LoggerFactory.getLogger(TridentTopologyCreator.class);
    
    public static final String TYPE = "TRIDENT";
    
    private final TridentTopology tridentTopology = new TridentTopology();
    
    // Builder related variables
    private Stream stream = null;
    private GroupedStream groupedStream = null;
    private IAggregatableStream aggregatableStream = null;
    private ChainedAggregatorDeclarer chainedStream = null;
    private ActiveStream activeStream = ActiveStream.NONE;

    @Override
    public StormTopology build(Topology topology, StreamflowConfig configuration, boolean isClusterMode) 
            throws FrameworkException {
        
        TopologyComponent component = null;
        
        try {
            // Iterate over all of the components to build the topology
            while ((component = getNextComponent(topology.getDeployedConfig(), component)) != null) {
                // Parse input and output fields in case they are needed (empty lists are generated for null fields)
                List<String> inputFields = parseFieldsList(component.getFields().getInput());
                List<String> outputFields = parseFieldsList(component.getFields().getOutput());
            
                // Initialize the component based on its component type
                if (component.getType().equalsIgnoreCase(Component.TRIDENT_DRPC_TYPE)) {
                    // Get the name of the DRPC function from the properties
                    String drpcFunction = component.getProperties().get("drpc-function");
                            
                    stream = tridentTopology.newDRPCStream(drpcFunction);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.STORM_SPOUT_TYPE)) {
                    RichSpoutWrapper richSpoutWrapper = new RichSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    stream = tridentTopology.newStream(component.getKey(), richSpoutWrapper);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_SPOUT_TYPE)) {
                    TridentSpoutWrapper tridentSpoutWrapper = new TridentSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    stream = tridentTopology.newStream(component.getKey(), tridentSpoutWrapper);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_BATCH_SPOUT_TYPE)) {
                    BatchSpoutWrapper batchSpoutWrapper = new BatchSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    stream = tridentTopology.newStream(component.getKey(), batchSpoutWrapper);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_PARTITIONED_SPOUT_TYPE)) {
                    BatchSpoutWrapper batchSpoutWrapper = new BatchSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    stream = tridentTopology.newStream(component.getKey(), batchSpoutWrapper);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_OPAQUE_PARTITIONED_SPOUT_TYPE)) {
                    BatchSpoutWrapper batchSpoutWrapper = new BatchSpoutWrapper(
                            topology, component, isClusterMode, configuration);

                    stream = tridentTopology.newStream(component.getKey(), batchSpoutWrapper);
                    setActiveStream(ActiveStream.STREAM);
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_FUNCTION_TYPE)) {
                    FunctionWrapper function = new FunctionWrapper(
                            topology, component, isClusterMode, configuration); 
                   
                    if (isActiveStream(ActiveStream.STREAM)) {
                        if (inputFields.isEmpty()) {
                            stream = stream.each(function, new Fields(outputFields));
                        } else {
                            stream = stream.each(new Fields(inputFields), function, new Fields(outputFields));
                        }
                    } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
                        if (inputFields.isEmpty()) {
                            aggregatableStream = groupedStream.each(
                                    new Fields(inputFields), function, new Fields(outputFields));
                            setActiveStream(ActiveStream.AGGREGATABLE_STREAM);
                        }
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_FILTER_TYPE)) {
                    FilterWrapper filter = new FilterWrapper(
                            topology, component, isClusterMode, configuration);

                    if (isActiveStream(ActiveStream.STREAM)) {
                        if (!inputFields.isEmpty()) {
                            stream = stream.each(new Fields(inputFields), filter);
                        }
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_COMBINER_AGGREGATOR_TYPE)) {
                    CombinerAggregatorWrapper combinerAggregator = new CombinerAggregatorWrapper(
                            topology, component, isClusterMode, configuration);
                    
                    boolean partitionAggregate = false;
                    
                    String partitionAggregateProperty = component.getProperties().get("partition-aggregate");
                    if (StringUtils.isNotBlank(partitionAggregateProperty)) {
                        if (partitionAggregateProperty.equalsIgnoreCase("true")) {
                            partitionAggregate = true;
                        }
                    }
                    
                    if (isActiveStream(ActiveStream.STREAM)) {
                        if (inputFields.isEmpty()) {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        combinerAggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        combinerAggregator, new Fields(outputFields));
                            }
                        } else {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        new Fields(inputFields), combinerAggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        new Fields(inputFields), combinerAggregator, new Fields(outputFields));
                            }
                        }
                    } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
                        if (inputFields.isEmpty()) {
                            stream = groupedStream.aggregate(
                                    combinerAggregator, new Fields(outputFields));
                            setActiveStream(ActiveStream.STREAM);
                        } else {
                            stream = groupedStream.aggregate(
                                    new Fields(inputFields), combinerAggregator, new Fields(outputFields));
                            setActiveStream(ActiveStream.STREAM);
                        }
                        setActiveStream(ActiveStream.STREAM);
                    }
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_REDUCER_AGGREGATOR_TYPE)) {
                    ReducerAggregatorWrapper reducerAggregator = new ReducerAggregatorWrapper(
                            topology, component, isClusterMode, configuration);
                    
                    boolean partitionAggregate = false;
                    
                    String partitionAggregateProperty = component.getProperties().get("partition-aggregate");
                    if (StringUtils.isNotBlank(partitionAggregateProperty)) {
                        if (partitionAggregateProperty.equalsIgnoreCase("true")) {
                            partitionAggregate = true;
                        }
                    }

                    if (isActiveStream(ActiveStream.STREAM)) {
                        if (inputFields.isEmpty()) {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        reducerAggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        reducerAggregator, new Fields(outputFields));
                            }
                        } else {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        new Fields(inputFields), reducerAggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        new Fields(inputFields), reducerAggregator, new Fields(outputFields));
                            }
                        }
                    } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {if (inputFields.isEmpty()) {
                            stream = groupedStream.aggregate(
                                    reducerAggregator, new Fields(outputFields));
                            setActiveStream(ActiveStream.STREAM);
                        } else {
                            stream = groupedStream.aggregate(
                                    new Fields(inputFields), reducerAggregator, new Fields(outputFields));
                            setActiveStream(ActiveStream.STREAM);
                        }
                        setActiveStream(ActiveStream.STREAM);
                    }
                    
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_AGGREGATOR_TYPE)) {
                    AggregatorWrapper aggregator = new AggregatorWrapper(
                            topology, component, isClusterMode, configuration);
                    
                    boolean partitionAggregate = false;
                    
                    String partitionAggregateProperty = component.getProperties().get("partition-aggregate");
                    if (StringUtils.isNotBlank(partitionAggregateProperty)) {
                        if (partitionAggregateProperty.equalsIgnoreCase("true")) {
                            partitionAggregate = true;
                        }
                    }
                    
                    if (isActiveStream(ActiveStream.STREAM)) {
                        if (inputFields.isEmpty()) {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        aggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        aggregator, new Fields(outputFields));
                            }
                        } else {
                            if (partitionAggregate) {
                                stream = stream.partitionAggregate(
                                        new Fields(inputFields), aggregator, new Fields(outputFields));
                            } else {
                                stream = stream.aggregate(
                                        new Fields(inputFields), aggregator, new Fields(outputFields));
                            }
                        }
                    } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
                        if (inputFields.isEmpty()) {
                            stream = groupedStream.aggregate(
                                    aggregator, new Fields(outputFields));
                            setActiveStream(ActiveStream.STREAM);
                        } else {
                            if (partitionAggregate) {
                                aggregatableStream = groupedStream.partitionAggregate(
                                        new Fields(inputFields), aggregator, new Fields(outputFields));
                                setActiveStream(ActiveStream.AGGREGATABLE_STREAM);
                            } else {
                                stream = groupedStream.aggregate(
                                        new Fields(inputFields), aggregator, new Fields(outputFields));
                                setActiveStream(ActiveStream.STREAM);
                            }
                        }
                        setActiveStream(ActiveStream.STREAM);
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_CHAINED_AGG_START_TYPE)) {
                    if (isActiveStream(ActiveStream.STREAM)) {
                        chainedStream = stream.chainedAgg();
                        setActiveStream(ActiveStream.CHAINED_AGGREGATOR_STREAM);
                    } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
                        chainedStream = groupedStream.chainedAgg();
                        setActiveStream(ActiveStream.CHAINED_AGGREGATOR_STREAM);
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_CHAINED_AGG_END_TYPE)) {
                    if (isActiveStream(ActiveStream.CHAINED_AGGREGATOR_STREAM)) {
                        stream = chainedStream.chainEnd();
                        setActiveStream(ActiveStream.STREAM);
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_AGG_PARTITION_TYPE)) {
                    if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
                        aggregatableStream = groupedStream.aggPartition(groupedStream);
                        setActiveStream(ActiveStream.CHAINED_AGGREGATOR_STREAM);
                    }
                } else if (component.getType().equalsIgnoreCase(Component.TRIDENT_PROJECT_TYPE)) {
                    if (isActiveStream(ActiveStream.STREAM)) {
                        String projectProperty = component.getProperties().get("project-fields");
                        
                        if (StringUtils.isNotBlank(projectProperty)) {
                            List<String> projectList = parseFieldsList(projectProperty);
                            if (!projectList.isEmpty()) {
                                stream = stream.project(new Fields(projectList));
                            }
                        }
                    }
                }

                // TODO: ADDIITONAL TRIDENT WRAPPER IMPLMENTATIONS CALLED HERE

                // Apply the parallelism hint to the stream if necessary
                if (component.getParallelism() > 1) {
                    if (isActiveStream(ActiveStream.STREAM)) {
                        stream = stream.parallelismHint(component.getParallelism());
                    }
                }

                // Check if an outgoing connector was specified
                TopologyConnector connector = getConnectorForComponent(
                        topology.getDeployedConfig(), component);
                if (connector != null) {
                    if (connector.getGrouping().equalsIgnoreCase("Default")) {
                        // USE THE DEFAULT GROUPING WHICH DOESNT REQUIRE AN API CALL
                    } else if (connector.getGrouping().equalsIgnoreCase("Shuffle")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            stream = stream.shuffle();
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Broadcast")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            stream = stream.broadcast();
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Global")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            stream = stream.global();
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Batch Global")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            stream = stream.batchGlobal();
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Partition By")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            stream = stream.partitionBy(
                                    new Fields(parseFieldsList(connector.getGroupingRef())));
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Group By")) {
                        if (isActiveStream(ActiveStream.STREAM)) {
                            groupedStream = stream.groupBy(
                                    new Fields(parseFieldsList(connector.getGroupingRef())));
                            setActiveStream(ActiveStream.GROUPED_STREAM);
                        }
                    } else if (connector.getGrouping().equalsIgnoreCase("Partition")) {
                        // TODO: SUPPORT CONTRIBUTION OF CUSTOM GROUPING CLASSES
                    } 
                }
            }
        } catch (FrameworkException frameworkException) {
            throw frameworkException;
        } catch (Exception ex) {
            LOG.error("Uncaught exception in TridentEngineTopology: ", ex);
            
            throw new FrameworkException(ex.getMessage());
        }
        
        return tridentTopology.build();
    }
    
    private TopologyComponent getNextComponent(TopologyConfig topologyConfig, 
            TopologyComponent currentComponent) {
        TopologyComponent nextComponent = null;
        
        // Current component is null which means we need a spout to start the stream
        if (currentComponent == null) {
            // Iterate over the config to get the first availabe spout
            for (TopologyComponent component : topologyConfig.getComponents().values()) {
                // Check if the type matches any of the supported spout types
                if (component.getType().equalsIgnoreCase(Component.TRIDENT_DRPC_TYPE) ||
                    component.getType().equalsIgnoreCase(Component.STORM_SPOUT_TYPE) ||
                    component.getType().equalsIgnoreCase(Component.TRIDENT_SPOUT_TYPE) ||
                    component.getType().equalsIgnoreCase(Component.TRIDENT_BATCH_SPOUT_TYPE) ||
                    component.getType().equalsIgnoreCase(Component.TRIDENT_PARTITIONED_SPOUT_TYPE) ||
                    component.getType().equalsIgnoreCase(Component.TRIDENT_OPAQUE_PARTITIONED_SPOUT_TYPE)) {
                    nextComponent = component;
                    break;
                }
            }
        } else {
            TopologyConnector currentConnector = getConnectorForComponent(topologyConfig, currentComponent);
            
            // If the connector is not null then it means we haven't reached the end yet
            if (currentConnector != null) {
                String nextComponentKey = currentConnector.getTargetComponentKey();
                
                nextComponent = topologyConfig.getComponents().get(nextComponentKey);
            }
        }
        
        return nextComponent;
    }
    
    private TopologyConnector getConnectorForComponent(TopologyConfig topologyConfig,
            TopologyComponent component) {
        TopologyConnector topologyConnector = null;
        
        for (TopologyConnector connector : topologyConfig.getConnectors().values()) {
            // Check if the source matches the component key
            if (connector.getSourceComponentKey().equals(component.getKey())) {
                topologyConnector = connector;
                break;
            }
        }
        
        return topologyConnector;
    }
    
    private boolean isActiveStream(ActiveStream activeStream) {
        if (this.activeStream == activeStream) {
            if (activeStream == ActiveStream.STREAM && stream != null) {
                return true;
            } else if (activeStream == ActiveStream.GROUPED_STREAM && groupedStream != null) {
                return true;
            } else if (activeStream == ActiveStream.AGGREGATABLE_STREAM && aggregatableStream != null) {
                return true;
            } else if (activeStream == ActiveStream.CHAINED_AGGREGATOR_STREAM && aggregatableStream != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void setActiveStream(ActiveStream activeStream) {
        this.activeStream = activeStream;
        
        if (isActiveStream(ActiveStream.STREAM)) {
            groupedStream = null;
            aggregatableStream = null;
        } else if (isActiveStream(ActiveStream.GROUPED_STREAM)) {
            stream = null;
            aggregatableStream = null;
        } else if (isActiveStream(ActiveStream.AGGREGATABLE_STREAM)) {
            stream = null;
            groupedStream = null;
        } else if (isActiveStream(ActiveStream.CHAINED_AGGREGATOR_STREAM)) {
            stream = null;
            groupedStream = null;
        }
    }
    
    private List<String> parseFieldsList(String fieldsString) {
        List<String> fieldsList = new ArrayList<String>();
        if (fieldsString != null && StringUtils.isNotBlank(fieldsString)) {
            for (String field : fieldsString.split(",")) {
                fieldsList.add(StringUtils.trim(field));
            }
        }
        return fieldsList;
    }
    
    enum ActiveStream {
        NONE, 
        STREAM, 
        GROUPED_STREAM, 
        AGGREGATABLE_STREAM,
        CHAINED_AGGREGATOR_STREAM
    }
}
