package com.society.leagues.conf.spring;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.*;

import java.security.Principal;

@Configuration
@SuppressWarnings("unused")
public class SwaggerConfig {

    @Autowired SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin publicApi() {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion("1")
                .includePatterns("/api/.*")
                .ignoredParameterTypes(Principal.class);
    }


    ApiInfo apiInfo() {
        return new ApiInfo("Billiard Managament API","Billiards",
                "", "changeme@example.com", "All Rights Reserved",
                ""
        );
    }
}