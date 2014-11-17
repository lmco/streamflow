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
package streamflow.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;

@Entity("fileInfo")
public class FileInfo implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;

    private String fileName;

    private String fileType;

    private long fileSize;

    private String contentHash;

    private Date created;

    private Date modified;

    
    public FileInfo() {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 71 * hash + (this.fileType != null ? this.fileType.hashCode() : 0);
        hash = 71 * hash + (int) (this.fileSize ^ (this.fileSize >>> 32));
        hash = 71 * hash + (this.contentHash != null ? this.contentHash.hashCode() : 0);
        hash = 71 * hash + (this.created != null ? this.created.hashCode() : 0);
        hash = 71 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileInfo other = (FileInfo) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.fileName == null) ? (other.fileName != null) 
                : !this.fileName.equals(other.fileName)) {
            return false;
        }
        if ((this.fileType == null) ? (other.fileType != null) 
                : !this.fileType.equals(other.fileType)) {
            return false;
        }
        if (this.fileSize != other.fileSize) {
            return false;
        }
        if ((this.contentHash == null) ? (other.contentHash != null) 
                : !this.contentHash.equals(other.contentHash)) {
            return false;
        }
        if (this.created != other.created && 
                (this.created == null || !this.created.equals(other.created))) {
            return false;
        }
        if (this.modified != other.modified && 
                (this.modified == null || !this.modified.equals(other.modified))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FileInfo{" + "id=" + id + ", fileName=" + fileName + ", fileType=" + fileType 
                + ", fileSize=" + fileSize + ", contentHash=" + contentHash 
                + ", created=" + created + ", modified=" + modified + '}';
    }
}
