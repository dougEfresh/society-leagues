package com.society.leagues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class WebListCache<T extends List> {
    public static final int EVICT_TIMEOUT =  60 * 1000 ;
    private static Logger logger = LoggerFactory.getLogger(WebMapCache.class);
    final AtomicReference<T> cache;

    public WebListCache(T cache) {
        this.cache = new AtomicReference<>();
        this.cache.set(cache);
    }

    @Scheduled(fixedRate = EVICT_TIMEOUT)
    public void evict() {
        logger.info("Evicting Cache");
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
