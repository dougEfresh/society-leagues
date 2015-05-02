package com.society.leagues.conf;

import com.society.leagues.WebListCache;
import com.society.leagues.WebMapCache;
import com.society.leagues.client.api.domain.UserStats;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public WebMapCache<Map<Integer,UserStats>> statsCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

    @Bean
    public WebListCache<List<Map<String,Object>>> statsTeamCache() {
        return new WebListCache<>(Collections.emptyList());
    }

    @Bean
    public WebMapCache<Map<String,Object>> dataCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

}
