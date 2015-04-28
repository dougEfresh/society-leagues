package com.society.leagues.conf;

import com.society.leagues.WebMapCache;
import com.society.leagues.client.api.domain.UserStats;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public WebMapCache<Map<Integer,UserStats>> statsCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

}
