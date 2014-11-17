package streamflow.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import streamflow.util.environment.StreamflowEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(JarBuilder.class);
    
    private File targetJarFile;
    
    private File tempJarFile;
    
    private JarOutputStream jarOutputStream;
    
    public JarBuilder(File targetJarFile) {
        this.targetJarFile = targetJarFile;
    }
    
    public boolean open() {
        boolean success = false;
        
        // Before opening new jar make sure any previous opened jars were closed
        if (jarOutputStream != null) {
            close();
        }
        
        tempJarFile = new File(StreamflowEnvironment.getTempDir(), IDUtils.randomUUID() + ".jar");
        
        try {
            jarOutputStream = new JarOutputStream(new FileOutputStream(tempJarFile));
            
            // Load all existing jar content from the the target file to the temp file
            if (targetJarFile.exists() && targetJarFile.canRead()) {
                JarFile inputJarFile = new JarFile(targetJarFile);
                
                Enumeration<JarEntry> jarEntries = inputJarFile.entries();
                
                while(jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    
                    jarOutputStream.putNextEntry(jarEntry);
                    IOUtils.copy(inputJarFile.getInputStream(jarEntry), jarOutputStream);
                }
            }
            
            success = true;
        } catch (Exception ex) {
            LOG.error("An exception was thrown while opening the jar: ", ex);
        }
        
        return success;
    }
    
    public boolean addFile(String path, byte[] content) {
        boolean success = false;
        
        if (jarOutputStream != null && path != null && content != null) {
            try {
                JarEntry jarEntry = new JarEntry(path);

                jarOutputStream.putNextEntry(jarEntry);
                IOUtils.write(content, jarOutputStream);
                
                success = true;
            } catch (Exception ex) {
                //LOG.error("An exception was thrown while adding a file to the jar: Path = "
                //    + path + ", Excepion = " + ex.getMessage());
            }
        }
        
        return success;
    }
    
    public boolean close() {
        boolean success = true;
        
        if (jarOutputStream != null) {
            try {
                jarOutputStream.close();
            } catch (IOException ex) {
                LOG.error("An exception was thrown while closing the jar stream: ", ex);
            }
            jarOutputStream = null;
        }
        
        if (targetJarFile != null && tempJarFile != null) {
            try {
                FileUtils.forceDelete(targetJarFile);
                FileUtils.moveFile(tempJarFile, targetJarFile);
                
                success = true;
            } catch (IOException ex) {
                LOG.error("An exception was thrown while moving the final jar file: ", ex);
            }
            targetJarFile = null;
            tempJarFile = null;
        }
        
        return success;
    }
}
