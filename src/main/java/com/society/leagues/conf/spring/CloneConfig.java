package com.society.leagues.conf.spring;

import com.rits.cloning.Cloner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloneConfig {

    @Bean
    public Cloner cloner() {
        return new Cloner();
    }
}
