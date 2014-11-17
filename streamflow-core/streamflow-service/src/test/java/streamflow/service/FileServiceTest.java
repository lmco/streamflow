package streamflow.service;

import streamflow.service.FileService;
import streamflow.datastore.core.FileContentDao;
import streamflow.datastore.core.FileInfoDao;
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
public class FileServiceTest {

    @Mock
    public FileInfoDao fileInfoDao;
    
    @Mock
    public FileContentDao fileContentDao;
    
    private FileService fileService;
    
    @Before
    public void setUp() {
        fileService = new FileService(fileInfoDao, fileContentDao);
    }
    
    @Test
    public void listUploads() {
        
    }
}
