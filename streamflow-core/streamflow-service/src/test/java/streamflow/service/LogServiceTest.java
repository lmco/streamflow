package streamflow.service;

import streamflow.model.config.StreamflowConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogServiceTest {
    
    private StreamflowConfig streamflowConfig;
    
    private LogService logService;
    
    @Before
    public void setUp() {
        logService = new LogService(streamflowConfig);
    }
    
    @Test
    public void getTopologyLogLocal() {
        
    }
}
