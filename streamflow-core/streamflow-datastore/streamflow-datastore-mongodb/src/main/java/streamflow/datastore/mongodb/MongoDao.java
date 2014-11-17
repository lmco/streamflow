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
package streamflow.datastore.mongodb;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import com.google.inject.Inject;
import java.io.Serializable;
import java.util.List;
import streamflow.datastore.core.GenericDao;
import streamflow.model.util.Entity;

public class MongoDao<T extends Entity<ID>, ID extends Serializable>
        implements GenericDao<T, ID> {

    protected Class<T> persistentClass;

    protected Datastore datastore;

    @Inject
    public MongoDao(Datastore datastore, Class<T> persistentClass) {
        this.datastore = datastore;
        this.persistentClass = persistentClass;
    }

    @Override
    public boolean exists(ID id) {
        return datastore.get(persistentClass, id) != null;
    }

    @Override
    public List<T> findAll() {
        Query<T> q = datastore.createQuery(persistentClass);
        return q.asList();
    }

    @Override
    public T findById(ID id) {
        return datastore.get(persistentClass, id);
    }

    @Override
    public T save(T entity) {
        Key<T> key = datastore.save(entity);
        return datastore.getByKey(persistentClass, key);
    }

    @Override
    public T update(T entity) {
        Key<T> key = datastore.merge(entity);
        return datastore.getByKey(persistentClass, key);
    }

    @Override
    public void delete(T entity) {
        if (entity != null) {
            datastore.delete(entity);
        }
    }

    @Override
    public void deleteById(ID id) {
        datastore.delete(persistentClass, id);
    }

    public Query<T> query() {
        return datastore.createQuery(persistentClass);
    }
}
