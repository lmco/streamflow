package streamflow.service;

import streamflow.service.FileService;
import streamflow.service.ComponentService;
import streamflow.datastore.core.ComponentDao;
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
public class ComponentServiceTest {

    @Mock
    public ComponentDao componentDao;
    
    @Mock
    public FileService fileService;
    
    private ComponentService componentService;
    
    @Before
    public void setUp() {
        componentService = new ComponentService(componentDao, fileService);
    }
    
    @Test
    public void listComponentsWithVisibility() {
        
    }
    
    @Test
    public void listComponentsWithFramework() {
        
    }
}
