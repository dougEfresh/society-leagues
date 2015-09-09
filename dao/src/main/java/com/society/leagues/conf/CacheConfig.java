package com.society.leagues.conf;

import com.society.leagues.CachedCollection;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@SuppressWarnings("unused")
public class CacheConfig {
    private static Logger logger = Logger.getLogger(LeagueService.class);

    @Bean
    public CachedCollection<List<Team>> teamCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("team");
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<TeamMatch>> teamMatchCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("teamMatch");
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<User>> userCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("user");
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<Season>> seasonCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("season");
        return cachedCollection;
    }

    @Bean(name = "challengeCachedCollection")
    public CachedCollection<List<Challenge>> challengeCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("challenge");
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<Slot>> slotCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("slot");
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<PlayerResult>> playerResultCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("playerResult");
        return cachedCollection;
    }
}
