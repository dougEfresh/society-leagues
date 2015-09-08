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
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@SuppressWarnings("unused")
public class CacheConfig {
    @Autowired ChallengeRepository challengeRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired PlayerResultRepository playerResultRepository;
    private static Logger logger = Logger.getLogger(LeagueService.class);

    @Bean
    public CachedCollection<List<Team>> teamCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("team",teamRepository);
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<TeamMatch>> teamMatchCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("teamMatch",teamMatchRepository);
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<User>> userCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("user",userRepository);
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<Season>> seasonCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("season",seasonRepository);
        return cachedCollection;
    }

    @Bean(name = "challengeCachedCollection")
    public CachedCollection<List<Challenge>> challengeCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("challenge",challengeRepository);
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<Slot>> slotCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("slot",slotRepository);
        return cachedCollection;
    }

    @Bean
    public CachedCollection<List<PlayerResult>> playerResultCachedCollection() {
        CachedCollection cachedCollection = new CachedCollection("playerResult",playerResultRepository);
        return cachedCollection;
    }
}
