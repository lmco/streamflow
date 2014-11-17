package streamflow.service;

import streamflow.service.ResourceEntryService;
import streamflow.datastore.core.ResourceEntryDao;

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
public class ResourceEntryServiceTest {

    @Mock
    public ResourceEntryDao resourceEntryDao;
    
    private ResourceEntryService resourceEntryService;
    
    @Before
    public void setUp() {
        resourceEntryService = new ResourceEntryService(resourceEntryDao);
    }
    
    @Test
    public void listResourceEntries() {
        
    }
}
