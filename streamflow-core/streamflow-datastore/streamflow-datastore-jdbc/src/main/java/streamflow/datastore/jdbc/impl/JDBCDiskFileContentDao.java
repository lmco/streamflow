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
package streamflow.datastore.jdbc.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import streamflow.datastore.core.FileContentDao;
import streamflow.model.FileContent;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCDiskFileContentDao implements FileContentDao {

    public static Logger LOG = LoggerFactory.getLogger(JDBCDiskFileContentDao.class);

    @Inject
    public JDBCDiskFileContentDao() {
    }

    @Override
    public List<FileContent> findAll() {
        List<FileContent> files = new ArrayList<FileContent>();

        File filesDirectory = new File(StreamflowEnvironment.getFilesDir());
        for (File file : filesDirectory.listFiles()) {
            FileContent entry = new FileContent();
            entry.setId(file.getName());
        }

        return files;
    }

    @Override
    public boolean exists(String id) {
        return findById(id) != null;
    }

    @Override
    public FileContent findById(String id) {
        FileContent result = null;
        
        File targetFile = new File(StreamflowEnvironment.getFilesDir(), id);

        if (targetFile.exists() && targetFile.canRead()) {
            try {
                // Retrieve the file contents from the frameworks 
                byte[] fileData = FileUtils.readFileToByteArray(targetFile);

                result = new FileContent();
                result.setId(id);
                result.setData(fileData);
            } catch (Exception ex) {
                //LOG.error("Error while retrieving file system entry: ID = " + id, ex);
            }
        }

        return result;
    }

    @Override
    public FileContent save(FileContent entity) {
        try {
            FileUtils.writeByteArrayToFile(new File(StreamflowEnvironment.getFilesDir(),
                    entity.getId()), entity.getData());
        } catch (Exception ex) {
            LOG.error("Error while saving the file system entry: ", ex);
        }

        return entity;
    }

    @Override
    public void delete(FileContent entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(String id) {
        FileUtils.deleteQuietly(new File(StreamflowEnvironment.getFilesDir(), id));
    }

    @Override
    public FileContent update(FileContent entity) {
        try {
            FileUtils.writeByteArrayToFile(new File(StreamflowEnvironment.getFilesDir(),
                    entity.getId()), entity.getData());
        } catch (Exception ex) {
            LOG.error("Error while updating the file system entry:", ex);
        }

        return entity;
    }
}
