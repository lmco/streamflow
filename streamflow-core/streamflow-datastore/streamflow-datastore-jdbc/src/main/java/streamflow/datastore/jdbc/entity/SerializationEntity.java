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
    @NamedQuery(name = SerializationEntity.FIND_ALL,
            query = "SELECT s FROM SerializationEntity s ORDER BY s.typeClass ASC"),
    @NamedQuery(name = SerializationEntity.FIND_ALL_WITH_FRAMEWORK,
            query = "SELECT s FROM SerializationEntity s WHERE s.framework = :framework ORDER BY s.typeClass ASC"),
    @NamedQuery(name = SerializationEntity.FIND_BY_TYPE_CLASS,
            query = "SELECT s FROM SerializationEntity s WHERE s.typeClass = :typeClass")
})
public class SerializationEntity implements Serializable {

    public static final String FIND_ALL = "SerializationEntity.findAll";
    public static final String FIND_ALL_WITH_FRAMEWORK = "SerializationEntity.findAllWithFramework";
    public static final String FIND_BY_TYPE_CLASS = "SerializationEntity.findByTypeClass";

    @Id
    private String id;

    private String framework;

    private String typeClass;

    @Lob
    private String entity;

    public SerializationEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
