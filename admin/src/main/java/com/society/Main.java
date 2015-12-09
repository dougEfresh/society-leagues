package com.society;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.society")
@EnableAutoConfiguration
public class Main  {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class,args);
    }
}

