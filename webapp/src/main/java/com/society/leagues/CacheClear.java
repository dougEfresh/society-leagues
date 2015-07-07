package com.society.leagues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheClear {
    public static final int EVICT_TIMEOUT =  60 * 1000 * 5 ;
    private static Logger logger = LoggerFactory.getLogger(WebMapCache.class);
    @Autowired List<WebMapCache> webMapCaches;

    @Scheduled(fixedRate = EVICT_TIMEOUT)
    public void evict() {
        logger.info("Evicting Cache");
        for (WebMapCache webMapCach : webMapCaches) {
            webMapCach.evict();
        }
    }

}
