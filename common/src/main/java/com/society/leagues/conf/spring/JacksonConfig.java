package com.society.leagues.conf.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Configuration
@SuppressWarnings("unused")
public class JacksonConfig {
    @Autowired DateSerializer dateSerializer;
    @Autowired DateDeSerializer dateDeSerializer;

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        return builder.indentOutput(true).dateFormat(
                new SimpleDateFormat("yyyy-MM-dd")).defaultViewInclusion(true).
                serializers(dateSerializer).deserializerByType(LocalDateTime.class,dateDeSerializer);
    }

}
