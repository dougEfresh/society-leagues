package com.society;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.society")
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class Main  {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class,args);
    }
}

