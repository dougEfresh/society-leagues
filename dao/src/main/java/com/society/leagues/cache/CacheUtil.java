package com.society.leagues.cache;

import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class CacheUtil {

    @Autowired List<CachedCollection> cachedCollections;
    @Value("${convert:false}") boolean convert = false;

    public <T extends LeagueObject> T findOne(String id,String collection) {
        for (CachedCollection cachedCollection : cachedCollections) {
            if (cachedCollection.getCollection().equals(collection)) {
                return (T) cachedCollection.get().parallelStream().filter(c->((LeagueObject) c).getId().equals(id)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public Object addCache(Object entity) {
        if (entity instanceof LeagueObject) {
            return getCache((LeagueObject) entity);
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

    public CachedCollection getCache(LeagueObject entity) {
        Class clz = entity.getClass();
        for (CachedCollection cachedCollection : cachedCollections) {
            if (cachedCollection.getRepo().getClass().getInterfaces()[0].getSimpleName().contains(clz.getSimpleName() + "Repository")) {
                return cachedCollection;
            }
        }
        throw new RuntimeException("Cannot find cache repo for " + clz.getCanonicalName());
    }

    @Scheduled(fixedRate = 1000*60*5, initialDelay = 1000*60*10)
    public void refreshAllCache() {
        cachedCollections.forEach(CachedCollection::refresh);
    }

}
