package streamflow.server;

import com.google.inject.servlet.GuiceFilter;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import streamflow.model.config.StreamflowConfig;
import streamflow.server.config.WebConfig;
import streamflow.util.config.ConfigLoader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamflowServer {
    
    public static Logger LOG = LoggerFactory.getLogger(StreamflowServer.class);

    public static void main(String[] args) throws Exception {
        
        long startTime = System.currentTimeMillis();
        
        StreamflowConfig streamflowConfig = ConfigLoader.getConfig();
        
        LOG.info("Streamflow Server: Binding server to port " + streamflowConfig.getServer().getPort());
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addEventListener(new WebConfig());
        context.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.setWelcomeFiles(new String[]{ "index.htm", "index.html" });
        context.setBaseResource(Resource.newClassPathResource("/assets"));
        context.addServlet(DefaultServlet.class, "/*");
        
        Server server = new Server(streamflowConfig.getServer().getPort());
        server.setHandler(context);
        server.start();
        
        double startupTime = (System.currentTimeMillis() - startTime) / 1000.0;
        
        LOG.info("Streamflow Server: Server started in " + startupTime + " seconds");
        
        server.join();
    }
}
