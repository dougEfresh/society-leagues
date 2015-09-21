package com.society.leagues.conf;

import com.society.leagues.CachedCollection;
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
    @Autowired List<MongoRepository> repositories;

    @Bean
    public CachedCollection<List<Team>> teamCachedCollection() {
        return new CachedCollection<>(getRepo(TeamMatchRepository.class));
    }

    @Bean
    public CachedCollection<List<TeamMatch>> teamMatchCachedCollection() {
        return new CachedCollection<>(getRepo(TeamMatchRepository.class));
    }

    @Bean
    public CachedCollection<List<User>> userCachedCollection() {
        return new CachedCollection<>(getRepo(UserRepository.class));
    }

    @Bean
    public CachedCollection<List<Season>> seasonCachedCollection() {
        return new CachedCollection<>(getRepo(SeasonRepository.class));
    }

    @Bean(name = "challengeCachedCollection")
    public CachedCollection<List<Challenge>> challengeCachedCollection() {
        return new CachedCollection<>(getRepo(ChallengeRepository.class));
    }

    @Bean
    public CachedCollection<List<Slot>> slotCachedCollection() {
        return new CachedCollection<>(getRepo(SlotRepository.class));
    }

    @Bean
    public CachedCollection<List<PlayerResult>> playerResultCachedCollection() {
        return new CachedCollection<>(getRepo(PlayerResultRepository.class));
    }

    private MongoRepository getRepo(Class clz) {
        if (clz.getCanonicalName().endsWith("Challenge")) {
            return null;
        }
        if (clz.getCanonicalName().endsWith("User")) {
            return null;
        }
        if (clz.getCanonicalName().endsWith("Slot")) {
            return null;
        }
        if (clz.getCanonicalName().endsWith("Team")) {
            return null;
        }
        if (clz.getCanonicalName().endsWith("TeamMatch")) {
            return null;
        }

        if (clz.getCanonicalName().endsWith("Season")) {
            return null;
        }

        if (clz.getCanonicalName().endsWith("PlayerResult")) {
            return null;
        }

        return null;
    }

}
