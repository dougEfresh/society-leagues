package com.society.leagues.conf;

import com.society.leagues.cache.CachedCollection;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


@Configuration
@SuppressWarnings(value = {"unused","unchecked"})
public class CacheConfig {
    private static Logger logger = Logger.getLogger(LeagueService.class);

    @Bean
    public CachedCollection<List<Team>> teamCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<TeamMatch>> teamMatchCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<User>> userCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<Season>> seasonCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<Challenge>> challengeCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<Slot>> slotCachedCollection() {
        return new CachedCollection<>();
    }

    @Bean
    public CachedCollection<List<PlayerResult>> playerResultCachedCollection() {
        return new CachedCollection<>();
    }

}
