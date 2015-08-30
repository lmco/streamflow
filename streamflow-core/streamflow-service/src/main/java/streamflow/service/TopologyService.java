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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import streamflow.datastore.core.TopologyDao;
import streamflow.engine.StormEngine;
import streamflow.model.Cluster;
import streamflow.model.Component;
import streamflow.model.ComponentConfig;
import streamflow.model.ComponentProperty;
import streamflow.model.Resource;
import streamflow.model.ResourceConfig;
import streamflow.model.ResourceEntry;
import streamflow.model.ResourceProperty;
import streamflow.model.Serialization;
import streamflow.model.Topology;
import streamflow.model.TopologyComponent;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologyLog;
import streamflow.model.TopologyLogCriteria;
import streamflow.model.TopologyLogPage;
import streamflow.model.TopologyResourceEntry;
import streamflow.model.TopologySerialization;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.storm.TopologyInfo;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;
import streamflow.service.util.IDUtils;
import streamflow.service.util.JarBuilder;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TopologyService {
    
    public static final Logger LOG = LoggerFactory.getLogger(TopologyService.class);

    private final TopologyDao topologyDao;

    private final ComponentService componentService;

    private final ResourceService resourceService;

    private final ResourceEntryService resourceEntryService;

    private final SerializationService serializationService;

    private final FileService fileService;

    private final FrameworkService frameworkService;

    private final ClusterService clusterService;

    private final LogService logService;
    
    private final StormEngine stormEngine;
    
    private final StreamflowConfig streamflowConfig;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public TopologyService(TopologyDao topologyDao, ComponentService componentService, 
            ResourceService resourceService, ResourceEntryService resourceEntryService, 
            SerializationService serializationService, FileService fileService, 
            FrameworkService frameworkService, ClusterService clusterService, 
            LogService logService, StormEngine stormEngine, StreamflowConfig streamflowConfig) {
        this.topologyDao = topologyDao;
        this.componentService = componentService;
        this.resourceService = resourceService;
        this.resourceEntryService = resourceEntryService;
        this.serializationService = serializationService;
        this.fileService = fileService;
        this.frameworkService = frameworkService;
        this.clusterService = clusterService;
        this.logService = logService;
        this.stormEngine = stormEngine;
        this.streamflowConfig = streamflowConfig;
    }

    public List<Topology> listAllTopologies() {
        return topologyDao.findAll();
    }

    public List<Topology> listTopologies(String userId) {
        List<Topology> topologies = topologyDao.findAll(userId);
        
        // Get the live status for all topologies
        for (Topology topology : topologies) {
            // Get live topology status from the storm engine
            TopologyInfo topologyInfo = stormEngine.getTopologyInfo(topology);
            
            topology.setStatus(topologyInfo.getStatus());
        }

        return topologies;
    }

    public Topology createTopology(Topology topology, String userId) {
        if (topology == null) {
            throw new EntityInvalidException("The provided topology is NULL");
        }
        if (topology.getName() == null || topology.getType() == null) {
            throw new EntityInvalidException("The topology is missing required fields");
        }
        if (hasTopology(topology.getName(), userId)) {
            throw new EntityConflictException(
                    "Topology with the specified name already exists: Name = " + topology.getName());
        }

        // Generate a topology id if none is present
        topology.setId(IDUtils.formatId(topology.getName()));
        topology.setCreated(new Date());
        topology.setModified(topology.getCreated());
        topology.setUserId(userId);

        return topologyDao.save(topology, userId);
    }

    public Topology getTopology(String topologyId, String userId) {
        Topology topology = topologyDao.findById(topologyId, userId);
        if (topology == null) {
            throw new EntityNotFoundException(
                    "Topology with the specified ID could not be found: ID = " 
                            + topologyId);
        }

        // Get the updated status for the topology
        TopologyInfo topologyInfo = stormEngine.getTopologyInfo(topology);
        topology.setStatus(topologyInfo.getStatus());

        //topologyDao.update(topology);

        return topology;
    }
    
    public boolean hasTopology(String topologyName, String userId) {
        return topologyDao.findByName(topologyName, userId) != null;
    }

    public void deleteTopology(String topologyId, String userId) {
        Topology topology = getTopology(topologyId, userId);

        // Clear the topology before deletion to make sure it is killed and all temp data removed
        clearTopology(topologyId, userId);

        topologyDao.delete(topology);
    }

    public void updateTopology(String topologyId, Topology topology, String userId) {
        Topology oldTopology = getTopology(topologyId, userId);

        if (topology == null) {
            throw new EntityInvalidException("The provided topology is NULL");
        }
        if (topology.getName() == null || topology.getType() == null) {
            throw new EntityInvalidException("The topology is missing required fields");
        }
        if (!oldTopology.getName().equals(topology.getName())) {
            if (hasTopology(topology.getName(), userId)) {
                throw new EntityConflictException(
                        "Topology with the specified name already exists: Name = " 
                                + topology.getName());
            }
        }

        topology.setId(topologyId);
        topology.setCreated(oldTopology.getCreated());
        topology.setModified(new Date());

        topologyDao.update(topology);
    }

    public Topology submitTopology(String topologyId, String userId, String clusterId,
            String logLevel, String classLoaderPolicy) {
        
        LOG.info("Submitting topology: Topology ID = " + topologyId + ", Cluster ID = " + clusterId
                 + ", Log Level = " + logLevel + ", Class Loader Policy = " + classLoaderPolicy);
        
        // Always attempt to kill the topology before submitting it in case it is already active
        killTopology(topologyId, 0, false, userId);
        
        // Clear out all previous project artifacts before building a new one
        clearTopology(topologyId, userId);
        
        // Intialize the deployed topology config with current settings
        Topology topology = initializeTopologyObject(getTopology(topologyId, userId));
        if (topology != null) {
            Cluster cluster = clusterService.getCluster(clusterId);
            
            // Generate the jar for the new topology
            String projectId = generateTopologyJar(topology, cluster);
            if (projectId != null) {
                // Update the topology entity with the deployed project info
                topology.setProjectId(projectId);
                topology.setSubmitted(new Date());
                topology.setClusterId(cluster.getId());
                topology.setClusterName(cluster.getDisplayName());
                topology.setLogLevel(logLevel);
                topology.setClassLoaderPolicy(classLoaderPolicy);
                topology.setStatus("ACTIVE");
                
                topology = stormEngine.submitTopology(topology, cluster);

                topologyDao.update(topology);
            } else {
                LOG.error("There was an error generating the topology jar");
            }
        } else {
            LOG.error("There was an error initializing the topology object");
        }

        return topology;
    }

    public void killTopology(String topologyId, int waitTimeSecs, boolean async, String userId) {
        Topology topology = getTopology(topologyId, userId);

        if (stormEngine.killTopology(topology, waitTimeSecs, async)) {
            // Update the topology entity status and update the killed date to now
            topology.setStatus("KILLED");
            topology.setKilled(new Date());

            topologyDao.update(topology);
        }
    }

    public void clearTopology(String topologyId, String userId) {
        // Reset the topology status in the database
        Topology topology = getTopology(topologyId, userId);

        // Clear the generated topology jar and local log file
        clearTopologyProject(topology.getProjectId());
        
        // Delete local and cluster log files from the log server
        clearTopologyLog(topology.getId(), userId);

        // Reset the status of the topology back to IDLE to indicate no deployed status
        topology.setStatus("IDLE");
        topology.setProjectId(null);
        topology.setClusterId(null);
        topology.setClusterName(null);
        topology.setLogLevel(null);
        topology.setClassLoaderPolicy(null);
        topology.setSubmitted(null);
        topology.setKilled(null);

        topologyDao.update(topology);
    }

    public TopologyInfo getTopologyInfo(String topologyId, String userId) {
        return stormEngine.getTopologyInfo(getTopology(topologyId, userId));
    }

    public TopologyLog getTopologyLogLocal(String topologyId, String userId, long offset, long limit) {
        return logService.getTopologyLogLocal(getTopology(topologyId, userId), offset, limit);
    }
    
    public TopologyLogPage getTopologyLogCluster(String topologyId, String userId, 
            TopologyLogCriteria criteria) {
        Topology topology = getTopology(topologyId, userId);
        
        Cluster cluster = clusterService.getCluster(topology.getClusterId());
        
        return logService.getTopologyLogCluster(topology, cluster, criteria);
    }
    
    public void clearTopologyLog(String topologyId, String userId) {
        Topology topology = getTopology(topologyId, userId);
        
        Cluster cluster = null;
        if (topology.getClusterId() != null) {
            cluster = clusterService.getCluster(topology.getClusterId());
        }
        
        logService.clearTopologyLog(topology, cluster);
    }
    
    public Topology initializeTopologyObject(Topology topology) {
        try {
            TopologyConfig topologyConfig = (TopologyConfig) SerializationUtils.clone(
                    topology.getCurrentConfig());

            for (TopologyComponent topologyComponent : topologyConfig.getComponents().values()) {
                Component component = componentService.getComponent(
                        topologyComponent.getFramework(), topologyComponent.getName());

                if (component == null) {
                    throw new ServiceException("A component with the specified "
                            + " framework and name could not be found: "
                            + "Framework = " + topologyComponent.getFramework()
                            + "Name = " + topologyComponent.getName());
                }

                ComponentConfig componentConfig = component.getConfig();

                // Iterate over the resource config to build a mapping of names to types
                HashMap<String, String> componentPropertyTypes = new HashMap<String, String>();
                for (ComponentProperty componentProperty : componentConfig.getProperties()) {
                    // Get the property type using "text" as default if not specified
                    String propertyType = componentProperty.getType();
                    if (propertyType == null) {
                        propertyType = "text";
                    }

                    componentPropertyTypes.put(componentProperty.getName(), propertyType);
                }
                
                topologyComponent.setPropertyTypes(componentPropertyTypes);
                topologyComponent.setMainClass(componentConfig.getMainClass());
                topologyComponent.setFrameworkHash(
                        frameworkService.getFrameworkFileInfo(
                                component.getFramework()).getContentHash());
                topologyComponent.setVersion(component.getVersion());
                topologyComponent.getResources().clear();

                // Iterate over each of the properties looking for resource types
                for (ComponentProperty componentProperty : componentConfig.getProperties()) {
                    // The property type is a resource type so fetch the resource entry details
                    if (componentProperty.getType() != null
                            && componentProperty.getType().equalsIgnoreCase("resource")) {
                        String resourceFramework = componentProperty.getOptions().getResourceFramework();
                        String resourceName = componentProperty.getOptions().getResourceName();

                        Resource resource = resourceService.getResource(resourceFramework, resourceName);

                        ResourceConfig resourceConfig = resource.getConfig();

                        // Iterate over the resource config to build a mapping of names to types
                        HashMap<String, String> resourcePropertyTypes = new HashMap<String, String>();
                        for (ResourceProperty resourceProperty : resourceConfig.getProperties()) {
                            resourcePropertyTypes.put(
                                    resourceProperty.getName(), resourceProperty.getType());
                        }

                        String resourceEntryId = topologyComponent.getProperties()
                                .get(componentProperty.getName());

                        if (resourceEntryId != null) {
                            ResourceEntry resourceEntry = resourceEntryService.getResourceEntry(resourceEntryId);

                            // Create a new topology resource to store with the topology node
                            TopologyResourceEntry topologyResource = new TopologyResourceEntry();
                            topologyResource.setId(resourceEntryId);
                            topologyResource.setVersion(resource.getVersion());
                            topologyResource.setFramework(resourceFramework);
                            topologyResource.setFrameworkHash(
                                    frameworkService.getFrameworkFileInfo(
                                            resource.getFramework()).getContentHash());
                            topologyResource.setResource(resourceName);
                            topologyResource.setName(resourceEntry.getName());
                            topologyResource.setDescription(resourceEntry.getDescription());
                            topologyResource.setResourceClass(resourceConfig.getResourceClass());
                            topologyResource.setProperties(resourceEntry.getConfig().getProperties());
                            topologyResource.setPropertyTypes(resourcePropertyTypes);

                            // Add the newly built topology resource to the topology ode
                            topologyComponent.getResources().add(topologyResource);
                        }
                    }
                }
            }

            // Rewrite the modified topology config to the topology so the additional metadata is included
            topology.setDeployedConfig(topologyConfig);

            return topology;
        } catch (ServiceException ex) {
            LOG.error("Exception while initializing the topology config object", ex);

            throw new ServiceException("Exception while initializing the "
                    + "Topology config object: " + ex.getMessage());
        }
    }

    public String generateTopologyJar(Topology topology, Cluster cluster) {
        // Generate a unique artifact ID for the topology
        String projectId = "topology_" + topology.getId() + "_" + System.currentTimeMillis();
        
        try {
            File topologyJarFile = new File(
                    StreamflowEnvironment.getTopologiesDir(), projectId + ".jar");
            
            // Copy the template jar file to the topologies directory as a base for the topology
            FileUtils.writeByteArrayToFile(topologyJarFile, loadTopologyTemplate());
            
            JarBuilder jarBuilder = new JarBuilder(topologyJarFile);
            jarBuilder.open();

            // Keep track of already added dependencies
            HashSet<String> frameworkDependencies = new HashSet<>();

            HashSet<String> processedSerializations = new HashSet<>();

            TopologyConfig topologyConfig = topology.getDeployedConfig();

            // Iterate over all of the nodes and add the dependencies to the lib dir (only once)
            for (TopologyComponent topologyComponent : topologyConfig.getComponents().values()) {
                // Save the node coordinates so each node is added only once
                frameworkDependencies.add(topologyComponent.getFramework());

                // Iterate over the property types for the component to see if files are specified
                for (Map.Entry<String, String> propertyType
                        : topologyComponent.getPropertyTypes().entrySet()) {

                    // Check if the resource property type is a file
                    if (propertyType.getValue() != null
                            && propertyType.getValue().equalsIgnoreCase("file")) {
                        // Get the upload ID from the property value
                        String fileId = topologyComponent.getProperties()
                                .get(propertyType.getKey());

                        // Make sure the user actually specified a file for the field
                        if (StringUtils.isNotBlank(fileId)) {
                            // Embed the upload inside the topology jar
                            if (embedTopologyFile(jarBuilder, fileId)) {
                                // Update the file property to use the file path instead of the file ID
                                topologyComponent.getProperties().put(propertyType.getKey(),
                                        "/files/" + fileId);
                            }
                        }
                    } else if (propertyType.getValue() != null
                            && propertyType.getValue().equalsIgnoreCase("serialization")) {
                        String typeClass = topologyComponent.getProperties()
                                .get(propertyType.getKey());

                        // Only add the serialization if it has not already been added
                        if (StringUtils.isNotBlank(typeClass)
                                && !processedSerializations.contains(typeClass)) {
                            Serialization serialization = serializationService
                                    .getSerializationWithTypeClass(typeClass);

                            TopologySerialization topologySerialization
                                    = new TopologySerialization();
                            topologySerialization.setTypeClass(serialization.getTypeClass());
                            topologySerialization.setSerializerClass(serialization.getSerializerClass());
                            topologySerialization.setFramework(serialization.getFramework());
                            topologySerialization.setFrameworkHash(
                                    frameworkService.getFrameworkFileInfo(
                                            serialization.getFramework()).getContentHash());
                            topologySerialization.setVersion(serialization.getVersion());

                            topologyConfig.getSerializations().add(topologySerialization);

                            processedSerializations.add(serialization.getTypeClass());

                            // Save the node coordinates so each node is added only once
                            frameworkDependencies.add(serialization.getFramework());
                        }
                    }
                }

                // Iterate over the resources to add the required dependencies to the build
                for (TopologyResourceEntry topologyResource : topologyComponent.getResources()) {
                    // Save the node coordinates so each node is added only once
                    frameworkDependencies.add(topologyResource.getFramework());

                    // Iterate over the property types for the resoruce to see if files are specified
                    for (Map.Entry<String, String> resourceType
                            : topologyResource.getPropertyTypes().entrySet()) {

                        // Check if the resource property type is a file
                        if (resourceType.getValue() != null
                                && resourceType.getValue().equalsIgnoreCase("file")) {
                            // Get the upload ID from the property value
                            String fileId = topologyResource.getProperties()
                                    .get(resourceType.getKey());

                            // Make sure the user actually specified a file for the field
                            if (StringUtils.isNotBlank(fileId)) {
                                // Embed the upload inside the topology jar
                                if (embedTopologyFile(jarBuilder, fileId)) {
                                    // Update the resource property to use the file path instead of the upload ID
                                    topologyResource.getProperties().put(resourceType.getKey(),
                                            "/files/" + fileId);
                                }
                            }
                        }
                    }
                }

                List<Serialization> serializations
                        = serializationService.listSerializationsWithFramework(
                                topologyComponent.getFramework());

                // Iterate over each of the serializations for the current framework
                for (Serialization serialization : serializations) {
                    // Iterate over the serializations for the current node and add them only once
                    if (!processedSerializations.contains(serialization.getTypeClass())) {
                        TopologySerialization topologySerialization
                                = new TopologySerialization();
                        topologySerialization.setTypeClass(serialization.getTypeClass());
                        topologySerialization.setSerializerClass(serialization.getSerializerClass());
                        topologySerialization.setFramework(topologyComponent.getFramework());
                        topologySerialization.setFrameworkHash(
                                frameworkService.getFrameworkFileInfo(
                                        serialization.getFramework()).getContentHash());
                        topologySerialization.setVersion(topologyComponent.getVersion());
                        topologyConfig.getSerializations().add(topologySerialization);

                        processedSerializations.add(serialization.getTypeClass());
                    }
                }
            }
            
            // Build the relam framework dir as a temporary storage for files
            File realmFrameworkDir = new File(StreamflowEnvironment.getFrameworksDir(), projectId);
            realmFrameworkDir.mkdirs();

            // Iterate over all of the identified dependencies and add them to topology jar
            for (String frameworkDependency : frameworkDependencies) {
                String frameworkHash = frameworkService.getFrameworkFileInfo(
                            frameworkDependency).getContentHash();
                
                if (cluster.getId().equalsIgnoreCase(Cluster.LOCAL)) {
                    File frameworkJarFile = new File(StreamflowEnvironment.getFrameworksDir(), 
                            frameworkHash + ".jar");
                    
                    // Write out the file to disk only if it is not already there
                    if (!frameworkJarFile.exists()) {
                        byte[] frameworkJarData = frameworkService.getFrameworkJar(frameworkDependency);

                        FileUtils.writeByteArrayToFile(frameworkJarFile, frameworkJarData);
                    }
                    
                } else {
                    // Only need to embed files within the topology jar for cluster deploys
                    String frameworkJarPath = "STREAMFLOW-INF" + File.separator + "lib"
                            + File.separator + frameworkHash + ".jar";

                    byte[] frameworkJarData = frameworkService.getFrameworkJar(frameworkDependency);

                    if (!jarBuilder.addFile(frameworkJarPath, frameworkJarData)) {
                        LOG.error("Error while writing the framework jar dependency to the topology jar");
                    }

                    // Check each framework jar for inbuilt resources to copy to the topology resources
                    embedFrameworkResources(jarBuilder, frameworkJarData);
                }
            }

            // Write out the topology file to the topology jar with the modified changes
            String topologyJsonPath = "STREAMFLOW-INF" + File.separator + "topology.json";

            if (!jarBuilder.addFile(topologyJsonPath, objectMapper.writeValueAsBytes(topology))) {
                LOG.error("Error while writing the topology.json file to the topology jar");
            }

            // Write out the config file to the topology jar with the modified changes
            String configJsonPath = "STREAMFLOW-INF" + File.separator + "config.json";
            
            streamflowConfig.setSelectedCluster(cluster);

            if (!jarBuilder.addFile(configJsonPath, objectMapper.writeValueAsBytes(streamflowConfig))) {
                LOG.error("Error while writing the config.json file to the topology jar");
            }
            
            // Close the new topology jar to move it to the topology directory
            jarBuilder.close();
            
        } catch (IOException ex) {
            // If an exception occurs, delete the topology project from disk
            clearTopologyProject(projectId);
            
            LOG.error("Exception while generating the topology jar", ex);

            throw new ServiceException("Error while generating the topology jar: "
                    + ex.getMessage());
        }

        return projectId;
    }

    private boolean embedTopologyFile(JarBuilder jarBuilder, String fileId) {
        boolean success = false;
        
        // Retrieve the upload metadata from the service
        byte[] fileContent = fileService.getFileContent(fileId);

        if (fileContent != null) {
            String targetFilePath1 = "files" + File.separator + fileId;

            String targetFilePath2 = "resources" + File.separator + "files" + File.separator + fileId;

            // Copy the file upload to the files directory of the jar
            if (jarBuilder.addFile(targetFilePath1, fileContent)) {
                // Also copy the file to a special /resources folder which Storm will automatically explode to disk
                if (jarBuilder.addFile(targetFilePath2, fileContent)) {
                    success = true;
                } else {
                    LOG.error("Error while writing the file upload to the topology jar: " + targetFilePath2);
                }
            } else {
                LOG.error("Error while writing the file upload to the topology jar: " + targetFilePath1);
            }
        } else {
            LOG.error("File was not found for the topology: ID = " + fileId);
        }

        return success;
    }

    private void embedFrameworkResources(JarBuilder jarBuilder, byte[] frameworkJar) {
        JarEntry jarEntry;
        JarInputStream jarInputStream;

        try {
            jarInputStream = new JarInputStream(new ByteArrayInputStream(frameworkJar));

            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                // A resource file is defined as a non *.class file that is a real file
                if (!jarEntry.isDirectory() && !jarEntry.getName().endsWith(".class")) {
                    // Ignore the resource files in the META-INF folder
                    if (!jarEntry.getName().startsWith("META-INF") 
                            && !jarEntry.getName().startsWith("STREAMFLOW-INF")) {
                        // Create the handle to the target resource file in the topology jar
                        if (!jarBuilder.addFile(jarEntry.getName(), IOUtils.toByteArray(jarInputStream))) {
                            LOG.error("Error occurred while writing a framework resource to the topology jar: "
                                    + jarEntry.getName());
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("Exception while embedding framework resource: " + ex.getMessage());
        }
    }

    private void clearTopologyProject(String projectId) {
        if (projectId != null) {
            try {
                // Delete the project folder from the server
                FileUtils.forceDelete(new File(StreamflowEnvironment.getTopologiesDir(), 
                        projectId + ".jar"));
            } catch (IOException ex) {
                //LOG.error("Exception while clearing the topology project: ", ex);
            }
        }
    }
    
    private byte[] loadTopologyTemplate() {
        byte[] engineJarData = null;
        
        try {
            InputStream engineStream = null;

            try {
                // Copy the inbuilt streamflow-engine.jar template to the streamflow lib directory
                engineStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("STREAMFLOW-INF/lib/streamflow-engine.jar");

                engineJarData = IOUtils.toByteArray(engineStream);
            } finally {
                if (engineStream != null) {
                    engineStream.close();
                }
            }
        } catch (IOException ex) {
            LOG.error("Exception occurred while loading the streamflow-engine jar: ", ex);
        }
        
        return engineJarData;
    }
}
