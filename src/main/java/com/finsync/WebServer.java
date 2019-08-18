package com.finsync;

import com.finsync.controller.FrmContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.eclipse.jetty.servlet.DefaultServlet;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class WebServer {
    //private static final Logger logger = LoggerFactory.getLogger(JerseyApplication.class);

    public WebServer() {

    }

    public void run() {
        Server server = new Server(8080);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        //server.setHandler(servletContextHandler);



        //for dynamic content
        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "com.finsync.jersey"
        );

        /*
        //the default one
        // add special pathspec of "/home/" content mapped to the homePath
        ServletHolder holderHome = new ServletHolder("static", DefaultServlet.class);
        holderHome.setInitParameter("dirAllowed","true");
        holderHome.setInitParameter("pathInfoOnly","true");
        servletContextHandler.addServlet(holderHome,"/static/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed","true");
        servletContextHandler.addServlet(holderPwd,"/");
        */



        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        //resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setResourceBase(WebServer.class.getClassLoader().getResource(".").toString());


        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, servletContextHandler, new DefaultHandler() });
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            //logger.error("Error occurred while starting Jetty", ex);
            System.exit(1);
        }

        finally {
            server.destroy();
        }
    }

    public static void main(String[] args) {
        new WebServer().run();

    }
}
