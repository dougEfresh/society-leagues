package com.society.leagues;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.TraceWebFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        GroovyTemplateAutoConfiguration.class,
        TraceWebFilterAutoConfiguration.class
}
)
//@EnableWebMvcSecurity
//@EnableConfigurationProperties
//@EnableWebMvc
@Configuration
@ComponentScan("com.society.leagues")
//@EnableSwagger
public class Main implements CommandLineRunner{

    @Autowired RestAppConfig app;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Main.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Society League REST service");

        HttpServer server = startServer();

        System.out.println("Application started.\n" +
                "Try accessing " + getBaseURI() + " in the browser.\n" +
                "Hit enter to stop the application...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        thread.setDaemon(false);
        thread.run();
    }

    /**
     * Starts the lightweight HTTP server serving the JAX-RS application.
     *
     * @return new instance of the lightweight HTTP server
     * @throws IOException
     */
    HttpServer startServer() throws IOException {
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(getBaseURI(),
                app);
                // create a new server listening at port 8080
        //HttpServer server = HttpServer.create(new InetSocketAddress(getBaseURI().getPort()), 0);

        // create a handler wrapping the JAX-RS application
        //HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(app, HttpHandler.class);

        // map JAX-RS handler to the server root
        //server.createContext(getBaseURI().getPath(), handler);

        // start the server
        server.start();

        return server;
    }

    private static int getPort(int defaultPort) {
        final String port = System.getProperty("jersey.config.test.container.port");
        if (null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.out.println("Value of jersey.config.test.container.port property" +
                        " is not a valid positive integer [" + port + "]." +
                        " Reverting to default [" + defaultPort + "].");
            }
        }
        return defaultPort;
    }

    /**
     * Gets base {@link URI}.
     *
     * @return base {@link URI}.
     */
    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(getPort(8080)).build();
    }
}
