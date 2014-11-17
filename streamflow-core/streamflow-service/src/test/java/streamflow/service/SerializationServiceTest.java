package streamflow.service;

import streamflow.service.SerializationService;
import streamflow.datastore.core.SerializationDao;

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
public class SerializationServiceTest {

    @Mock
    public SerializationDao serializationDao;
    
    private SerializationService serializationService;
    
    @Before
    public void setUp() {
        serializationService = new SerializationService(serializationDao);
    }
    
    @Test
    public void listSerializations() {
        
    }
}
