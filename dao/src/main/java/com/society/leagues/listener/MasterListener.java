package com.society.leagues.listener;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MasterListener {

    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired StatService statService;

    @PostConstruct
    public void init() {

    }
}
