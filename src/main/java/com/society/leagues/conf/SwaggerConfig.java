package com.society.leagues.conf;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.society.leagues.domain.DomainUser;
import com.wordnik.swagger.model.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@Configuration
//@EnableWebMvc
//@EnableSwagger
public class SwaggerConfig {

    @Autowired
    SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .includePatterns(".*/api/.*")
                .apiVersion("1.0")
                .ignoredParameterTypes(DomainUser.class);
   }

    private ApiInfo apiInfo() {
      ApiInfo apiInfo = new ApiInfo(
              "Society League API",
              "Automation of League Software",
              "Fill out Terms of Service",
              "api@society.com",
              "GNU",
              "Lic"
        );
      return apiInfo;
    }
}
