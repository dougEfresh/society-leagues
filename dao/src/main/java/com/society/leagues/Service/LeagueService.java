package com.society.leagues.Service;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
    Validator validator;

    @PostConstruct
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public <T extends LeagueObject> T save(T entity) {
        MongoRepository repo = getRepo(entity);
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            throw  new RuntimeException("Could not validate " + entity);
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

     public List<TeamMatch> findTeamMatchByTeam(Team team) {
         List<TeamMatch> matches = teamMatchRepository.findByHome(team);
         matches.addAll(teamMatchRepository.findByAway(team));
         return matches;
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
        //TODO Optimize
        List<PlayerResult> results = new ArrayList<>();
        results.addAll(playerResultRepository.findByPlayerHome(u));
        results.addAll(playerResultRepository.findByPlayerAway(u));
        return results;
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
