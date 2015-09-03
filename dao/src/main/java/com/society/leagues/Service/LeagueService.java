package com.society.leagues.Service;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class LeagueService {

    @Autowired ChallengeRepository challengeRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired HandicapSeasonRepository handicapSeasonRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired PlayerResultRepository playerResultRepository;
    private static Logger logger = Logger.getLogger(LeagueService.class);
    int counter = 0;

    public <T extends LeagueObject> T save(T entity) {
        MongoRepository repo = getRepo(entity);
        if (entity.getId() == null && counter++ % 100 == 0) {
            //logger.info("Saving entity " + entity.toString());
        }
        repo.save(entity);
        return (T) repo.findOne(entity.getId());
    }

    public <T extends LeagueObject> Boolean delete(T entity) {
        MongoRepository repo = getRepo(entity);
        repo.delete(entity);
        return Boolean.TRUE;
    }

    public <T extends LeagueObject> T findOne(T entity) {
        MongoRepository repo = getRepo(entity);
        return (T) repo.findOne(entity.getId());
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public List<Team> findTeamBySeason(Season season) {
        return teamRepository.findBySeason(season);
    }

    public List<TeamMatch> findTeamMatchBySeason(Season season) {
        return teamMatchRepository.findBySeason(season);
    }

    public <T extends LeagueObject> List<T> findAll(Class<T> clz) {
        try {
            MongoRepository repo = getRepo(clz.newInstance());
            return repo.findAll();
        } catch (InstantiationException e) {
            return Collections.emptyList();
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<PlayerResult> findPlayerResultBySeason(Season s) {
        return playerResultRepository.findBySeason(s);
    }

    public List<PlayerResult> findPlayerResultByUser(User u) {
        return playerResultRepository.findByPlayerHomeOrPlayerAway(u);
    }

    private MongoRepository getRepo(LeagueObject entity) {
        Class clz = entity.getClass();
        if (clz.getCanonicalName().endsWith("Challenge")) {
            return challengeRepository;
        }
        if (clz.getCanonicalName().endsWith("User")) {
            return userRepository;
        }
        if (clz.getCanonicalName().endsWith("Slot")) {
            return slotRepository;
        }
        if (clz.getCanonicalName().endsWith("Team")) {
            return teamRepository;
        }
        if (clz.getCanonicalName().endsWith("TeamMatch")) {
            return teamMatchRepository;
        }
        if (clz.getCanonicalName().endsWith("HandicapSeason")) {
            return handicapSeasonRepository;
        }
        if (clz.getCanonicalName().endsWith("Season")) {
            return seasonRepository;
        }

        if (clz.getCanonicalName().endsWith("PlayerResult")) {
            return playerResultRepository;
        }

        return null;
    }
}
