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
package streamflow.datastore.mongodb.impl;

import com.github.fakemongo.junit.FongoRule;
import java.util.Date;
import java.util.List;
import streamflow.model.FileInfo;
import streamflow.model.test.IntegrationTest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(IntegrationTest.class)
public class MongoFileInfoDaoTest {

    @Rule
    public FongoRule fongoRule = new FongoRule();
    
    private MongoFileInfoDao fileInfoDao;
    
    @Before
    public void setUp() {
        Datastore datastore = new Morphia().createDatastore(fongoRule.getMongo(), "streamflow");
        
        fileInfoDao = new MongoFileInfoDao(datastore);
        
        FileInfo file1 = new FileInfo();
        file1.setId("first-file");
        file1.setFileName("First File");
        file1.setContentHash("CONTENT_HASH_FILE_1");
        file1.setCreated(new Date(System.currentTimeMillis()));
        
        FileInfo file2 = new FileInfo();
        file2.setId("second-file");
        file2.setFileName("Second File");
        file2.setContentHash("CONTENT_HASH_FILE_2");
        file2.setCreated(new Date(System.currentTimeMillis() + 1000l));
        
        FileInfo file3 = new FileInfo();
        file3.setId("third-file");
        file3.setFileName("Third File");
        file3.setContentHash("CONTENT_HASH_FILE_1");
        file3.setCreated(new Date(System.currentTimeMillis() + 2000l));
        
        fileInfoDao.save(file3);
        fileInfoDao.save(file1);
        fileInfoDao.save(file2);
    }
    
    @Test
    public void findAllFiles() {
        List<FileInfo> files = fileInfoDao.findAll();
        
        assertEquals("There should be 3 files in the datastore", 3, files.size());
        
        // Check proper sorting of the elements by label
        assertEquals("The first item in the fileInfo list should have and id of \"first-file\"",
                "third-file", files.get(0).getId());
        assertEquals("The second item in the fileInfo list should have and id of \"second-file\"",
                "second-file", files.get(1).getId());
        assertEquals("The third item in the fileInfo list should have and id of \"third-file\"",
                "first-file", files.get(2).getId());
    }
    
    @Test
    public void findFileByContentHash() {
        List<FileInfo> validFile1 = fileInfoDao.findByContentHash("CONTENT_HASH_FILE_1");
        
        assertEquals("The returned file list should have two items", 2, validFile1.size());
        
        List<FileInfo> validFile2 = fileInfoDao.findByContentHash("CONTENT_HASH_FILE_2");
        
        assertEquals("The returned file list should have one item", 1, validFile2.size());
        
        List<FileInfo> invalidFile = fileInfoDao.findByContentHash("");
        
        assertEquals("The returned file list should have no items", 0, invalidFile.size());
    }
}