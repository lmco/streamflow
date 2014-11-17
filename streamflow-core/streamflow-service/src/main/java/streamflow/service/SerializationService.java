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
import streamflow.datastore.core.SerializationDao;
import streamflow.model.Serialization;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;

@Singleton
public class SerializationService {

    private final SerializationDao serializationDao;

    @Inject
    public SerializationService(SerializationDao serializationDao) {
        this.serializationDao = serializationDao;
    }

    public List<Serialization> listSerializations() {
        return serializationDao.findAll();
    }

    public List<Serialization> listSerializationsWithFramework(String framework) {
        if (framework == null) {
            throw new ServiceException("The provided framework was null");
        }
        return serializationDao.findAllWithFramework(framework);
    }

    public Serialization addSerialization(Serialization serialization) {
        if (serialization == null) {
            throw new EntityInvalidException("The provided serialization was NULL");
        }
        if (serialization.getTypeClass() == null || serialization.getFramework() == null) {
            throw new EntityInvalidException("The serialization was missing required fields");
        }
        if (hasSerialization(serialization.getTypeClass())) {
            throw new EntityConflictException(
                    "Serialization with the specified type class already exists: Type Class = " 
                            + serialization.getTypeClass());
        }

        serialization.setId(serialization.getFramework() + "_" + serialization.getTypeClass());
        serialization.setCreated(new Date());
        serialization.setModified(serialization.getCreated());

        return serializationDao.save(serialization);
    }

    public Serialization getSerialization(String serializationId) {
        Serialization serialization = serializationDao.findById(serializationId);
        if (serialization == null) {
            throw new EntityNotFoundException(
                    "Serialization with the ID could not be found: ID = " + serializationId);
        }
        return serialization;
    }

    public Serialization getSerializationWithTypeClass(String typeClass) {
        Serialization serialization = serializationDao.findByTypeClass(typeClass);
        if (serialization == null) {
            throw new EntityNotFoundException(
                    "Serialization with the type class could not be found: Type Class = " + typeClass);
        }
        return serialization;
    }

    public boolean hasSerialization(String typeClass) {
        return serializationDao.findByTypeClass(typeClass) != null;
    }

    public void deleteSerialization(String serializationId) {
        serializationDao.delete(getSerialization(serializationId));
    }

    public void updateSerialization(String serializationId, Serialization serialization) {
        Serialization oldSerialization = getSerialization(serializationId);

        if (serialization == null) {
            throw new EntityInvalidException("The provided serialization was NULL");
        }
        if (serialization.getTypeClass() == null || serialization.getFramework() == null) {
            throw new EntityInvalidException("The serialization was missing required fields");
        }
        if (!oldSerialization.getTypeClass().equals(serialization.getTypeClass())) {
            if (hasSerialization(serialization.getTypeClass())) {
                throw new EntityConflictException(
                        "Serialization with the type class already exists: Type Class = " 
                                + serialization.getTypeClass());
            }
        }

        serialization.setId(serializationId);
        serialization.setCreated(oldSerialization.getCreated());
        serialization.setModified(new Date());

        serializationDao.update(serialization);
    }
}
