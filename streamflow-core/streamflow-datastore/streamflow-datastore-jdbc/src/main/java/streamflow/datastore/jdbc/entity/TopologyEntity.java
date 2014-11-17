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
    @NamedQuery(name = TopologyEntity.FIND_ALL,
            query = "SELECT t FROM TopologyEntity t ORDER BY t.name ASC"),
    @NamedQuery(name = TopologyEntity.FIND_ALL_WITH_USER,
            query = "SELECT t FROM TopologyEntity t WHERE t.userId = :userId ORDER BY t.name ASC"),
    @NamedQuery(name = TopologyEntity.FIND_ALL_WITH_ANON,
            query = "SELECT t FROM TopologyEntity t WHERE t.userId IS NULL ORDER BY t.name ASC"),
    @NamedQuery(name = TopologyEntity.FIND_BY_ID,
            query = "SELECT t FROM TopologyEntity t WHERE t.id = :id"),
    @NamedQuery(name = TopologyEntity.FIND_BY_ID_WITH_USER,
            query = "SELECT t FROM TopologyEntity t WHERE t.id = :id AND t.userId = :userId"),
    @NamedQuery(name = TopologyEntity.FIND_BY_ID_WITH_ANON,
            query = "SELECT t FROM TopologyEntity t WHERE t.id = :id AND t.userId IS NULL"),
    @NamedQuery(name = TopologyEntity.FIND_BY_NAME,
            query = "SELECT t FROM TopologyEntity t WHERE t.name = :name"),
    @NamedQuery(name = TopologyEntity.FIND_BY_NAME_WITH_USER,
            query = "SELECT t FROM TopologyEntity t WHERE t.name = :name AND t.userId = :userId"),
    @NamedQuery(name = TopologyEntity.FIND_BY_NAME_WITH_ANON,
            query = "SELECT t FROM TopologyEntity t WHERE t.name = :name AND t.userId IS NULL")
})
public class TopologyEntity implements Serializable {

    public static final String FIND_ALL = "TopologyEntity.findAll";
    public static final String FIND_ALL_WITH_USER = "TopologyEntity.findAllWithUser";
    public static final String FIND_ALL_WITH_ANON = "TopologyEntity.findAllWithAnon";
    public static final String FIND_BY_ID = "TopologyEntity.findById";
    public static final String FIND_BY_ID_WITH_USER = "TopologyEntity.findByIdWithUser";
    public static final String FIND_BY_ID_WITH_ANON = "TopologyEntity.findByIdWithAnon";
    public static final String FIND_BY_NAME = "TopologyEntity.findByName";
    public static final String FIND_BY_NAME_WITH_USER = "TopologyEntity.findByNameWithUser";
    public static final String FIND_BY_NAME_WITH_ANON = "TopologyEntity.findByNameWithAnon";

    @Id
    private String id;

    private String userId;
    
    private String name;

    @Lob
    private String entity;
    
    public TopologyEntity() {
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}