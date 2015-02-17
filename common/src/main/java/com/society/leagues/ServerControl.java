package com.society.leagues;

import com.society.leagues.conf.RestAppConfig;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainerProvider;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Component
public class ServerControl {
    static Logger logger = LoggerFactory.getLogger(ServerControl.class);
    @Autowired RestAppConfig app;
    @Value("${server.port:8081}")
    int port;
    HttpServer server;
    Thread serverThread;

    public void run(String ...args) throws Exception {
        logger.info("Starting Society League REST service on port " + port);
        startServer();
        logger.info("Application started.\n" +
                "Try accessing " + getBaseURI() + " in the browser.\n" +
                "Hit crt-c to stop the application...");

    }

    public void startServer() throws Exception {
        server = GrizzlyHttpServerFactory.createHttpServer(getBaseURI());

        //Api docs
        CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(this.getClass().getClassLoader(),"/public/");
        server.getServerConfiguration().addHttpHandler(httpHandler,"/doc");

        //Api docs
        CLStaticHttpHandler demoHttpHandler = new CLStaticHttpHandler(this.getClass().getClassLoader(),"/public/demo/");
        server.getServerConfiguration().addHttpHandler(httpHandler,"/demo");

        //JAX resources
        GrizzlyHttpContainerProvider provider = new GrizzlyHttpContainerProvider();
        GrizzlyHttpContainer container = provider.createContainer(GrizzlyHttpContainer.class, app);
        server.getServerConfiguration().addHttpHandler(container,"/");

        server.start();

    }

    public URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(port).build();
    }

    public int getPort() {
        for (NetworkListener networkListener : server.getListeners()) {
            return networkListener.getPort();
        }
        return port;
    }

    public void shutdown() {
        serverThread.interrupt();
    }
}
