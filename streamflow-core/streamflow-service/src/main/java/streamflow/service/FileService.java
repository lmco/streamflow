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
package streamflow.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.List;
import streamflow.datastore.core.FileContentDao;
import streamflow.datastore.core.FileInfoDao;
import streamflow.model.FileInfo;
import streamflow.model.FileContent;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;
import streamflow.service.util.IDUtils;

@Singleton
public class FileService {

    private final FileInfoDao fileInfoDao;

    private final FileContentDao fileContentDao;

    @Inject
    public FileService(FileInfoDao uploadDao, FileContentDao fileSystemDao) {
        this.fileInfoDao = uploadDao;
        this.fileContentDao = fileSystemDao;
    }

    public List<FileInfo> listFiles() {
        return fileInfoDao.findAll();
    }

    public FileInfo saveFile(FileInfo fileInfo) {
        return saveFile(fileInfo, null);
    }

    public FileInfo saveFile(FileInfo fileInfo, byte[] fileContent) {
        if (fileInfo == null) {
            throw new EntityInvalidException("The provided file info was null");
        }
        
        if (fileInfo.getId() == null) {
            fileInfo.setId(IDUtils.randomUUID());
        }
        fileInfo.setCreated(new Date());
        fileInfo.setModified(fileInfo.getCreated());

        fileInfo = fileInfoDao.save(fileInfo);

        if (fileContent != null) {
            updateFileContent(fileInfo.getId(), fileContent);
        }

        return fileInfo;
    }

    public FileInfo getFileInfo(String fileId) {
        FileInfo fileInfo = fileInfoDao.findById(fileId);
        if (fileInfo == null) {
            throw new EntityNotFoundException(
                    "File with the specified ID not found: ID = " + fileId);
        }

        return fileInfo;
    }

    public void deleteFile(String fileId) {
        FileInfo fileInfo = fileInfoDao.findById(fileId);
        if (fileInfo != null) {
            fileInfoDao.delete(fileInfo);
        }

        FileContent fileContent = fileContentDao.findById(fileId);
        if (fileContent != null) {
            fileContentDao.delete(fileContent);
        }
    }

    public void updateFile(String fileId, FileInfo fileInfo) {
        FileService.this.updateFile(fileId, fileInfo, null);
    }

    public void updateFile(String fileId, FileInfo fileInfo, byte[] fileContent) {
        FileInfo oldFileInfo = getFileInfo(fileId);
        if (fileInfo == null) {
            throw new EntityInvalidException("The provided file info was NULL");
        }

        fileInfo.setId(fileId);
        fileInfo.setCreated(oldFileInfo.getCreated());
        fileInfo.setModified(new Date());

        fileInfoDao.update(fileInfo);

        if (fileContent != null) {
            updateFileContent(fileId, fileContent);
        }
    }

    public byte[] getFileContent(String fileId) {
        FileContent fileContent = fileContentDao.findById(fileId);
        if (fileContent == null) {
            throw new ServiceException(
                    "Unable to load the file content on the server: ID = " + fileId);
        }

        return fileContent.getData();
    }

    public void updateFileContent(String fileId, byte[] data) {
        FileContent fileContent = fileContentDao.findById(fileId);

        if (fileContent == null) {
            fileContent = new FileContent();
            fileContent.setId(fileId);
            fileContent.setData(data);

            fileContentDao.save(fileContent);
        } else {
            fileContent.setData(data);

            fileContentDao.update(fileContent);
        }
    }
}
