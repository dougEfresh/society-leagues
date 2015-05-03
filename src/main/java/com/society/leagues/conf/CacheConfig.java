package com.society.leagues.conf;

import com.society.leagues.WebListCache;
import com.society.leagues.WebMapCache;
import com.society.leagues.adapters.PlayerResultAdapter;
import com.society.leagues.adapters.TeamStatsSeasonAdapter;
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
    public WebMapCache<Map<Integer,List<TeamStatsSeasonAdapter>>> statsTeamCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

    @Bean
    public WebMapCache<Map<Integer,Map<Integer,List<PlayerResultAdapter>>>> userResultCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

    @Bean
    public WebMapCache<Map<String,Object>> dataCache() {
        return new WebMapCache<>(Collections.emptyMap());
    }

}
