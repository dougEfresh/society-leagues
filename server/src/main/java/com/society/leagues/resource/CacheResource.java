package com.society.leagues.resource;

import com.society.leagues.service.*;
import com.society.leagues.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/cache")
@SuppressWarnings("unused")
public class CacheResource {
    @Autowired LeagueService leagueService;
    @Autowired StatService statService;
    @Autowired CacheUtil cacheUtil;
    @Autowired ResultService resultService;
    @Autowired UserService userService;
    @Autowired ChallengeService challengeService;

    @RequestMapping(value = "/refresh", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String refresh() {
        return refreshNonWeb();
    }

     public String refreshNonWeb() {
         cacheUtil.refreshAllCache();
         statService.refresh();
         resultService.refresh();
         userService.refresh();
         challengeService.refresh();
        return LocalDateTime.now().toString();
    }
}
