package com.society.leagues;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebMvcSecurity
@EnableConfigurationProperties
@EnableWebMvc
@Configuration
@ComponentScan("com.society")
public class Application {

    public static void main(String[] args) throws Exception {
	SpringApplication.run(Application.class, args);
    }

}
