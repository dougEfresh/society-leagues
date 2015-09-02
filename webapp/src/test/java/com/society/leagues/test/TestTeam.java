package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest("server.port=8081")
public class TestTeam {

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
    public void testTeam() {
        Season s = utils.createRandomSeason();
        Team t = new Team(s,UUID.randomUUID().toString());
        User u = utils.createRandomUser();
        t.addMember(u);
        HttpEntity requestEntity = new HttpEntity(t, requestHeaders);
        t =  restTemplate.postForEntity(host + "/api/team/admin/create",requestEntity,Team.class).getBody();
        assertNotNull(t.getId());
        assertEquals(t.getSeason(),s);
        assertNotNull(t.getMembers());
        assertTrue(t.getMembers().size() == 1);
    }


    @Test
    public void testTeamModify() {
        Team t  = utils.createRandomTeam();
        User u = utils.createRandomUser();
        t.addMember(u);
        HttpEntity requestEntity = new HttpEntity(t, requestHeaders);
        t = restTemplate.postForEntity(host + "/api/team/admin/modify",requestEntity,Team.class).getBody();
        assertNotNull(t.getId());
        assertNotNull(t.getMembers());
        assertFalse(t.getMembers().isEmpty());
        assertTrue(t.getMembers().contains(u));
    }

    @Test
    public void testTeamGet() {
        Team t  = utils.createRandomTeam();
        User u = utils.createRandomUser();
        t.addMember(u);
        leagueService.save(t);
        HttpEntity requestEntity = new HttpEntity(t, requestHeaders);
        t = restTemplate.exchange(host + "/api/team/get/"  + t.getId(),HttpMethod.GET,requestEntity,Team.class).getBody();
        assertNotNull(t.getId());
        assertNotNull(t.getMembers());
        assertFalse(t.getMembers().isEmpty());
        assertTrue(t.getMembers().contains(u));
    }

    @Test
    public void testTeamGetSeason() {
        Team t  = utils.createRandomTeam();
        User u = utils.createRandomUser();
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        List<Team> teams = Arrays.asList(restTemplate.exchange(host + "/api/team/get/season/"  + t.getSeason().getId(), HttpMethod.GET,requestEntity,Team[].class).getBody());
        assertNotNull(teams);
        assertTrue(teams.size()>0);
    }

}
