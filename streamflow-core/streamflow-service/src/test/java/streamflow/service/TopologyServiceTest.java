package streamflow.service;

import streamflow.datastore.core.TopologyDao;
import streamflow.engine.StormEngine;
import streamflow.model.config.StreamflowConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TopologyServiceTest {

    @Mock
    public TopologyDao topologyDao;
    
    @Mock
    public ComponentService componentService;
    
    @Mock
    public ResourceService resourceService;
    
    @Mock
    public ResourceEntryService resourceEntryService;
    
    @Mock
    public SerializationService serializationService;
    
    @Mock
    public FileService uploadService;
    
    @Mock
    public FrameworkService frameworkService;
    
    @Mock
    public ClusterService clusterService;
    
    @Mock
    public LogService logService;
    
    @Mock
    public StormEngine stormEngine;
    
    private StreamflowConfig streamflowConfig;
    
    private TopologyService topologyService;
    
    @Before
    public void setUp() {
        streamflowConfig = new StreamflowConfig();
        
        topologyService = new TopologyService(topologyDao, componentService, resourceService, 
                resourceEntryService, serializationService, uploadService, frameworkService,
                clusterService, logService, stormEngine, streamflowConfig);
    }
    
    @Test
    public void listTopologies() {
        
    }
}
