package com.society.leagues.spring;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    /*
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
    */
}
