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
import streamflow.datastore.core.ResourceDao;
import streamflow.model.Resource;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;

@Singleton
public class ResourceService {

    private final ResourceDao resourceDao;

    @Inject
    public ResourceService(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
    }

    public List<Resource> listResources() {
        return resourceDao.findAll();
    }

    public List<Resource> listResourcesWithFramework(String framework) {
        return resourceDao.findAllWithFramework(framework);
    }

    public Resource addResource(Resource resource) {
        if (resource == null) {
            throw new EntityInvalidException("The provided resource was NULL");
        }
        if (resource.getName() == null || resource.getLabel() == null 
                || resource.getFramework() == null) {
            throw new EntityInvalidException("The resource was missing required fields");
        }
        if (hasResource(resource.getFramework(), resource.getName())) {
            throw new EntityConflictException(
                    "Resource with the specified framework and name already exits: Framework = " 
                            + resource.getFramework() + ", Name = " + resource.getName());
        }

        resource.setId(resource.getFramework() + "_" + resource.getName());
        resource.setCreated(new Date());
        resource.setModified(resource.getCreated());

        return resourceDao.save(resource);
    }

    public Resource getResource(String resourceId) {
        Resource resource = resourceDao.findById(resourceId);
        if (resource == null) {
            throw new EntityNotFoundException(
                    "Resource with the specified ID could not be found: ID = " + resourceId);
        }
        return resource;
    }

    public Resource getResource(String framework, String name) {
        Resource resource = resourceDao.findByFrameworkAndName(framework, name);
        if (resource == null) {
            throw new EntityNotFoundException(
                    "Resource with the specified framework and name could not be found: Framework = " 
                            + framework + ", Name = " + name);
        }
        return resource;
    }

    public boolean hasResource(String framework, String name) {
        return resourceDao.findByFrameworkAndName(framework, name) != null;
    }

    public void deleteResource(String resourceId) {
        resourceDao.delete(getResource(resourceId));
    }

    public void updateResource(String resourceId, Resource resource) {
        Resource oldResource = getResource(resourceId);

        if (resource == null) {
            throw new EntityInvalidException("The provided resource was NULL");
        }
        if (resource.getName() == null || resource.getLabel() == null 
                || resource.getFramework() == null) {
            throw new EntityInvalidException("The resource was missing required fields");
        }
        if (!oldResource.getName().equals(resource.getName())) {
            if (hasResource(resource.getFramework(), resource.getName())) {
                throw new EntityConflictException(
                    "Resource with the specified framework and name already exists: Framework = " 
                            + resource.getFramework() + ", Name = " + resource.getName());
            }
        }

        resource.setId(resourceId);
        resource.setCreated(oldResource.getCreated());
        resource.setModified(new Date());

        resourceDao.update(resource);
    }
}
