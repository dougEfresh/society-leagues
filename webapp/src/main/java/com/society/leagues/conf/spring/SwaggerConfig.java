package com.society.leagues.conf.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.security.Principal;

@Configuration
@SuppressWarnings("unused")
public class SwaggerConfig {

    @Autowired SpringSwaggerConfig springSwaggerConfig;
    @Autowired ApplicationContext context;
    @Autowired RequestMappingHandlerAdapter[] requestMappingHandlerAdapters;
    @Autowired Jackson2ObjectMapperBuilder jacksonBuilder;
    @Autowired ObjectMapper objectMapper;

    @Bean
    public SwaggerSpringMvcPlugin publicApi() {

    //    springSwaggerConfig.jacksonSwaggerSupport().setApplicationContext();
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion("1")
                .includePatterns("/api/.*")
                .ignoredParameterTypes(Principal.class);
    }

    ApiInfo apiInfo() {
        return new ApiInfo("Billiard Mang. API","Billiards",
                "", "changeme@example.com", "All Rights Reserved",
                ""
        );
    }
}