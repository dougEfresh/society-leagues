package com.society.leagues.client;

import com.society.leagues.client.filter.ExceptionFilter;
import com.society.leagues.client.filter.TokenFilter;
import feign.Feign;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.restz.client.RestProxyFactory;
import org.restz.client.filter.DeprecatedResponseHandler;
import org.restz.client.filter.MethodCallFilter;
import org.restz.client.filter.VersionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import feign.Logger;

@Component
public class ApiFactory {
    @Autowired SpringEncoder feignEncoder;
    @Autowired SpringDecoder feignDecoder;
    @Autowired Logger feignLogger;
    @Value("${debug:false}")
    boolean debug;

    public <T> T getApi(Class<T> api, String url) {
      return getApi(api,url,debug);
    }

    public <T> T getApi(Class<T> api, String url, boolean debug) {
        if (debug) {
            return Feign.builder().
                    decoder(feignDecoder).
                    encoder(feignEncoder).
                    logger(feignLogger).logLevel(Logger.Level.HEADERS).
                    contract(new SpringMvcContract()).
                    target(api, url);
        }

        return Feign.builder().
                    decoder(feignDecoder).
                    encoder(feignEncoder).
                    logger(feignLogger).logLevel(Logger.Level.BASIC).
                    contract(new SpringMvcContract()).
                    target(api, url);

    }

    public static <T> T createApi(Class<T> api, String url) {
        return createApi(api,null,url,false);
    }

    public static <T> T createApi(Class<T> api, String token, String url) {
        return createApi(api,token,url,false);
    }

    public static <T> T createApi(Class<T> api, String token, String url, boolean debug) {

        ClientConfig config = new ClientConfig().
                register(DeprecatedResponseHandler.class).
                register(MethodCallFilter.class).
                register(new VersionFilter(api)).
                register(new TokenFilter(token)).
                register(ExceptionFilter.class);

        if (debug) {
            java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LoggingFilter.class.getName());
            LoggingFilter loggingFilter  = new LoggingFilter(LOGGER,true);
            config = config.register(loggingFilter);
        }

        Client client = ClientBuilder.newClient(config);
        return RestProxyFactory.getRestClientApi(api,url,client);
    }
}
