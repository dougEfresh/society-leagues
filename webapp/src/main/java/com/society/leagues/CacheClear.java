package com.society.leagues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
public class CacheClear {
    public static final int EVICT_TIMEOUT =  60 * 1000 * 5 ;
    private static Logger logger = LoggerFactory.getLogger(WebMapCache.class);
    @Autowired List<WebMapCache> webMapCaches;
    @Value("${cache-token:}")
    String token;

    @Scheduled(fixedRate = EVICT_TIMEOUT)
    public void evict() {
        logger.info("Evicting Cache");
        for (WebMapCache webMapCach : webMapCaches) {
            webMapCach.evict();
        }
    }

    @RequestMapping(value = "/api/cache/clear/{tokenRequest}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Boolean clear(@PathVariable String tokenRequest) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (tokenRequest.equals(token)) {
            evict();
            return true;
        }
        return false;
    }

}
