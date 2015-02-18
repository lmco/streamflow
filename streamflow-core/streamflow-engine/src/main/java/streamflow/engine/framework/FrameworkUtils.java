package streamflow.engine.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import streamflow.util.environment.StreamflowEnvironment;

public class FrameworkUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkUtils.class);

    private static FrameworkUtils singleton;

    // FrameworkHash -> ClassLoader
    private final HashMap<String, ClassLoader> frameworkClassLoaders
            = new HashMap<String, ClassLoader>();

    private FrameworkUtils() {
    }

    public static synchronized FrameworkUtils getInstance() {
        if (singleton == null) {
            singleton = new FrameworkUtils();
            singleton.initialize();
        }
        return singleton;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();

        throw new CloneNotSupportedException();
    }
    
    private void initialize() {
        // Initialize the frameworks directory where temporary frameworks will be stored
        File frameworksDir = new File(StreamflowEnvironment.getFrameworksDir());
        if (!frameworksDir.exists()) {
            frameworksDir.mkdirs();
        }
    }

    public synchronized Class loadFrameworkClass(
            String frameworkHash, String frameworkClass)
            throws FrameworkException {
        Class frameworkClazz = null;

        try {
            // Load the framework realm to isolate the class loading for the framework
            ClassLoader frameworkClassLoader = getFrameworkClassLoader(frameworkHash);

            // Process the framework jar to load each of the underlying classes as constituents
            if (frameworkClassLoader != null) {
                // Load the module using the specified module class
                frameworkClazz = frameworkClassLoader.loadClass(frameworkClass);
            } else {
                throw new FrameworkException(
                    "Framework jar could not be loaded: Framework Hash = " 
                            +  frameworkHash + ", Class = " + frameworkClass);
            }
        } catch (ClassNotFoundException ex) {
            throw new FrameworkException(
                    "Framework class was not found in the framework jar: "
                    + frameworkClass);
        }

        return frameworkClazz;
    }

    public synchronized <T> T loadFrameworkClassInstance(String frameworkHash,
            String frameworkClass, Class<T> frameworkClassType) throws FrameworkException {
        try {
            // Load the framework class from the framework with specified coordinates
            Class frameworkClazz = loadFrameworkClass(frameworkHash, frameworkClass);

            // Check to make sure that the library loaded matches the class type
            if (frameworkClassType.isAssignableFrom(frameworkClazz)) {
                // Create a new instance of the module and return it
                return (T) frameworkClazz.newInstance();
            } else {
                throw new FrameworkException(
                    "The framework class could not be assigned to the specified class type: " 
                            + frameworkClass);
            }
        } catch (FrameworkException ex) {
            // Rethrow framework exceptions as is
            throw ex;
        } catch (InstantiationException ex) {
            throw new FrameworkException(
                    "Component class cound not be instantiated: " + frameworkClass);
        } catch (IllegalAccessException ex) {
            throw new FrameworkException(
                    "Component class was illegally accessed: " + frameworkClass);
        } catch (Exception ex) {
            throw new FrameworkException(
                    "Component loading failed due to an unexpected exception: " + ex.getMessage());
        }
    }

    public synchronized ClassLoader getFrameworkClassLoader(String frameworkHash)
            throws FrameworkException {
        ClassLoader frameworkClassLoader = frameworkClassLoaders.get(frameworkHash);

        if (frameworkClassLoader == null) {
            URL frameworkUrl = getFrameworkJarUrl(frameworkHash);

            if (frameworkUrl != null) {
                // Add the framework jar URL to the list to be loaded by the classloader
                URL[] frameworkUrls = new URL[]{frameworkUrl};

                // Build the framework first class loader
                frameworkClassLoader = new FrameworkFirstClassLoader(frameworkUrls);

                // Save the class loader for reuse
                frameworkClassLoaders.put(frameworkHash, frameworkClassLoader);

                LOG.info("Framework Class Loader Initialized: Framework Hash = " 
                        + frameworkHash);
            } else {
                LOG.error("Unable to load the framework jar: File was not available");
            }
        }
        return frameworkClassLoader;
    }

    private URL getFrameworkJarUrl(String frameworkHash) {
        File frameworkJar = new File(StreamflowEnvironment.getFrameworksDir(), 
                    frameworkHash + ".jar");
            
        // Check if the framework jar has already been added
        if (!frameworkJar.exists()) {
            // URL to the framework jar embedded within the topology jar
            URL embeddedFrameworkUrl = Thread.currentThread().getContextClassLoader()
                    .getResource("STREAMFLOW-INF/lib/" + frameworkHash + ".jar");

            try {
                // Copy the framework jar out of the topology jar and into the temp directory
                FileUtils.writeByteArrayToFile(frameworkJar, 
                        IOUtils.toByteArray(embeddedFrameworkUrl));
            } catch (IOException ex) {
                LOG.error("An exception occurred while copying the inbuilt framework jar to the temp directory", ex);
            }
        }
        
        try {
            return frameworkJar.toURI().toURL();
        } catch (Exception ex) {
            LOG.error("Unabled to load the framework jar URL: ", ex);
            
            return null;
        }
    }
}
