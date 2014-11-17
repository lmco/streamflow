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
package streamflow.datastore.core;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, ID extends Serializable> {

    boolean exists(ID id);

    List<T> findAll();

    T findById(ID id);

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
