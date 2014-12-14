package com.society.leagues;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.society.leagues.api")
public class SwaggerConfig {
    @Autowired
    SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){
      return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            .apiInfo(apiInfo())
            .includePatterns(".*/api/.*");
   }

    private ApiInfo apiInfo() {
      ApiInfo apiInfo = new ApiInfo(
              "Society Leaug API",
              "Automation of League Software",
              "Fill out Terms of Service",
              "api@society.com",
              "GUN?",
              "Lic"
        );
      return apiInfo;
    }
}
