package com.society.leagues.cache;

import com.society.leagues.client.api.domain.LeagueObject;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class CachedCollection<T extends List<? extends LeagueObject>> {
    final AtomicReference<T> entity = new AtomicReference(new ArrayList<T>());
    private static Logger logger = Logger.getLogger(CachedCollection.class);
    MongoRepository repo;
    String collectionName;

    public CachedCollection() {

    }

    public T get() {
        return entity.get();
    }

    public void set(T collection) {
        this.entity.lazySet(collection);
    }

    public MongoRepository getRepo() {
        return repo;
    }

    public String getCollection() {
        return collectionName;
    }

    public void setRepo(MongoRepository repo) {
        this.repo = repo;
        String name = repo.getClass().getInterfaces()[0].getSimpleName().replace("Repository","");
        this.collectionName = name.substring(0,1).toLowerCase() + name.substring(1,name.length());
    }

    @SuppressWarnings("unchecked")
    public void refresh() {
        if (repo == null)
            return;

        logger.info("Refreshing " + collectionName);
        this.set((T) this.repo.findAll());
        logger.info("Done " + collectionName);
    }
}
