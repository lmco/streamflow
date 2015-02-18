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
package streamflow.engine.framework;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkLoader.class);

    private static FrameworkLoader singleton;

    private final ClassWorld classWorld;

    private final HashMap<String, HashSet<String>> includedFrameworks
            = new HashMap<String, HashSet<String>>();

    private FrameworkLoader() {
        classWorld = new ClassWorld();
    }

    public static synchronized FrameworkLoader getInstance() {
        if (singleton == null) {
            singleton = new FrameworkLoader();
        }
        return singleton;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public ClassWorld getClassWorld() {
        return classWorld;
    }

    public synchronized void includeFramework(String realmId, String frameworkName)
            throws FrameworkException {

        try {
            // Load the core framework realm to include the new framework in
            ClassRealm targetRealm = loadRealm(realmId);

            loadFrameworkJar(targetRealm, frameworkName);
        } catch (Exception ex) {
            throw new FrameworkException(
                    "Framework could not be included: Target Framework = " + realmId
                    + ", Included Framework = " + frameworkName);
        }
    }

    public synchronized Class loadFrameworkClass(String frameworkName, String frameworkClass)
            throws FrameworkException {
        // Convenience method when framework name is seame as the realm ID
        return loadFrameworkClass(frameworkName, frameworkName, frameworkClass);
    }

    public synchronized Class loadFrameworkClass(String realmId, String frameworkName, String frameworkClass)
            throws FrameworkException {

        Class frameworkClazz = null;

        try {
            // Load the framework realm to isolate the class loading for the framework
            ClassRealm targetRealm = loadRealm(realmId);

            // Process the framework jar to load each of the underlying classes as constituents
            if (loadFrameworkJar(targetRealm, frameworkName)) {
                // Load the module using the specified module class
                frameworkClazz = targetRealm.loadClass(frameworkClass);
            } else {
                throw new FrameworkException(
                    "Framework jar could not be loaded: Framework = " 
                            +  frameworkName + ", Class = " + frameworkClass);
            }
        } catch (ClassNotFoundException ex) {
            throw new FrameworkException(
                    "Framework class was not found in the framework jar: "
                    + frameworkClass);
        }

        return frameworkClazz;
    }

    public synchronized <T> T loadFrameworkComponent(String frameworkName,
            String componentClass, Class<T> componentClassType) throws FrameworkException {
        // Convenience method when framework name is seame as the realm ID
        return loadFrameworkComponent(frameworkName, frameworkName, componentClass, componentClassType);
    }

    public synchronized <T> T loadFrameworkComponent(String realmId, String frameworkName,
            String componentClass, Class<T> componentClassType) throws FrameworkException {
        try {
            // Load the framework class from the framework with specified coordinates
            Class componentClazz = loadFrameworkClass(realmId, frameworkName, componentClass);

            // Check to make sure that the library loaded matches the class type
            if (componentClassType.isAssignableFrom(componentClazz)) {
                // Create a new instance of the module and return it
                return (T) componentClazz.newInstance();
            } else {
                throw new FrameworkException(
                    "Component class is not a valid Storm base component type: " + componentClass);
            }
        } catch (FrameworkException ex) {
            // Rethrow framework exceptions as is
            throw ex;
        } catch (InstantiationException ex) {
            throw new FrameworkException(
                    "Component class cound not be instantiated: " + componentClass);
        } catch (IllegalAccessException ex) {
            throw new FrameworkException(
                    "Component class was illegally accessed: " + componentClass);
        } catch (Exception ex) {
            throw new FrameworkException(
                    "Component loading failed due to an unexpected exception: " + ex.getMessage());
        }
    }

    public synchronized boolean loadFrameworkJar(ClassRealm realm, String frameworkName) {
        boolean isLoaded = false;
        
        // TODO: NEED TO ENSURE THAT THE TEMP FRAMEWORK JARS ARE CLEANED UP AFTER A TOPOLOGY IS DESTROYED

        // Ensure frameworks are only added once per realm
        if (isFrameworkIncluded(realm.getId(), frameworkName)) {
            // Framework has already been included in the realm
            isLoaded = true;
        } else {
            // Create a new folder for each topology project to hold the framework jars
            File realmFrameworkDir = new File(StreamflowEnvironment.getFrameworksDir(), realm.getId());
            if (!realmFrameworkDir.exists()) {
                realmFrameworkDir.mkdirs();
            }
            
            // Attempt to first read the framework data from the frameworks directory (server)
            File realmFrameworkJar = new File(realmFrameworkDir, frameworkName + ".jar");
            if (!realmFrameworkJar.exists()) {
                
                // URL to the framework jar embedded within the topology jar
                URL frameworkUrl = Thread.currentThread().getContextClassLoader()
                        .getResource("STREAMFLOW-INF/lib/" + frameworkName + ".jar");

                try {
                    // Copy the framework jar out of the topology jar and into the temp directory
                    FileUtils.writeByteArrayToFile(realmFrameworkJar, IOUtils.toByteArray(frameworkUrl));
                } catch (IOException ex) {
                    LOG.error("An exception occurred while copying the inbuilt framework jar to the temp directory", ex);
                }
            }

            // Make sure the framework jar data was loaded by some means
            if (realmFrameworkJar.exists()) {
                try {
                    // Make the jar file accessible to the framework realm for class loading
                    realm.addURL(realmFrameworkJar.toURI().toURL());

                    // Keep track of the framework that has just been included
                    includedFrameworks.get(realm.getId()).add(frameworkName);

                    LOG.info("Framework added to Realm: Realm = " + realm.getId() 
                            + ", Framework = " + frameworkName);

                    isLoaded = true;
                } catch (MalformedURLException ex) {
                    LOG.error("Exception occurred writing framework jar data to the target file: ", ex);
                }
            } else {
                LOG.error("Unable to load the realm framework jar: File was not available");
            }
        }

        return isLoaded;
    }

    public synchronized ClassRealm loadRealm(String realmId) throws FrameworkException {
        ClassRealm realm = null;

        try {
            // Retrieve the existing framework realm in case it already exists in the context
            realm = classWorld.getRealm(realmId);
        } catch (NoSuchRealmException nsre) {
            try {
                if (realmId.startsWith(FrameworkKryoFactory.KRYO_REALM)) {
                    // Initialize the realm using the current thread class loader
                    realm = classWorld.newRealm(realmId,
                            Thread.currentThread().getContextClassLoader());
                } else {
                    // All other realms should use the Kryo realm as a parent
                    realm = classWorld.newRealm(realmId,
                            loadRealm(FrameworkKryoFactory.KRYO_REALM + realmId));
                }
            } catch (Exception ex) {
                throw new FrameworkException("Unable to create the Framework Realm: "
                        + ex.getMessage());
            }
        }

        return realm;
    }

    public boolean isFrameworkIncluded(String realmId, String frameworkName) {
        // Realm frameworks have not been initialized so do that first
        if (includedFrameworks.get(realmId) == null) {
            includedFrameworks.put(realmId, new HashSet<String>());
        }

        // Check if the framework has already been included in the realm
        return includedFrameworks.get(realmId).contains(frameworkName);
    }
}
