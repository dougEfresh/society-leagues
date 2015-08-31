package com.society.leagues.Service;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeagueService {

    @Autowired ChallengeRepository challengeRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired HandicapSeasonRepository handicapSeasonRepository;
    @Autowired TeamRepository teamRepository;

    public <T extends LeagueObject> T save(T entity) {
        MongoRepository repo = getRepo(entity);
        repo.save(entity);
        return (T) repo.findOne(entity.getId());
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

        return null;
    }

}
