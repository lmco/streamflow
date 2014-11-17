package streamflow.service;

import streamflow.service.KafkaService;
import streamflow.datastore.core.KafkaDao;

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
public class KafkaServiceTest {

    @Mock
    public KafkaDao kafkaDao;
    
    private KafkaService kafkaService;
    
    @Before
    public void setUp() {
        kafkaService = new KafkaService(kafkaDao);
    }
    
    @Test
    public void listKafkaClusters() {
        
    }
}
