package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest("server.port=8081")
public class TestTeamMatch {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
    int port;
    String host = "http://localhost";
    @Autowired LeagueService leagueService;
    @Autowired Utils utils;
    RestTemplate restTemplate = new RestTemplate();
    static HttpHeaders requestHeaders = new HttpHeaders();

    @Before
    public void setUp() {
        host += ":" + port;
        utils.createAdminUser();
        requestHeaders.add("Cookie", utils.getSessionId(host + "/api/authenticate"));
    }

    @Test
    public void testCreate() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        LocalDateTime now = LocalDateTime.now();
        TeamMatch tm = new TeamMatch(home,away, now);
        HttpEntity requestEntity = new HttpEntity(tm, requestHeaders);
        tm =  restTemplate.postForEntity(host + "/api/teammatch/admin/create",requestEntity,TeamMatch.class).getBody();
        assertNotNull(tm.getId());
        assertNotNull(tm.getSeason());
        assertNotNull(tm.getDivision());
        assertTrue(tm.getMatchDate().isEqual(now));
    }

    @Test
    public void testModify() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        User u1 = utils.createRandomUser();
        User u2 = utils.createRandomUser();
        home.addMember(u1);
        away.addMember(u2);
        leagueService.save(home);
        leagueService.save(away);
        tm.setAwayRacks(7);
        tm.setHomeRacks(6);
        HttpEntity requestEntity = new HttpEntity(tm, requestHeaders);
        TeamMatch newTeamMatch  =  restTemplate.postForEntity(host + "/api/teammatch/admin/modify", requestEntity, TeamMatch.class).getBody();
        assertNotNull(newTeamMatch.getId());
        assertNotNull(newTeamMatch.getSeason());
        assertNotNull(newTeamMatch.getDivision());
        assertTrue(newTeamMatch.getHome().getMembers().contains(u1));
        assertTrue(newTeamMatch.getAway().getMembers().contains(u2));
        assertEquals(new Integer(7), newTeamMatch.getAwayRacks());
        assertEquals(new Integer(6), newTeamMatch.getHomeRacks());
    }

    @Test
    public void testGet() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        TeamMatch newTeamMatch  =  restTemplate.exchange(host + "/api/teammatch/get/" + tm.getId(), HttpMethod.GET, requestEntity, TeamMatch.class).getBody();
        assertEquals(tm.getId(),newTeamMatch.getId());
        assertEquals(tm.getMatchDate(),newTeamMatch.getMatchDate());
        assertEquals(tm.getHome(),newTeamMatch.getHome());
        assertEquals(tm.getAway(),newTeamMatch.getAway());
    }

    @Test
    public void testTeamGetSeason() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = leagueService.save(new TeamMatch(home, away, LocalDateTime.now()));
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        List<TeamMatch> teams = Arrays.asList(restTemplate.exchange(host + "/api/teammatch/get/season/" + tm.getSeason().getId(), HttpMethod.GET, requestEntity, TeamMatch[].class).getBody());
        assertNotNull(teams);
        assertTrue(teams.size() > 0);
    }
}
