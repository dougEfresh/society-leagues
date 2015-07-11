package com.society.leagues.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

@Configuration
@SuppressWarnings("unused")
public class JacksonConfig {
    @Value("${pretty-print:false}") boolean prettyPrint = false;
    @Autowired DateTimeSerializer dateTimeSerializer;

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        return builder.indentOutput(prettyPrint).
                dateFormat(new SimpleDateFormat("yyyy-MM-dd")).
                serializers(dateTimeSerializer).
                defaultViewInclusion(true);
    }

}
