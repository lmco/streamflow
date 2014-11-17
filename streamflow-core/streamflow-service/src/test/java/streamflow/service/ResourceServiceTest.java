package streamflow.service;

import streamflow.service.ResourceService;
import streamflow.datastore.core.ResourceDao;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceTest {

    @Mock
    public ResourceDao resourceDao;
    
    private ResourceService resourceService;
    
    @Before
    public void setUp() {
        resourceService = new ResourceService(resourceDao);
    }
    
    @Test
    public void listResources() {
        
    }
}
