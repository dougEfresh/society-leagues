package com.society.leagues;

import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class CachedCollection<T extends List<? extends LeagueObject>> {
    final AtomicReference<T> entity = new AtomicReference(new ArrayList<T>());
    final String type;

    public CachedCollection(String type) {
        this.type = type;
    }

    public T get() {
        return entity.get();
    }

    public void set(T collection) {
        this.entity.lazySet(collection);
    }

    public String getType() {
        return type;
    }
}
