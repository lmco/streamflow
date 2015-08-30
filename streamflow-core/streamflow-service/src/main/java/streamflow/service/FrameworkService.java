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
package streamflow.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streamflow.datastore.core.FrameworkDao;
import streamflow.model.Component;
import streamflow.model.ComponentConfig;
import streamflow.model.ComponentInterface;
import streamflow.model.ComponentProperty;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

    /**
     * Process the annotations found in a framework jar
     *
     * @param jarFile
     * @return a FrameworkConfig or null if no annotations were found
     */
    public FrameworkConfig processFrameworkAnnotations(File jarFile) {
        FrameworkConfig config = new FrameworkConfig();
        ArrayList<ComponentConfig> components = new ArrayList<ComponentConfig>();
        String frameworkLevel = null;
        boolean foundFrameworkAnnotations = false;
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(jarFile);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entry.isDirectory()) {
                    if (frameworkLevel != null) {
                        if (entryName.startsWith(frameworkLevel) == false) {
                            frameworkLevel = null;
                        }
                    }
                    ZipEntry packageInfoEntry = zipFile.getEntry(entryName + "package-info.class");
                    if (packageInfoEntry != null) {
                        InputStream fileInputStream = zipFile.getInputStream(packageInfoEntry);
                        DataInputStream dstream = new DataInputStream(fileInputStream);
                        ClassFile cf = new ClassFile(dstream);
                        String cfName = cf.getName();
                        AnnotationsAttribute attr = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
                        Annotation annotation = attr.getAnnotation("streamflow.annotations.Framework");
                        if (annotation == null) {
                            continue;
                        }

                        frameworkLevel = cfName;
                        foundFrameworkAnnotations = true;
                        StringMemberValue frameworkLabel = (StringMemberValue) annotation.getMemberValue("label");
                        if (frameworkLabel != null) {
                            config.setLabel(frameworkLabel.getValue());
                        }
                        StringMemberValue frameworkName = (StringMemberValue) annotation.getMemberValue("name");
                        if (frameworkName != null) {
                            config.setName(frameworkName.getValue());
                        }
                        StringMemberValue frameworkVersion = (StringMemberValue) annotation.getMemberValue("version");
                        if (frameworkVersion != null) {
                            config.setVersion(frameworkVersion.getValue());
                        }

                        Annotation descriptionAnnotation = attr.getAnnotation("streamflow.annotations.Description");
                        if (descriptionAnnotation != null) {
                            StringMemberValue frameworkDescription = (StringMemberValue) descriptionAnnotation.getMemberValue("value");
                            if (frameworkDescription != null) {
                                config.setDescription(frameworkDescription.getValue());
                            }
                        }

                    }
                } else if (frameworkLevel != null && entryName.endsWith(".class") && entryName.endsWith("package-info.class") == false) {
                    ZipEntry packageInfoEntry = zipFile.getEntry(entryName);
                    InputStream fileInputStream = zipFile.getInputStream(packageInfoEntry);
                    DataInputStream dstream = new DataInputStream(fileInputStream);
                    ClassFile cf = new ClassFile(dstream);
                    String cfName = cf.getName();
                    AnnotationsAttribute attr = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
                    if (attr == null) {
                        continue;
                    }
                    Annotation componentAnnotation = attr.getAnnotation("streamflow.annotations.Component");

                    if (componentAnnotation == null) {
                        continue;
                    }

                    ComponentConfig component = new ComponentConfig();
                    component.setMainClass(cf.getName());
                    StringMemberValue componentLabel = (StringMemberValue) componentAnnotation.getMemberValue("label");
                    if (componentLabel != null) {
                        component.setLabel(componentLabel.getValue());
                    }
                    StringMemberValue componentName = (StringMemberValue) componentAnnotation.getMemberValue("name");
                    if (componentName != null) {
                        component.setName(componentName.getValue());
                    }
                    StringMemberValue componentType = (StringMemberValue) componentAnnotation.getMemberValue("type");
                    if (componentType != null) {
                        component.setType(componentType.getValue());
                    }
                    StringMemberValue componentIcon = (StringMemberValue) componentAnnotation.getMemberValue("icon");
                    if (componentIcon != null) {
                        component.setIcon(componentIcon.getValue());
                    }

                    Annotation componentDescriptionAnnotation = attr.getAnnotation("streamflow.annotations.Description");
                    if (componentDescriptionAnnotation != null) {
                        StringMemberValue componentDescription = (StringMemberValue) componentDescriptionAnnotation.getMemberValue("value");
                        if (componentDescription != null) {
                            component.setDescription(componentDescription.getValue());
                        }
                    }

                    Annotation componentInputsAnnotation = attr.getAnnotation("streamflow.annotations.ComponentInputs");
                    if (componentInputsAnnotation != null) {
                        ArrayList<ComponentInterface> inputs = new ArrayList<ComponentInterface>();
                        ArrayMemberValue componentInputs = (ArrayMemberValue) componentInputsAnnotation.getMemberValue("value");
                        for (MemberValue value : componentInputs.getValue()) {
                            AnnotationMemberValue annotationMember = (AnnotationMemberValue) value;
                            Annotation annotationValue = annotationMember.getValue();
                            StringMemberValue keyAnnotationValue = (StringMemberValue) annotationValue.getMemberValue("key");
                            StringMemberValue descriptionAnnotationValue = (StringMemberValue) annotationValue.getMemberValue("description");
                            ComponentInterface inputInterface = new ComponentInterface();
                            if (keyAnnotationValue != null) {
                                inputInterface.setKey(keyAnnotationValue.getValue());
                            }
                            if (descriptionAnnotationValue != null) {
                                inputInterface.setDescription(descriptionAnnotationValue.getValue());
                            }
                            inputs.add(inputInterface);
                        }

                        component.setInputs(inputs);
                    }

                    Annotation componentOutputsAnnotation = attr.getAnnotation("streamflow.annotations.ComponentOutputs");
                    if (componentOutputsAnnotation != null) {
                        ArrayList<ComponentInterface> outputs = new ArrayList<ComponentInterface>();
                        ArrayMemberValue componentOutputs = (ArrayMemberValue) componentOutputsAnnotation.getMemberValue("value");
                        for (MemberValue value : componentOutputs.getValue()) {
                            AnnotationMemberValue annotationMember = (AnnotationMemberValue) value;
                            Annotation annotationValue = annotationMember.getValue();
                            StringMemberValue keyAnnotationValue = (StringMemberValue) annotationValue.getMemberValue("key");
                            StringMemberValue descriptionAnnotationValue = (StringMemberValue) annotationValue.getMemberValue("description");
                            ComponentInterface outputInterface = new ComponentInterface();
                            if (keyAnnotationValue != null) {
                                outputInterface.setKey(keyAnnotationValue.getValue());
                            }
                            if (descriptionAnnotationValue != null) {
                                outputInterface.setDescription(descriptionAnnotationValue.getValue());
                            }
                            outputs.add(outputInterface);
                        }

                        component.setOutputs(outputs);
                    }

                    List<MethodInfo> memberMethods = cf.getMethods();
                    if (memberMethods != null) {
                        ArrayList<ComponentProperty> properties = new ArrayList<ComponentProperty>();

                        for (MethodInfo method : memberMethods) {
                            AnnotationsAttribute methodAttr = (AnnotationsAttribute) method.getAttribute(AnnotationsAttribute.visibleTag);
                            if (methodAttr == null) {
                                continue;
                            }
                            Annotation propertyAnnotation = methodAttr.getAnnotation("streamflow.annotations.ComponentProperty");
                            if (propertyAnnotation == null) {
                                continue;
                            }

                            ComponentProperty property = new ComponentProperty();

                            StringMemberValue propertyName = (StringMemberValue) propertyAnnotation.getMemberValue("name");
                            if (propertyName != null) {
                                property.setName(propertyName.getValue());
                            }
                            StringMemberValue propertylabel = (StringMemberValue) propertyAnnotation.getMemberValue("label");
                            if (propertylabel != null) {
                                property.setLabel(propertylabel.getValue());
                            }
                            StringMemberValue propertyType = (StringMemberValue) propertyAnnotation.getMemberValue("type");
                            if (propertyType != null) {
                                property.setType(propertyType.getValue());
                            }
                            StringMemberValue propertyDefaultValue = (StringMemberValue) propertyAnnotation.getMemberValue("defaultValue");
                            if (propertyDefaultValue != null) {
                                property.setDefaultValue(propertyDefaultValue.getValue());
                            }
                            BooleanMemberValue propertyRequired = (BooleanMemberValue) propertyAnnotation.getMemberValue("required");
                            if (propertyRequired != null) {
                                property.setRequired(propertyRequired.getValue());
                            }

                            Annotation methodDescriptionAnnotation = methodAttr.getAnnotation("streamflow.annotations.Description");
                            if (methodDescriptionAnnotation != null) {
                                StringMemberValue methodDescription = (StringMemberValue) methodDescriptionAnnotation.getMemberValue("value");
                                if (methodDescription != null) {
                                    property.setDescription(methodDescription.getValue());
                                }
                            }
                            properties.add(property);
                        }
                        component.setProperties(properties);

                    }

                    components.add(component);
                }
            }

            config.setComponents(components);

            // return null if no framework annotations were located
            if (foundFrameworkAnnotations == false) {
                return null;
            }

            return config;
        } catch (IOException ex) {
            LOG.error("Error while parsing framework annotations: ", ex);

            throw new EntityInvalidException("Error while parsing framework annotations: "
                    + ex.getMessage());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    LOG.error("Error while closing framework zip");
                }
            }
        }

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
                frameworkConfig = processFrameworkAnnotations(tempFrameworkFile);
                if (frameworkConfig == null) {
                    throw new EntityInvalidException(
                            "The framework configuration file was not found in the framework jar");
                }
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
