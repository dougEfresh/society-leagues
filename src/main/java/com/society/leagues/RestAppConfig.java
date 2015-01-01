package com.society.leagues;

import com.society.leagues.infrastructure.security.SecurityFilter;
import com.society.leagues.resource.AccountResource;
import com.society.leagues.resource.ApiResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class RestAppConfig extends ResourceConfig {

    @Autowired List<ApiResource> resources;

    @PostConstruct
    public void init() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, false);
        property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        property(ServerProperties.MONITORING_STATISTICS_ENABLED,true);
        register(LoggingFilter.class);
        register(JacksonFeature.class);
        resources.forEach(this::register);
        register(SecurityFilter.class);
        //packages("com.society.leagues.resource");
    }
}
