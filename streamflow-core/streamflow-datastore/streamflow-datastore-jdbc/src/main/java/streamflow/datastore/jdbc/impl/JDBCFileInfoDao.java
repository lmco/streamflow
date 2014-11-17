/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package streamflow.datastore.jdbc.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import streamflow.datastore.core.FileInfoDao;
import streamflow.datastore.jdbc.JDBCDao;
import streamflow.datastore.jdbc.entity.FileInfoEntity;
import streamflow.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class JDBCFileInfoDao extends JDBCDao<FileInfo, String, FileInfoEntity> 
        implements FileInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCFileInfoDao.class);

    @Inject
    public JDBCFileInfoDao(EntityManager entityManager) {
        super(entityManager, FileInfo.class, FileInfoEntity.class);
    }

    @Override
    public List<FileInfo> findByContentHash(String contentHash) {
        List<FileInfo> files = new ArrayList<FileInfo>();
        
        try {
            TypedQuery<FileInfoEntity> query = entityManager.createNamedQuery(
                    FileInfoEntity.FIND_BY_CONTENT_HASH, FileInfoEntity.class);
            query.setParameter("contentHash", contentHash);
            
            for (FileInfoEntity entity : query.getResultList()) {
                files.add(toObject(entity));
            }
        } catch (Exception ex) {
            LOG.error("Exception occurred while retrieving the entity: ", ex);
        }
        
        return files;
    }

    @Override
    protected FileInfo toObject(FileInfoEntity entity) {
        FileInfo fileInfo = null;
        try {
            if (entity != null) {
                fileInfo = mapper.readValue(entity.getEntity(), FileInfo.class);
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the file info entity to an object", ex);
        }
        return fileInfo;
    }

    @Override
    protected FileInfoEntity toEntity(FileInfo fileInfo) {
        FileInfoEntity entity = null;
        try {
            if (fileInfo != null) {
                entity = new FileInfoEntity();
                entity.setId(fileInfo.getId());
                entity.setFileType(fileInfo.getFileType());
                entity.setContentHash(fileInfo.getContentHash());
                entity.setEntity(mapper.writeValueAsString(fileInfo));
            }
        } catch (Exception ex) {
            LOG.error("An exception occurred converting the file info entity to an object", ex);
        }
        return entity;
    }
}
