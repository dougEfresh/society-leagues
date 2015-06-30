package com.society.leagues.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.feign.UserRestApi;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

@Configuration
public class FeignConfig {
    @Value("${rest-url}")
    String restUrl;

    @Autowired
    Jackson2ObjectMapperBuilder builder;

    @Bean
    UserRestApi userRestApi() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return Feign.builder()
                       .decoder(new JacksonDecoder(objectMapper))
                       .target(UserRestApi.class, restUrl);
    }
}
