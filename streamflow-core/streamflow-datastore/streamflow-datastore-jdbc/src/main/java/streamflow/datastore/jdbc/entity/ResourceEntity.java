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
    @NamedQuery(name = ResourceEntity.FIND_ALL,
            query = "SELECT r FROM ResourceEntity r ORDER BY r.label ASC"),
    @NamedQuery(name = ResourceEntity.FIND_ALL_WITH_FRAMEWORK,
            query = "SELECT r FROM ResourceEntity r WHERE r.framework = :framework ORDER BY r.label ASC"),
    @NamedQuery(name = ResourceEntity.FIND_BY_FRAMEWORK_AND_NAME,
            query = "SELECT r FROM ResourceEntity r WHERE r.framework = :framework AND r.name = :name")
})
public class ResourceEntity implements Serializable {

    public static final String FIND_ALL = "ResourceEntity.findAll";
    public static final String FIND_ALL_WITH_FRAMEWORK = "ResourceEntity.findAllWithFramework";
    public static final String FIND_BY_FRAMEWORK_AND_NAME = "ResourceEntity.findByFrameworkAndName";

    @Id
    private String id;

    private String name;

    private String label;
    
    private String framework;

    @Lob
    private String entity;
    
    public ResourceEntity() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
