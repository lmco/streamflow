/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.server.resource;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;
import streamflow.model.FileInfo;
import streamflow.model.test.IntegrationTest;
import streamflow.service.FileService;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
@Category(IntegrationTest.class)
public class FileResourceTest extends JerseyTest {

    @Mock
    public static FileService fileServiceMock;

    public FileResourceTest() {
        super(new WebAppDescriptor.Builder()
                .contextListenerClass(FileInfoWebConfig.class)
                .filterClass(GuiceFilter.class)
                .build());
    }
    
    @Test
    public void createFileInfo() {
        final byte[] mockedFileInfoData = "UPLOAD FILE CONTENT!".getBytes();
        
        when(fileServiceMock.saveFile(any(FileInfo.class), any(byte[].class))).then(new Answer<FileInfo>() {
            @Override
            public FileInfo answer(InvocationOnMock invocation) {
                FileInfo argFileInfo = (FileInfo) invocation.getArguments()[0];
                byte[] argFileInfoData = (byte[]) invocation.getArguments()[1];
                
                assertEquals("FileInfo file name should be set on the service upload object",
                        "test-file.txt", argFileInfo.getFileName());
                assertEquals("FileInfo file size should be set on the service upload object",
                        20, (long) argFileInfo.getFileSize()); 
                assertEquals("FileInfo file type should be set on the service upload object",
                        "text/plain", argFileInfo.getFileType());
                assertEquals("FileInfo content hash should be set on the service upload object",
                        "4b8c28bc84aa524ad5d2ff08f2bb7e84", argFileInfo.getContentHash());
                
                assertTrue("Service upload data should equal the requested upload data",
                        Arrays.equals(mockedFileInfoData, argFileInfoData));
                
                // Return the same cluster object as provided as an argument (same as real behavior)
                return argFileInfo;
            }
        });
        
        FormDataMultiPart formData = new FormDataMultiPart();
        FormDataBodyPart bodyPart = new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName("test-file.txt").build(), 
                mockedFileInfoData, MediaType.TEXT_PLAIN_TYPE);
        formData.bodyPart(bodyPart);

        FileInfo responseFileInfo = resource().path("/api/files")
                .accept(MediaType.APPLICATION_JSON).type(MediaType.MULTIPART_FORM_DATA)
                .post(FileInfo.class, formData);

        assertNotNull("Response file should not be null after valid file upload", responseFileInfo);
        
        verify(fileServiceMock).saveFile(any(FileInfo.class), any(byte[].class));
    }
    
    @Test
    public void updateFileInfo() {
        final FileInfo requestFileInfo = new FileInfo();
        requestFileInfo.setId("upload-1");
        requestFileInfo.setFileName("New File.txt");
        requestFileInfo.setFileType("text/html");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argFileInfoId = (String) invocation.getArguments()[0];
                FileInfo argFileInfo = (FileInfo) invocation.getArguments()[1];
                
                assertEquals("Service upload ID should match the requested upload ID",
                        argFileInfoId, requestFileInfo.getId());
                assertEquals("Service upload should match the requested upload",
                        argFileInfo, requestFileInfo);
                
                return null;
            }
        }).when(fileServiceMock).updateFile(anyString(), any(FileInfo.class));
              
        ClientResponse clientResponse = resource().path("/api/files/" + requestFileInfo.getId())
            .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            .put(ClientResponse.class, requestFileInfo);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(fileServiceMock).updateFile(anyString(), any(FileInfo.class));
    }
    
    @Test
    public void listFileInfos() {
        FileInfo file1 = new FileInfo();
        
        FileInfo file2 = new FileInfo();
        
        List<FileInfo> mockedFileInfos = new ArrayList<FileInfo>();
        mockedFileInfos.add(file1);
        mockedFileInfos.add(file2);
        
        when(fileServiceMock.listFiles()).thenReturn(mockedFileInfos);
              
        List<FileInfo> responseFileInfos = resource().path("/api/files")
            .accept(MediaType.APPLICATION_JSON).get(new GenericType<List<FileInfo>>(){});
        
        assertEquals("Response uploads should be equal to the mocked uploads",
                mockedFileInfos, responseFileInfos);
        
        verify(fileServiceMock).listFiles();
    }
    
    @Test
    public void getFileInfo() {
        final FileInfo mockedFileInfo = new FileInfo();
        mockedFileInfo.setId("upload-1");
        
        when(fileServiceMock.getFileInfo(anyString())).then(new Answer<FileInfo>() {
            @Override
            public FileInfo answer(InvocationOnMock invocation) throws Throwable {
                String argFileInfoId = (String) invocation.getArguments()[0];
                
                assertEquals("Service upload ID should match the requested upload ID", 
                        argFileInfoId, mockedFileInfo.getId());
                
                return mockedFileInfo;
            }
        });
              
        FileInfo responseFileInfo = resource().path("/api/files/" + mockedFileInfo.getId())
            .accept(MediaType.APPLICATION_JSON).get(FileInfo.class);
        
        assertEquals("Response upload should match the mocked upload", mockedFileInfo, responseFileInfo);
        
        verify(fileServiceMock).getFileInfo(anyString());
    }
    
    @Test
    public void deleteFileInfo() {
        final String requestFileInfoId = "FILE_TO_DELETE";
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argFileInfoId = (String) invocation.getArguments()[0];
                
                assertEquals("Service upload ID should match the requested upload ID", 
                        argFileInfoId, requestFileInfoId);
                
                return null;
            }
        }).when(fileServiceMock).deleteFile(anyString());
        
        ClientResponse clientResponse = resource().path("/api/files/" + requestFileInfoId)
            .delete(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        verify(fileServiceMock).deleteFile(anyString()); 
   }
    
    @Test
    public void getFileInfoContent() {
        final byte[] mockedFileInfoContent = "UPLOADED_CONTENT_HERE!".getBytes();
        final FileInfo mockedFileInfo = new FileInfo();
        mockedFileInfo.setFileName("test-file.txt");
        mockedFileInfo.setFileSize((long)mockedFileInfoContent.length);
        
        when(fileServiceMock.getFileInfo(anyString())).thenReturn(mockedFileInfo);
        when(fileServiceMock.getFileContent(anyString())).thenReturn(mockedFileInfoContent);
              
        ClientResponse clientResponse = resource().path("/api/files/" 
                + mockedFileInfo.getId() + "/content").get(ClientResponse.class);
        
        assertEquals("Response HTTP status code should be 200 (OK)", clientResponse.getStatus(), 200);
        
        assertTrue("Reponse upload data should match mocked upload data",
                Arrays.equals(mockedFileInfoContent, clientResponse.getEntity(byte[].class)));
        
        verify(fileServiceMock).getFileInfo(anyString());
        verify(fileServiceMock).getFileContent(anyString());
    }
    
    @Test
    public void updateFileInfoContent() {
        final FileInfo mockedFileInfo = new FileInfo();
        mockedFileInfo.setId("test-upload");
        
        final byte[] mockedFileInfoData = "UPDATED UPLOAD FILE CONTENT!".getBytes();
        
        when(fileServiceMock.getFileInfo(anyString())).thenReturn(mockedFileInfo); 
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String argFileInfoId = (String) invocation.getArguments()[0];
                FileInfo argFileInfo = (FileInfo) invocation.getArguments()[1];
                byte[] argFileInfoData = (byte[]) invocation.getArguments()[2];
                
                assertEquals("Service upload ID should match the requested upload ID",
                        argFileInfoId, mockedFileInfo.getId());
                
                assertEquals("FileInfo file name should be set on the service upload object",
                        "updated-file.txt", argFileInfo.getFileName());
                assertEquals("FileInfo file size should be set on the service upload object",
                        28, (long) argFileInfo.getFileSize()); 
                assertEquals("FileInfo file type should be set on the service upload object",
                        "text/plain", argFileInfo.getFileType());
                assertEquals("FileInfo content hash should be set on the service upload object",
                        "de1e1f8c088c7024368d2af801f51c67", argFileInfo.getContentHash());
                
                assertTrue("Service upload data should match the requested upload data",
                        Arrays.equals(mockedFileInfoData, argFileInfoData));
                
                return null;
            }
        }).when(fileServiceMock).updateFile(anyString(), any(FileInfo.class), any(byte[].class));
        
        FormDataMultiPart formData = new FormDataMultiPart();
        FormDataBodyPart bodyPart = new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName("updated-file.txt").build(), 
                mockedFileInfoData, MediaType.TEXT_PLAIN_TYPE);
        formData.bodyPart(bodyPart);

        FileInfo responseFileInfo = resource().path("/api/files/" + mockedFileInfo.getId() + "/content")
                .accept(MediaType.APPLICATION_JSON).type(MediaType.MULTIPART_FORM_DATA)
                .post(FileInfo.class, formData);

        assertNotNull("Response upload should not be null after valid file upload", responseFileInfo);
        
        verify(fileServiceMock).getFileInfo(anyString());
        verify(fileServiceMock).updateFile(anyString(), any(FileInfo.class), any(byte[].class));
    }
    
    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }
    
    public static class FileInfoWebConfig extends GuiceServletContextListener {
        @Override
        protected Injector getInjector() {
            return Guice.createInjector(new JerseyServletModule() {
                
                @Override
                protected void configureServlets() {
                    bind(FileService.class).toInstance(fileServiceMock);
                    bind(FileResource.class);

                    serve("/api/*").with(GuiceContainer.class);
                    
                    // hook Jackson into Jersey as the POJO <-> JSON mapper
                    bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
                }
            });
        }
    }
}
