package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest
public class TestPlayerResult {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";
    @Autowired LeagueService leagueService;
    @Autowired Utils utils;
    private RestTemplate restTemplate = new RestTemplate();
    static HttpHeaders requestHeaders = new HttpHeaders();

    @Before
    public void setUp() {
        host += ":" + port;
        utils.createAdminUser();
        requestHeaders.add("Cookie", utils.getSessionId(host + "/api/authenticate"));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testCreate() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        User playerHome = utils.createRandomUser();
        User playerAway= utils.createRandomUser();
        home.addMember(playerHome);
        away.addMember(playerAway);
        LocalDateTime now = LocalDateTime.now();
        TeamMatch tm = new TeamMatch(home,away, now);
        tm = leagueService.save(tm);
        PlayerResult pr = new PlayerResult(tm,playerHome,playerAway,6,7,0, Handicap.B,Handicap.BPLUS);
        HttpEntity requestEntity = new HttpEntity(pr, requestHeaders);
        pr =  restTemplate.postForEntity(host + "/api/playerresult/admin/create",requestEntity,PlayerResult.class).getBody();
        assertNotNull(pr.getId());
        assertNotNull(pr.getSeason());
        assertNotNull(pr.getTeamMatch());
        assertTrue(pr.getTeamMatch().getMatchDate().isEqual(now));
        assertEquals(playerAway, pr.getPlayerAway());
        assertEquals(playerHome,pr.getPlayerHome());
        assertEquals(home,pr.getTeamMatch().getHome());
        assertEquals(away,pr.getTeamMatch().getAway());

        assertEquals(new Integer(6),pr.getHomeRacks());
        assertEquals(new Integer(7), pr.getAwayRacks());
        assertEquals(Handicap.B,pr.getPlayerHomeHandicap());
        assertEquals(Handicap.BPLUS,pr.getPlayerAwayHandicap());

    }

    /*
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
*/
    @Test
    public void testGet() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        User playerHome = utils.createRandomUser();
        User playerAway= utils.createRandomUser();
        home.addMember(playerHome);
        away.addMember(playerAway);
        LocalDateTime now = LocalDateTime.now();
        TeamMatch tm = new TeamMatch(home,away, now);
        tm = leagueService.save(tm);
        PlayerResult pr = new PlayerResult(tm,playerHome,playerAway,6,7,0, Handicap.B,Handicap.BPLUS);
        pr = leagueService.save(pr);
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        PlayerResult returned  = restTemplate.exchange(host + "/api/playerresult/get/" + pr.getId(), HttpMethod.GET, requestEntity, PlayerResult.class).getBody();
        assertEquals(pr.getId(),returned.getId());
    }

    @Test
    public void testGetSeason() {
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        User playerHome = utils.createRandomUser();
        User playerAway= utils.createRandomUser();
        home.addMember(playerHome);
        away.addMember(playerAway);
        LocalDateTime now = LocalDateTime.now();
        TeamMatch tm = new TeamMatch(home,away, now);
        tm = leagueService.save(tm);
        PlayerResult pr = new PlayerResult(tm,playerHome,playerAway,6,7,0, Handicap.B,Handicap.BPLUS);
        pr = leagueService.save(pr);
        List<PlayerResult> teams = Arrays.asList(restTemplate.exchange(host + "/api/playerresult/get/user/" + pr.getPlayerAway().getId() + "/current", HttpMethod.GET, requestEntity, PlayerResult[].class).getBody());
        assertNotNull(teams);
        assertTrue(teams.size() > 0);
    }
}
