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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import streamflow.datastore.core.FileContentDao;
import streamflow.model.FileContent;
import streamflow.model.config.DatastoreConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MongoGridFsFileContentDao implements FileContentDao {

    public static Logger LOG = LoggerFactory.getLogger(MongoGridFsFileContentDao.class);

    private final GridFS gridFs;

    @Inject
    public MongoGridFsFileContentDao(Mongo mongo, DatastoreConfig datastoreConfig) {
        String dbName = (String) datastoreConfig.properties().get("dbName");
        if (dbName == null || dbName.isEmpty()) {
            dbName = "streamflow";
        }
        
        DB db = mongo.getDB(dbName);

        gridFs = new GridFS(db);
    }

    @Override
    public List<FileContent> findAll() {
        List<FileContent> files = new ArrayList<FileContent>();

        DBCursor cursor = gridFs.getFileList();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();

            FileContent fileContent = new FileContent();
            fileContent.setId((String) dbObject.get("_id"));
        }

        return files;
    }

    @Override
    public boolean exists(String id) {
        return gridFs.findOne(id) != null;
    }

    @Override
    public FileContent findById(String id) {
        GridFSDBFile fileEntry = gridFs.findOne(id);

        try {
            FileContent fileContent = new FileContent();
            fileContent.setId(id);
            fileContent.setData(IOUtils.toByteArray(fileEntry.getInputStream()));

            return fileContent;
        } catch (IOException ex) {
            LOG.error("Error while retrieving file: " + ex.getMessage());

            return null;
        }
    }

    @Override
    public FileContent save(FileContent entity) {
        GridFSInputFile inputFile = gridFs.createFile(entity.getData());
        inputFile.setId(entity.getId());
        inputFile.save();

        return entity;
    }

    @Override
    public void delete(FileContent entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(String id) {
        gridFs.remove(gridFs.findOne(id));
    }

    @Override
    public FileContent update(FileContent entity) {
        GridFSInputFile inputFile = gridFs.createFile(entity.getData());
        inputFile.setId(entity.getId());
        inputFile.save();

        return entity;
    }
}
