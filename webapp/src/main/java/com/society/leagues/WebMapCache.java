package com.society.leagues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class WebMapCache<T extends Map> {
    final AtomicReference<T> cache;

    public WebMapCache(T cache) {
        this.cache = new AtomicReference<>();
        this.cache.set(cache);
    }

    public void evict() {
        cache.get().clear();
    }

    public void setCache(T cache) {
        this.cache.get().clear();
        this.cache.set(cache);
    }

    public T getCache() {
        return cache.get();
    }

    public boolean isEmpty() {
        if (cache == null || cache.get() == null)
            return true;

        return cache.get().isEmpty();
    }

}
