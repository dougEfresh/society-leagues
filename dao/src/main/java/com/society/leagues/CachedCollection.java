package com.society.leagues;

import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class CachedCollection<T extends List<? extends LeagueObject>> {
    final AtomicReference<T> entity = new AtomicReference(new ArrayList<T>());
    final MongoRepository repo;

    public CachedCollection(MongoRepository repo) {
        this.repo = repo;
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
}
