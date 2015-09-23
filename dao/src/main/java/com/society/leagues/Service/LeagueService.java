package com.society.leagues.Service;

import com.society.leagues.cache.CachedCollection;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.listener.DaoListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import javax.validation.Validator;

@Component
public class LeagueService {

    final static Logger logger = Logger.getLogger(LeagueService.class);
    @Autowired(required = false) List<DaoListener> daoListeners = new ArrayList<>();
    @Autowired CacheUtil cacheUtil;
    Validator validator;
    @Autowired List<MongoRepository> mongoRepositories;

    @PostConstruct
    @SuppressWarnings("unused")
    public void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        cacheUtil.initialize(mongoRepositories);
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> T save(final T entity) {
        MongoRepository repo = cacheUtil.getCache(entity).getRepo();
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
        CachedCollection c = cacheUtil.getCache(entity);
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
        cacheUtil.getCache(entity).getRepo().delete(entity);
        //cacheUtil.refreshAllCache();
        for (DaoListener daoListener : daoListeners) {
            daoListener.onChange(entity);
        }
        return Boolean.TRUE;
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> T findOne(T entity) {
        CachedCollection<List<LeagueObject>> repo = cacheUtil.getCache(entity);
        if (repo == null) {
            return null;
        }
        return (T) repo.get().stream().filter(e -> e.getId().equals(entity.getId())).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public User findByLogin(String login) {
        if (login == null) {
            return null;
        }
        return (User) cacheUtil.getCache(new User()).get().parallelStream().filter(u -> login.equals(((User) u).getLogin())).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> List<T> findAll(Class<T> clz) {
        try {
            CachedCollection<List<LeagueObject>> cache = cacheUtil.getCache(clz.newInstance());
            if (cache == null) {
                return null;
            }
            return (List<T>) cache.get();
        } catch (InstantiationException e) {
            logger.error(e.getMessage(),e);
            return Collections.emptyList();
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends LeagueObject> Set<T> findCurrent(Class<T> clz) {
        return cacheUtil.getCache(clz).current();
    }

    @SuppressWarnings("unchecked")
    public  <T extends LeagueObject> void deleteAll(Class<T> clz){
        try {
            CachedCollection<List<LeagueObject>> cache = cacheUtil.getCache(clz.newInstance());
            if (cache == null) {
                return ;
            }
            //TODO cascade delete;
            cache.set(new ArrayList<>());
            cache.getRepo().deleteAll();
            //cacheUtil.refreshAllCache();
        } catch (InstantiationException | IllegalAccessException ignore) {

        }
    }
}
