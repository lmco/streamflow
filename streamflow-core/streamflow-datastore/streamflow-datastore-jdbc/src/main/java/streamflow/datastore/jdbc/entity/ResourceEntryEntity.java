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
package streamflow.datastore.jdbc.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL,
            query = "SELECT r FROM ResourceEntryEntity r ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL_WITH_USER,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.userId = :userId ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL_WITH_ANON,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.userId IS NULL ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL_BY_RESOURCE,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL_BY_RESOURCE_WITH_USER,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource AND r.userId = :userId ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_ALL_BY_RESOURCE_WITH_ANON,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource AND r.userId IS NULL ORDER BY r.name ASC"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_ID,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.id = :id"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_ID_WITH_USER,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.id = :id AND r.userId = :userId"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_ID_WITH_ANON,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.id = :id AND r.userId IS NULL"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_RESOURCE_AND_NAME,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource AND r.name = :name"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_RESOURCE_AND_NAME_WITH_USER,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource AND r.name = :name AND r.userId = :userId"),
    @NamedQuery(name = ResourceEntryEntity.FIND_BY_RESOURCE_AND_NAME_WITH_ANON,
            query = "SELECT r FROM ResourceEntryEntity r WHERE r.resource = :resource AND r.name = :name AND r.userId IS NULL")
})
public class ResourceEntryEntity implements Serializable {

    public static final String FIND_ALL = "ResourceEntryEntity.findAll";
    public static final String FIND_ALL_WITH_USER = "ResourceEntryEntity.findAllWithUser";
    public static final String FIND_ALL_WITH_ANON = "ResourceEntryEntity.findAllWithAnon";
    public static final String FIND_ALL_BY_RESOURCE = "ResourceEntryEntity.findAllWithResource";
    public static final String FIND_ALL_BY_RESOURCE_WITH_USER = "ResourceEntryEntity.findAllWithResourceWithUser";
    public static final String FIND_ALL_BY_RESOURCE_WITH_ANON = "ResourceEntryEntity.findAllWithResourceWithAnon";
    public static final String FIND_BY_ID = "ResourceEntryEntity.findById";
    public static final String FIND_BY_ID_WITH_USER = "ResourceEntryEntity.findByIdWithUser";
    public static final String FIND_BY_ID_WITH_ANON = "ResourceEntryEntity.findByIdWithAnon";
    public static final String FIND_BY_RESOURCE_AND_NAME = "ResourceEntryEntity.findByResourceAndName";
    public static final String FIND_BY_RESOURCE_AND_NAME_WITH_USER = "ResourceEntryEntity.findByResourceAndNameWithUser";
    public static final String FIND_BY_RESOURCE_AND_NAME_WITH_ANON = "ResourceEntryEntity.findByResourceAndNameWithAnon";

    @Id
    private String id;

    private String userId;
    
    private String name;
    
    private String resource;

    @Lob
    private String entity;
    
    public ResourceEntryEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
