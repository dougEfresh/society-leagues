package com.society.leagues.Service;

import com.society.leagues.CachedCollection;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.listener.DaoListener;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

@Component
public class LeagueService {

    final static Logger logger = Logger.getLogger(LeagueService.class);
    @Value("${use-cache:true}") boolean useCache;

    @Autowired ChallengeRepository challengeRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired PlayerResultRepository playerResultRepository;

    @Autowired @Qualifier("challengeCachedCollection") CachedCollection<List<Challenge>> challengeCachedCollection;
    @Autowired @Qualifier("userCachedCollection") CachedCollection<List<User>> userCachedCollection;
    @Autowired @Qualifier("slotCachedCollection") CachedCollection<List<Slot>> slotCachedCollection;
    @Autowired @Qualifier("teamMatchCachedCollection") CachedCollection<List<TeamMatch>> teamMatchCachedCollection;
    @Autowired @Qualifier("teamCachedCollection")  CachedCollection<List<Team>> teamCachedCollection;
    @Autowired @Qualifier("seasonCachedCollection") CachedCollection<List<Season>> seasonCachedCollection;
    @Autowired @Qualifier("playerResultCachedCollection") CachedCollection<List<PlayerResult>> playerResultCachedCollection;
    @Autowired(required = false) List<DaoListener> daoListeners = new ArrayList<>();
    @Value("${convert:false}") boolean contert = false;
    Validator validator;

    @PostConstruct
    @SuppressWarnings("unused")
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        logger.info("Refreshing all cache");
        //if (!contert)
            //refreshAllCache();
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> T save(final T entity) {
        MongoRepository repo = null; //getRepo(entity);
        if (repo == null) {
            return  null;
        }
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (!constraintViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                sb.append(constraintViolation.toString());
            }
            throw new RuntimeException("Could not validate " + entity + "\n" + sb.toString());
        }
        repo.save(entity);
        T newEntity = (T) repo.findOne(entity.getId());
        CachedCollection c = getCache(entity);
        if (c != null) {
            T cached = (T) c.get().stream().filter(u -> ((LeagueObject) u).getId().equals(newEntity.getId())).findFirst().orElse(null);
            if (cached == null) {
                c.get().add(newEntity);
            } else {
                cached.merge(newEntity);
            }
        }
        for (DaoListener daoListener : daoListeners) {
            daoListener.onChange(newEntity);
        }
        return newEntity;
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> Boolean delete(T entity) {
        MongoRepository repo = null; //getRepo(entity);
        assert repo != null;
        repo.delete(entity);
        refreshAllCache();
        for (DaoListener daoListener : daoListeners) {
            daoListener.onChange(entity);
        }
        return Boolean.TRUE;
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> T findOne(T entity) {
        CachedCollection<List<T>> repo = getCache(entity);
        if (repo == null) {
            return null;
        }
        return  repo.get().stream().filter(e -> e.getId().equals(entity.getId())).findFirst().orElse(null);
    }

    public User findByLogin(String login) {
        return userCachedCollection.get().stream().parallel().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);
    }
    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> List<T> findAll(Class<T> clz) {
        try {
            CachedCollection<List<T>> cache = getCache(clz.newInstance());
            if (cache == null) {
                return null;
            }
            return cache.get();
        } catch (InstantiationException e) {
            logger.error(e.getMessage(),e);
            return Collections.emptyList();
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    public List<PlayerResult> findPlayerResultBySeason(Season s) {
        return playerResultCachedCollection.get().stream()
                .filter(pr -> pr.getSeason().equals(s))
                .filter(pr -> !pr.getLoser().isFake())
                .filter(pr -> !pr.getWinner().isFake())
                .collect(Collectors.toList());
    }

    public List<PlayerResult> findPlayerResultByUser(User u) {
        return playerResultCachedCollection.get().stream().filter(pr -> pr.hasUser(u)).collect(Collectors.toList());
    }

    private CachedCollection getCache(LeagueObject entity) {
        Class clz = entity.getClass();

        if (clz.getCanonicalName().endsWith("Challenge")) {
            return challengeCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("User")) {
            return userCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("Slot")) {
            return slotCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("Team")) {
            return teamCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("TeamMatch")) {
            return teamMatchCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("Season")) {
            return seasonCachedCollection;
        }
        if (clz.getCanonicalName().endsWith("PlayerResult")) {
            return playerResultCachedCollection;
        }

        return null;
    }


    public  <T extends LeagueObject> void deleteAll(Class<T> clz){
        try {
            CachedCollection<List<T>> cache = getCache(clz.newInstance());
            if (cache == null) {
                return ;
            }
            cache.set(new ArrayList<T>());
            //MongoRepository repo = getRepo(clz.newInstance());
            //assert repo != null;
            //repo.deleteAll();
        } catch (InstantiationException | IllegalAccessException ignore) {

        }
    }

    @Scheduled(fixedRate = 1000*60*5, initialDelay = 1000*60*10)
    public void refreshAllCache() {
        logger.info("Refreshing season");
        seasonCachedCollection.set(seasonRepository.findAll());
        logger.info("Refreshing user");
        userCachedCollection.set(userRepository.findAll());
        logger.info("Refreshing team");
        teamCachedCollection.set(teamRepository.findAll());
        logger.info("Refreshing teamMatch");
        teamMatchCachedCollection.set(teamMatchRepository.findAll());
        logger.info("Refreshing playerResult");
        playerResultCachedCollection.set(playerResultRepository.findAll());
        logger.info("Refreshing Slot");
        slotCachedCollection.set(slotRepository.findAll());
        logger.info("Refreshing challenge");
        challengeCachedCollection.set(challengeRepository.findAll());
    }

}
