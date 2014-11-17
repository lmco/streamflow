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
import streamflow.datastore.core.ComponentDao;
import streamflow.model.Component;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;

@Singleton
public class ComponentService {

    private final ComponentDao componentDao;
    
    private final FileService fileService;

    @Inject
    public ComponentService(ComponentDao componentDao, FileService fileService) {
        this.componentDao = componentDao;
        this.fileService = fileService;
    }

    public List<Component> listComponents(String visbility) {
        return componentDao.findAll();
    }

    public List<Component> listComponentsWithFramework(String framework) {
        return componentDao.findAllWithFramework(framework);
    }

    public Component addComponent(Component component) {
        if (component == null) {
            throw new EntityInvalidException("The provided component is NULL");
        }
        if (component.getName() == null || component.getLabel() == null 
                || component.getFramework() == null || component.getType() == null) {
            throw new EntityInvalidException("The component is missing required fields");
        }
        if (hasComponent(component.getFramework(), component.getName())) {
            throw new EntityConflictException(
                    "Component with the specified framework and name already exists: Framework = " 
                            + component.getFramework() + ", Name = " + component.getName());
        }

        // Forcibly update component attributes that should not be user editable
        component.setId(component.getFramework() + "_" + component.getName());
        component.setCreated(new Date());
        component.setModified(component.getCreated());

        return componentDao.save(component);
    }

    public Component getComponent(String componentId) {
        Component component = componentDao.findById(componentId);
        if (component == null) {
            throw new EntityNotFoundException(
                    "Component with the specified ID not found: ID = " + componentId);
        }

        return component;
    }

    public Component getComponent(String framework, String name) {
        Component component = componentDao.findByFrameworkAndName(framework, name);
        if (component == null) {
            throw new EntityNotFoundException(
                    "Component with the specified framework and name not found: "
                    + "Framework = " + framework + ", Name = " + name);
        }

        return component;
    }

    public boolean hasComponent(String framework, String name) {
        return componentDao.findByFrameworkAndName(framework, name) != null;
    }

    public void deleteComponent(String componentId) {
        Component component = getComponent(componentId);
        
        if (component.getIconId() != null) {
            fileService.deleteFile(component.getIconId());
        }
        
        componentDao.delete(component);
    }

    public void updateComponent(String componentId, Component component) {
        Component oldComponent = getComponent(componentId);
        
        if (component == null) {
            throw new EntityInvalidException("The provided component is NULL");
        }
        if (component.getName() == null || component.getLabel() == null 
                || component.getFramework() == null || component.getType() == null) {
            throw new EntityInvalidException("The component is missing required fields");
        }
        if (hasComponent(component.getFramework(), component.getName())) {
            throw new EntityConflictException(
                    "Component with the specified framework and name already exists: Framework = " 
                            + component.getFramework() + ", Name = " + component.getName());
        }

        component.setId(oldComponent.getId());
        component.setCreated(oldComponent.getCreated());
        component.setModified(new Date());

        componentDao.update(component);
    }
    
    public byte[] getComponentIcon(String componentId) {
        Component component = getComponent(componentId);
        
        byte[] iconData = fileService.getFileContent(component.getIconId());
        if (iconData == null) {
            throw new ServiceException("Error retrieving component icon: ID = "
                    + componentId + ", Icon ID = " + component.getIconId());
        }

        return iconData;
    }
    
    public byte[] getComponentIcon(String framework, String name) {
        Component component = getComponent(framework, name);
        
        byte[] iconData = fileService.getFileContent(component.getIconId());
        if (iconData == null) {
            throw new ServiceException("Error retrieving component icon: ID = "
                    + component.getId() + ", Icon ID = " + component.getIconId());
        }

        return iconData;
    }
}
