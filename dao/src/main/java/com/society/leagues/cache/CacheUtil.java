package com.society.leagues.cache;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.listener.DaoListener;
import com.society.leagues.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unused")
public class CacheUtil {

    List<CachedCollection> cachedCollections = new ArrayList<>();

    public void initialize(List<MongoRepository> mongoRepositories) {

        for (MongoRepository mongoRepository : mongoRepositories.stream()
                .filter(r->!r.getClass().getName().contains("UserSocial"))
                .collect(Collectors.toList())) {
            cachedCollections.add(new CachedCollection(mongoRepository));
        }
        Collections.sort(cachedCollections);
        refreshAllCache();
    }

    public LeagueObject findOne(String id,String collection) {
        for (CachedCollection cachedCollection : cachedCollections) {
            if (cachedCollection.getCollection().equals(collection)) {
                return (LeagueObject) cachedCollection.get().parallelStream().filter(c->((LeagueObject) c).getId().equals(id)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public Object addCache(Object entity) {
        if (entity instanceof LeagueObject) {
            getCache((LeagueObject) entity).add((LeagueObject) entity);
            return entity;
        }
        return null;
    }

    public <T extends LeagueObject> CachedCollection getCache(Class<T> clz) {
        try {
            return getCache(clz.newInstance());
        } catch (InstantiationException | IllegalAccessException ignore) {
            return null;
        }
    }

    public CachedCollection<List<LeagueObject>> getCache(LeagueObject entity) {
        Class clz = entity.getClass();
        for (CachedCollection cachedCollection : cachedCollections) {
            if (cachedCollection.getRepo().getClass().getInterfaces()[0].getSimpleName().contains(clz.getSimpleName() + "Repository")) {
                return cachedCollection;
            }
        }
        throw new RuntimeException("Cannot find cache repo for " + clz.getCanonicalName());
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*60)
    public void refreshAllCache() {
        cachedCollections.forEach(CachedCollection::refresh);
    }

}
