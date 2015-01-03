package com.society.leagues.conf;

import com.society.leagues.infrastructure.security.SecurityFilter;
import com.society.leagues.resource.ApiResource;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.message.filtering.SecurityAnnotations;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
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

        //property(EntityFilteringFeature.ENTITY_FILTERING_SCOPE,
        //          new Annotation[] {SecurityAnnotations.rolesAllowed("manager")});
        //
        register(RolesAllowedDynamicFeature.class);
        register(LoggingFilter.class);
        register(JacksonFeature.class);
//        register(SecurityEntityFilteringFeature.class);

        resources.forEach(this::register);
        register(SecurityFilter.class);
    }
}
