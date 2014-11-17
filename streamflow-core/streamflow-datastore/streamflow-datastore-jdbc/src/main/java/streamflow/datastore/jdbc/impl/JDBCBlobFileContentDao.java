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
import javax.persistence.EntityManager;
import streamflow.datastore.core.FileContentDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.FileContentEntity;
import streamflow.model.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCBlobFileContentDao extends JDBCDao<FileContent, String, FileContentEntity> 
        implements FileContentDao {

    public static Logger LOG = LoggerFactory.getLogger(JDBCBlobFileContentDao.class);

    @Inject
    public JDBCBlobFileContentDao(EntityManager entityManager) {
        super(entityManager, FileContent.class, FileContentEntity.class);
    }

    @Override
    protected FileContent toObject(FileContentEntity entity) {
        FileContent fileContent = new FileContent();
        fileContent.setId(entity.getId());
        fileContent.setData(entity.getEntity());
        return fileContent;
    }

    @Override
    protected FileContentEntity toEntity(FileContent fileContent) {
        FileContentEntity entity = new FileContentEntity();
        entity.setId(fileContent.getId());
        entity.setEntity(fileContent.getData());
        return entity;
    }
}
