package streamflow.engine.framework;

import java.net.URL;
import java.net.URLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkFirstClassLoader extends ClassLoader {
    
    private Logger LOG = LoggerFactory.getLogger(FrameworkFirstClassLoader.class);

    private FrameworkClassLoader frameworkClassLoader;

    public FrameworkFirstClassLoader(URL[] frameworkUrls) {
        this(frameworkUrls, Thread.currentThread().getContextClassLoader());
    }

    public FrameworkFirstClassLoader(URL[] frameworkUrls, ClassLoader parent) {
        super(parent);

        frameworkClassLoader = new FrameworkClassLoader(frameworkUrls, this.getParent());
    }
    
    public void includeURL(URL frameworkUrl) {
        frameworkClassLoader.includeURL(frameworkUrl);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return frameworkClassLoader.findClass(name);
        } catch (ClassNotFoundException ex) {
            return super.loadClass(name, resolve);
        }
    }

    @Override
    public URL getResource(String name) {
        URL resource = frameworkClassLoader.getResource(name);
        if (resource == null) {
            resource = super.getResource(name);
        }
        return resource;
    }

    private class FrameworkClassLoader extends URLClassLoader {

        private final ClassLoader parentClassLoader;

        public FrameworkClassLoader(URL[] frameworkUrls, ClassLoader parentClassLoader) {
            super(frameworkUrls, null);

            this.parentClassLoader = parentClassLoader;
        }
        
        public void includeURL(URL frameworkUrl) {
            this.addURL(frameworkUrl);
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                // Attempt to reuse any classes which have already been loaded
                Class<?> loadedClass = super.findLoadedClass(name);
                if (loadedClass != null) {
                    return loadedClass;
                }
                
                return super.findClass(name);
            } catch (ClassNotFoundException e) {
                return parentClassLoader.loadClass(name);
            }
        }
    }
}