package com.society.leagues;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

@Configuration
@ComponentScan("com.society.leagues")
public class Main implements CommandLineRunner {

    @Autowired RestAppConfig app;
    @Value("${daemon:false}")
    boolean daemon;
    @Value("${server.port:8080}")
    int port;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Main.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Society League REST service");
        final HttpServer server = startServer();
        System.out.println("Application started.\n" +
                "Try accessing " + getBaseURI() + " in the browser.\n" +
                "Hit crt-c to stop the application...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        server.shutdown();
                    }
                }
            }
        });
        thread.setDaemon(daemon);
        thread.run();
    }

    public HttpServer startServer() throws IOException {
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                getBaseURI(),
                app);
        server.start();
        return server;
    }

    public URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(port).build();
    }
}
