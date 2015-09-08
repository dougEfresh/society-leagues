package com.society.leagues;

import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class CachedCollection<T extends List<? extends LeagueObject>> {
    final AtomicReference<T> entity = new AtomicReference(new ArrayList<T>());
    final MongoRepository repo;
    final String type;
    final ReentrantLock lock = new ReentrantLock();
    public CachedCollection(String type, MongoRepository repo) {
        this.repo = repo; this.type = type;
    }

    public T get() {
        return entity.get();
    }

    public void set(T collection) {
        this.entity.getAndSet(collection);
    }

    @Scheduled(fixedRate = 1000*60*5, initialDelay = 1000*60*15)
    public void update() {
        try {
            if (lock.tryLock())
                this.entity.lazySet((T) repo.findAll());
        } finally {
            lock.unlock();
        }

    }

    public String getType() {
        return type;
    }
}
