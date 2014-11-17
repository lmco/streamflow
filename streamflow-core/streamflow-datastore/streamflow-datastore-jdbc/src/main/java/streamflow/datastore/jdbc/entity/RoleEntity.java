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
    @NamedQuery(name = RoleEntity.FIND_ALL,
            query = "SELECT r FROM RoleEntity r ORDER BY r.name ASC"),
    @NamedQuery(name = RoleEntity.FIND_BY_NAME,
            query = "SELECT r FROM RoleEntity r WHERE r.name = :name")
})
public class RoleEntity implements Serializable {

    public static final String FIND_ALL = "RoleEntity.findAll";
    public static final String FIND_BY_NAME = "RoleEntity.findByName";

    @Id
    private String id;

    private String name;

    @Lob
    private String entity;
    
    public RoleEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
