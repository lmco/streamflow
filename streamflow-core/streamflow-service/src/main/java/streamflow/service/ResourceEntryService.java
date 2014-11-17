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
import streamflow.datastore.core.ResourceEntryDao;
import streamflow.model.ResourceEntry;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.util.IDUtils;

@Singleton
public class ResourceEntryService {

    private final ResourceEntryDao resourceEntryDao;

    @Inject
    public ResourceEntryService(ResourceEntryDao resourceEntryDao) {
        this.resourceEntryDao = resourceEntryDao;
    }

    public List<ResourceEntry> listResourceEntries(String userId) {
        return resourceEntryDao.findAll();
    }

    public List<ResourceEntry> listResourceEntriesForResource(String resource, String userId) {
        return resourceEntryDao.findAllWithResource(resource, userId);
    }

    public ResourceEntry addResourceEntry(ResourceEntry resourceEntry, String userId) {
        if (resourceEntry == null) {
            throw new EntityInvalidException("The provided resource entry was NULL");
        }
        if (resourceEntry.getName() == null || resourceEntry.getResource() == null) {
            throw new EntityInvalidException("The resource entry was missing required fields");
        }
        if (hasResourceEntry(resourceEntry.getResource(), resourceEntry.getName(), userId)) {
            throw new EntityConflictException(
                    "Resource Entry with the resource and name already exists: Resource = " 
                            + resourceEntry.getResource() + ", Name = " + resourceEntry.getName());
        }

        if (resourceEntry.getId() == null) {
            resourceEntry.setId(IDUtils.formatId(resourceEntry.getName()));
        }
        resourceEntry.setCreated(new Date());
        resourceEntry.setModified(resourceEntry.getCreated());

        return resourceEntryDao.save(resourceEntry, userId);
    }

    public boolean hasResourceEntry(String resourceName, String resourceEntryName, String userId) {
        return resourceEntryDao.findByResourceAndName(resourceName, resourceEntryName, userId) != null;
    }

    public ResourceEntry getResourceEntry(String resourceEntryId) {
        ResourceEntry resourceEntry = resourceEntryDao.findById(resourceEntryId);
        if (resourceEntry == null) {
            throw new EntityNotFoundException(
                    "Resource entry with the specified ID not found: ID = " + resourceEntryId);
        }

        return resourceEntry;
    }

    public ResourceEntry getResourceEntry(String resourceEntryId, String userId) {
        ResourceEntry resourceEntry = resourceEntryDao.findById(resourceEntryId, userId);
        if (resourceEntry == null) {
            throw new EntityNotFoundException(
                    "Resource entry with the specified ID not found: ID = " + resourceEntryId);
        }

        return resourceEntry;
    }

    public void deleteResourceEntry(String resourceEntryId, String userId) {
        resourceEntryDao.delete(getResourceEntry(resourceEntryId, userId));
    }

    public void updateResourceEntry(String resourceEntryId, ResourceEntry resourceEntry, String userId) {
        ResourceEntry oldResourceEntry = getResourceEntry(resourceEntryId, userId);

        if (resourceEntry == null) {
            throw new EntityInvalidException("The provided resource entry was NULL");
        }
        if (resourceEntry.getName() == null || resourceEntry.getResource() == null) {
            throw new EntityInvalidException("The resource entry was missing required fields");
        }
        if (!oldResourceEntry.getName().equals(resourceEntry.getName())) {
            if (hasResourceEntry(resourceEntry.getResource(), resourceEntry.getName(), userId)) {
                throw new EntityConflictException(
                        "Resource Entry with the specified resource and name already exists: "
                        + " Resource = " + resourceEntry.getResource()
                        + ", Name = " + resourceEntry.getName());
            }
        }

        resourceEntry.setId(resourceEntryId);
        resourceEntry.setCreated(oldResourceEntry.getCreated());
        resourceEntry.setModified(new Date());

        resourceEntryDao.update(resourceEntry, userId);
    }
}
