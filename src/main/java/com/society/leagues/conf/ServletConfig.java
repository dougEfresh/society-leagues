package com.society.leagues.conf;

import com.society.leagues.controller.AccountController;
import com.society.leagues.controller.ApiController;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

import javax.annotation.PostConstruct;

@Component
public class ServletConfig extends ResourceConfig {

    @PostConstruct
    public void init() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, false);
        property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        register(LoggingFilter.class);
        register(JacksonFeature.class);
        packages("com.society.leagues.controller");
    }

}
