package streamflow.service;

import streamflow.service.FileService;
import streamflow.service.SerializationService;
import streamflow.service.FrameworkService;
import streamflow.service.ComponentService;
import streamflow.service.ResourceService;
import streamflow.datastore.core.FileContentDao;
import streamflow.datastore.core.FrameworkDao;

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
public class FrameworkServiceTest {

    @Mock
    public FrameworkDao frameworkDao;
    
    @Mock
    public FileService fileService;
    
    @Mock
    public ComponentService componentService;
    
    @Mock
    public ResourceService resourceService;
    
    @Mock
    public SerializationService serializationService;
    
    private FrameworkService frameworkService;
    
    @Before
    public void setUp() {
        frameworkService = new FrameworkService(
            frameworkDao, fileService, componentService, resourceService, serializationService);
    }
    
    @Test
    public void listFrameworks() {
        
    }
}
