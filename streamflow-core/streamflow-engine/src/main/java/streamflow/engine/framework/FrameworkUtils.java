package streamflow.engine.framework;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import streamflow.util.environment.StreamflowEnvironment;

public class FrameworkUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkUtils.class);
    
    private static final String DEFAULT_CLASS_LOADER_POLICY = "FRAMEWORK_FIRST";

    private static FrameworkUtils singleton;

    private LoadingCache<String, ClassLoader> frameworkFirstCache;

    private LoadingCache<String, ClassLoader> frameworkLastCache;

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
        throw new CloneNotSupportedException();
    }
    
    private void initialize() {
        frameworkFirstCache = CacheBuilder.newBuilder().build(new FrameworkFirstCacheLoader());
        frameworkLastCache = CacheBuilder.newBuilder().build(new FrameworkLastCacheLoader());
        
        // Initialize the frameworks directory where temporary frameworks will be stored
        File frameworksDir = new File(StreamflowEnvironment.getFrameworksDir());
        if (!frameworksDir.exists()) {
            frameworksDir.mkdirs();
        }
    }

    public Class loadFrameworkClass(String frameworkHash, String frameworkClass) 
            throws FrameworkException {
        // Default policy is to load frameworks first
        return loadFrameworkClass(frameworkHash, frameworkClass, DEFAULT_CLASS_LOADER_POLICY);
    }
    
    public Class loadFrameworkClass(String frameworkHash, String frameworkClass, String classLoaderPolicy) 
            throws FrameworkException {
        Class frameworkClazz = null;

        try {
            // Load the framework realm to isolate the class loading for the framework
            ClassLoader frameworkClassLoader = getFrameworkClassLoader(frameworkHash, classLoaderPolicy);
            
            // Load the module using the specified module class
            frameworkClazz = frameworkClassLoader.loadClass(frameworkClass);
        } catch (ClassNotFoundException ex) {
            throw new FrameworkException(
                    "Framework class was not found in the framework jar: "
                    + frameworkClass);
        }

        return frameworkClazz;
    }
    
    public <T> T loadFrameworkClassInstance(String frameworkHash, String frameworkClass, 
            Class<T> frameworkClassType) throws FrameworkException {
        // Defaut policy is to load frameworks first
        return loadFrameworkClassInstance(
                frameworkHash, frameworkClass, frameworkClassType, DEFAULT_CLASS_LOADER_POLICY);
    }

    public <T> T loadFrameworkClassInstance(String frameworkHash, String frameworkClass, 
            Class<T> frameworkClassType, String classLoaderPolicy) throws FrameworkException {
        try {
            // Load the framework class from the framework with specified coordinates
            Class frameworkClazz = loadFrameworkClass(frameworkHash, frameworkClass, classLoaderPolicy);

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
            LOG.error("Component loading failed due to an unexpected exception: ", ex);
            
            throw new FrameworkException(
                    "Component loading failed due to an unexpected exception: " + ex.getMessage());
        }
    }
    
    public ClassLoader getFrameworkClassLoader(String frameworkHash)
            throws FrameworkException {
        return getFrameworkClassLoader(frameworkHash, DEFAULT_CLASS_LOADER_POLICY);
    }

    public ClassLoader getFrameworkClassLoader(String frameworkHash, String classLoaderPolicy)
            throws FrameworkException {
        try {
            if (classLoaderPolicy.equalsIgnoreCase("FRAMEWORK_LAST")) {
                return frameworkLastCache.get(frameworkHash);
            } else {
                // Default class loader policy is framework first
                return frameworkFirstCache.get(frameworkHash);
            }
        } catch (ExecutionException ex) {
            throw new FrameworkException("Framework class loader execution exception: " 
                    + ex.getMessage());
        }
    }

    public URL getFrameworkJarUrl(String frameworkHash) {
        File frameworkJar = new File(StreamflowEnvironment.getFrameworksDir(), 
                    frameworkHash + ".jar");
            
        // Check if the framework jar has already been added
        if (!frameworkJar.exists()) {
            // URL to the framework jar embedded within the topology jar
            //URL embeddedFrameworkUrl = Thread.currentThread().getContextClassLoader()
            URL embeddedFrameworkUrl = this.getClass().getClassLoader()
                    .getResource("STREAMFLOW-INF/lib/" + frameworkHash + ".jar");

            try {
                // Copy the framework jar out of the topology jar and into the temp directory
                FileUtils.writeByteArrayToFile(frameworkJar, 
                        IOUtils.toByteArray(embeddedFrameworkUrl));
            } catch (Exception ex) {
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
    
    private class FrameworkFirstCacheLoader extends CacheLoader<String, ClassLoader> {
        @Override
        public ClassLoader load(String frameworkHash) throws FrameworkException {
            URL frameworkUrl = getFrameworkJarUrl(frameworkHash);

            if (frameworkUrl != null) {
                // Add the framework jar URL to the list to be loaded by the classloader
                URL[] frameworkUrls = new URL[]{frameworkUrl};
                
                LOG.info("Framework First Class Loader initialized by cache: Framework Hash = " 
                        + frameworkHash);
                
                return new FrameworkFirstClassLoader(
                        frameworkUrls, Thread.currentThread().getContextClassLoader());
            } else {
                throw new FrameworkException("Unable to load the framework jar, file was not available: " 
                        + frameworkUrl);
            }
        }
    }
    
    private class FrameworkLastCacheLoader extends CacheLoader<String, ClassLoader> {
        @Override
        public ClassLoader load(String frameworkHash) throws FrameworkException {
            URL frameworkUrl = getFrameworkJarUrl(frameworkHash);

            if (frameworkUrl != null) {
                // Add the framework jar URL to the list to be loaded by the classloader
                URL[] frameworkUrls = new URL[]{frameworkUrl};
                
                LOG.info("Framework Last Class Loader initialized by cache: Framework Hash = " 
                        + frameworkHash);
                
                return new URLClassLoader(
                        frameworkUrls, Thread.currentThread().getContextClassLoader());
            } else {
                throw new FrameworkException("Unable to load the framework jar, file was not available: " 
                        + frameworkUrl);
            }
        }
    }
}
