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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import streamflow.datastore.core.FrameworkDao;
import streamflow.model.Component;
import streamflow.model.ComponentConfig;
import streamflow.model.FileInfo;
import streamflow.model.Framework;
import streamflow.model.FrameworkConfig;
import streamflow.model.Resource;
import streamflow.model.ResourceConfig;
import streamflow.model.Serialization;
import streamflow.model.SerializationConfig;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;
import streamflow.service.util.IDUtils;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FrameworkService {

    public static Logger LOG = LoggerFactory.getLogger(FrameworkService.class);

    private final FrameworkDao frameworkDao;

    private final FileService fileService;

    private final ComponentService componentService;

    private final ResourceService resourceService;

    private final SerializationService serializationService;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Inject
    public FrameworkService(FrameworkDao frameworkDao, FileService fileService,
            ComponentService componentService, ResourceService resourceService,
            SerializationService serializationService) {
        this.frameworkDao = frameworkDao;
        this.fileService = fileService;
        this.componentService = componentService;
        this.resourceService = resourceService;
        this.serializationService = serializationService;
    }

    public List<Framework> listFrameworks() {
        return frameworkDao.findAll();
    }

    public Framework addFramework(byte[] frameworkJar, boolean isPublic) {
        if (frameworkJar == null) {
            throw new EntityInvalidException("The provided framework jar was NULL");
        }
        return processFrameworkJar(frameworkJar, isPublic);
    }

    public Framework getFramework(String frameworkId) {
        Framework framework = frameworkDao.findById(frameworkId);
        if (framework == null) {
            throw new EntityNotFoundException(
                    "Framework with the specified ID could not be found: ID = " + frameworkId);
        }

        return framework;
    }

    public boolean hasFramework(String frameworkName) {
        return frameworkDao.findById(frameworkName) != null;
    }

    public void deleteFramework(String frameworkId) {
        Framework framework = getFramework(frameworkId);

        // Before deleting the framework clear all children
        for (Component component : componentService.listComponentsWithFramework(frameworkId)) {
            componentService.deleteComponent(component.getId());
        }

        for (Resource resource : resourceService.listResourcesWithFramework(frameworkId)) {
            resourceService.deleteResource(resource.getId());
        }

        for (Serialization serialization : serializationService.listSerializationsWithFramework(frameworkId)) {
            serializationService.deleteSerialization(serialization.getId());
        }

        // Delete the framework jar file
        fileService.deleteFile(framework.getJarId());

        frameworkDao.delete(framework);
    }

    public byte[] getFrameworkJar(String frameworkId) {
        Framework framework = getFramework(frameworkId);

        byte[] frameworkJarContent = fileService.getFileContent(framework.getJarId());
        if (frameworkJarContent == null) {
            throw new ServiceException("Error retrieving framework jar: ID = "
                    + frameworkId + ", Jar ID = " + framework.getJarId());
        }

        return frameworkJarContent;
    }

    public FileInfo getFrameworkFileInfo(String frameworkId) {
        Framework framework = getFramework(frameworkId);

        FileInfo frameworkFileInfo = fileService.getFileInfo(framework.getJarId());
        if (frameworkFileInfo == null) {
            throw new ServiceException("Error retrieving framework file info: ID = "
                    + frameworkId + ", Jar ID = " + framework.getJarId());
        }

        return frameworkFileInfo;
    }

    public Framework processFrameworkJar(byte[] frameworkJar, boolean isPublic) {
        Framework framework = null;
        
        try {
            String frameworkHash = DigestUtils.md5Hex(frameworkJar);
            
            // Write out a temporary file for the jar so it can be processed
            File tempFrameworkFile = new File(StreamflowEnvironment.getFrameworksDir(),
                    frameworkHash + ".jar");

            FileUtils.writeByteArrayToFile(tempFrameworkFile, frameworkJar);
            
            FrameworkConfig frameworkConfig = processFrameworkConfig(tempFrameworkFile);

            if (frameworkConfig != null) {
                String frameworkId = frameworkConfig.getName();

                // If the framework already exists, delete it first to clear out children
                if (hasFramework(frameworkId)) {
                    deleteFramework(frameworkId);
                }

                framework = new Framework();
                framework.setId(frameworkId);
                framework.setName(frameworkConfig.getName());
                framework.setVersion(frameworkConfig.getVersion());
                framework.setLabel(frameworkConfig.getLabel());
                framework.setDescription(frameworkConfig.getDescription());
                framework.setEnabled(true);
                framework.setCount(frameworkConfig.getComponents().size());
                framework.setCreated(new Date());
                framework.setModified(framework.getCreated());
                framework.setJarId(storeFrameworkJar(frameworkJar));
                framework.setPublic(isPublic);
                framework = frameworkDao.save(framework);

                // Load each of the entity types from the framework config
                processFrameworkComponents(framework, frameworkConfig, tempFrameworkFile);
                processFrameworkResources(framework, frameworkConfig);
                processFrameworkSerializations(framework, frameworkConfig);

                // Delete the temporary file and squelch delete errors
                //FileUtils.deleteQuietly(tempFrameworkFile);
            } else {
                throw new EntityInvalidException(
                        "The framework config could not be deserialized");
            }
        } catch (IOException ex) {
            LOG.error("Exception while processing the framework jar", ex);

            throw new EntityInvalidException(
                    "Exception while processing the framework framework: Exception = "  
                            + ex.getMessage());
        }

        return framework;
    }
    
    public String storeFrameworkJar(byte[] frameworkJar) {
        FileInfo frameworkFile = new FileInfo();
        frameworkFile.setFileName(IDUtils.randomUUID());
        frameworkFile.setFileType("application/java-archive");
        frameworkFile.setFileSize(frameworkJar.length);
        frameworkFile.setContentHash(DigestUtils.md5Hex(frameworkJar));
        frameworkFile.setCreated(new Date());
        frameworkFile.setModified(frameworkFile.getCreated());

        frameworkFile = fileService.saveFile(frameworkFile, frameworkJar);
        if (frameworkFile == null) {
            throw new ServiceException("Unable to save framework jar file");
        }
        
        return frameworkFile.getId();
    }
    
    public FrameworkConfig processFrameworkConfig(File tempFrameworkFile) {
        FrameworkConfig frameworkConfig = null;

        try {
            JarFile frameworkJarFile = new JarFile(tempFrameworkFile.getAbsoluteFile());
            
            JarEntry frameworkYamlEntry = frameworkJarFile.getJarEntry("STREAMFLOW-INF/framework.yml");
            
            JarEntry frameworkJsonEntry = frameworkJarFile.getJarEntry("STREAMFLOW-INF/framework.json");

            if (frameworkYamlEntry != null) {
                String frameworkYaml = IOUtils.toString(
                        frameworkJarFile.getInputStream(frameworkYamlEntry));

                // Attempt to deserialize the inbuilt streams-framework.json 
                frameworkConfig = yamlMapper.readValue(
                        frameworkYaml, FrameworkConfig.class);
            } else if (frameworkJsonEntry != null) {
                String frameworkJson = IOUtils.toString(
                        frameworkJarFile.getInputStream(frameworkJsonEntry));
                
                // Attempt to deserialize the inbuilt streams-framework.json 
                frameworkConfig = jsonMapper.readValue(
                        frameworkJson, FrameworkConfig.class);
            } else {
                throw new EntityInvalidException(
                        "The framework configuration file was not found in the framework jar");
            }
        } catch (IOException ex) {
            LOG.error("Error while loaded the framework configuration: ", ex);
            
            throw new EntityInvalidException("Error while loading the framework configuration: "
                + ex.getMessage());
        }
            
        return frameworkConfig;
    }
    
    public void processFrameworkComponents(Framework framework, FrameworkConfig frameworkConfig, File frameworkFile) {
        for (ComponentConfig componentConfig : frameworkConfig.getComponents()) {
            Component component = new Component();
            component.setName(componentConfig.getName());
            component.setLabel(componentConfig.getLabel());
            component.setType(componentConfig.getType());
            component.setConfig(componentConfig);
            component.setFramework(framework.getName());
            component.setFrameworkLabel(framework.getLabel());
            component.setVersion(frameworkConfig.getVersion());
            component.setIconId(loadFrameworkComponentIcon(componentConfig, frameworkFile));

            componentService.addComponent(component);
        }
    }
    
    public void processFrameworkResources(Framework framework, FrameworkConfig frameworkConfig) {
        for (ResourceConfig resourceConfig : frameworkConfig.getResources()) {
            Resource resource = new Resource();
            resource.setName(resourceConfig.getName());
            resource.setLabel(resourceConfig.getLabel());
            resource.setModified(new Date());
            resource.setConfig(resourceConfig);
            resource.setFramework(framework.getName());
            resource.setFrameworkLabel(framework.getLabel());
            resource.setVersion(frameworkConfig.getVersion());

            resourceService.addResource(resource);
        }
    }
    
    public void processFrameworkSerializations(Framework framework, FrameworkConfig frameworkConfig) {
        // Keep track of the order or the serializations specified in the config
        int serializationPriority = 0;

        for (SerializationConfig serializationConfig : frameworkConfig.getSerializations()) {
            Serialization serialization = new Serialization();
            serialization.setPriority(serializationPriority++);
            serialization.setTypeClass(serializationConfig.getTypeClass());
            serialization.setSerializerClass(serializationConfig.getSerializerClass());
            serialization.setFramework(framework.getName());
            serialization.setFrameworkLabel(framework.getLabel());
            serialization.setVersion(framework.getVersion());

            // Persist the new serialization
            serializationService.addSerialization(serialization);
        }
    }
    
    public String loadFrameworkComponentIcon(ComponentConfig componentConfig, File frameworkFile) {
        String iconId = null;
        byte[] iconData = null;

        if (componentConfig.getIcon() != null) {
            try {
                JarFile frameworkJarFile = new JarFile(frameworkFile);
                
                JarEntry iconEntry = frameworkJarFile.getJarEntry(componentConfig.getIcon());
                if (iconEntry != null) {
                    iconData = IOUtils.toByteArray(frameworkJarFile.getInputStream(iconEntry));
                }
            } catch (IOException ex) {
                LOG.error("Error occurred while loading the provided component icon: ", ex);
            }
        }

        if (iconData == null) {
            try {
                if (componentConfig.getType().equalsIgnoreCase(Component.STORM_SPOUT_TYPE)) {
                    iconData = IOUtils.toByteArray(Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream("icons/storm-spout.png"));
                } else if (componentConfig.getType().equalsIgnoreCase(Component.STORM_BOLT_TYPE)) {
                    iconData = IOUtils.toByteArray(Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream("icons/storm-bolt.png"));
                } else {
                    iconData = IOUtils.toByteArray(Thread.currentThread()
                        .getContextClassLoader().getResourceAsStream("icons/storm-trident.png"));
                }
            } catch (IOException ex) {
                LOG.error("Error occurred while loading the default component icon: ", ex);
            }
        }
        
        if (iconData != null) {
            FileInfo iconFile = new FileInfo();
            iconFile.setFileName(iconFile.getFileName());
            iconFile.setFileType("image/png");
            iconFile.setFileSize(iconData.length);
            iconFile.setContentHash(DigestUtils.md5Hex(iconData));
            
            iconFile = fileService.saveFile(iconFile, iconData);

            iconId = iconFile.getId();
        }
        
        return iconId;
    }
}
