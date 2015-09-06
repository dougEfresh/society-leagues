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
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true)
public class TestStat {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";
    @Autowired
    LeagueService leagueService;
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
    public void testTeamStat() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch tm = new TeamMatch(home,away,LocalDateTime.now());
        tm.setAwayRacks(5);
        tm.setHomeRacks(10);
        tm.setSetHomeWins(3);
        tm.setSetAwayWins(1);
        tm = leagueService.save(tm);
        TeamMatch match2 = new TeamMatch(home,away,LocalDateTime.now());
        match2.setAwayRacks(7);
        match2.setHomeRacks(5);
        match2.setSetHomeWins(2);
        match2.setSetAwayWins(2);
        match2 = leagueService.save(match2);

        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        Stat stat = restTemplate.exchange(host + "/api/stat/team/" + tm.getHome().getId() , HttpMethod.GET, requestEntity, Stat.class).getBody();
        assertNotNull(stat);
        assertTrue(stat.getLoses() == 1);
        assertTrue(stat.getWins() == 1);
        assertEquals(new Integer(10 + 5), stat.getRacksWon());
        assertEquals(new Integer(5 + 7), stat.getRacksLost());
    }

    @Test
    public void testTeamMember() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        Team anotherTeam = utils.createRandomTeam();
         TeamMatch match1 = new TeamMatch(home,away,LocalDateTime.now());
         match1.setAwayRacks(5+7);
         match1.setHomeRacks(10 + 5);
         match1.setSetHomeWins(3);
         match1.setSetAwayWins(1);
         match1 = leagueService.save(match1);

         TeamMatch match2 = new TeamMatch(home,anotherTeam,LocalDateTime.now());

         match2.setAwayRacks(7);
         match2.setHomeRacks(5);
         match2.setSetHomeWins(2);
         match2.setSetAwayWins(2);
         match2 = leagueService.save(match2);

         PlayerResult result1 = new PlayerResult();
         result1.setHomeRacks(5);
         result1.setAwayRacks(7);
         result1.setPlayerHome(home.getMembers().stream().findFirst().get());
         result1.setPlayerAway(away.getMembers().stream().findFirst().get());
         result1.setTeamMatch(match1);
         result1.setSeason(home.getSeason());
         leagueService.save(result1);

         PlayerResult result2 = new PlayerResult();
         result2.setHomeRacks(10);
         result2.setAwayRacks(5);
         result2.setPlayerHome(home.getMembers().stream().findFirst().get());
         result2.setPlayerAway(anotherTeam.getMembers().stream().findFirst().get());
         result2.setTeamMatch(match2);
         result2.setSeason(home.getSeason());
         leagueService.save(result2);

        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        List<Stat> stats = Arrays.asList(restTemplate.exchange(host + "/api/stat/team/" + home.getId() + "/members", HttpMethod.GET, requestEntity, Stat[].class).getBody());

         assertTrue(stats.size() == 1);
         Stat stat = stats.get(0);
         assertTrue(stat.getMatches() == 2);
         assertEquals(new Integer(10+5), stat.getRacksWon());
         assertEquals(new Integer(5 + 7), stat.getRacksLost());
         User user = home.getMembers().stream().findFirst().get();
         assertEquals(user,stat.getUser());
         assertEquals(home,stat.getTeam());
         assertEquals(new Integer(0),stat.getSetLoses());
         assertEquals(new Integer(0),stat.getSetWins());
     }

    @Test
    public void testTeamSeasonStats() {
        Team home = utils.createRandomTeam();
        Team away = utils.createRandomTeam();
        TeamMatch match1 = new TeamMatch(home,away,LocalDateTime.now());
        match1.setAwayRacks(5+7);
        match1.setHomeRacks(10+5);
        match1.setSetHomeWins(3);
        match1.setSetAwayWins(1);
        match1 = leagueService.save(match1);

        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        List<Stat> stats = Arrays.asList(restTemplate.exchange(host + "/api/stat/season/" + home.getSeason().getId(),
                HttpMethod.GET, requestEntity, Stat[].class).getBody());
        assertTrue(stats.size() > 0);
        Stat s = stats.stream().filter(st->st.getTeam().equals(home)).findFirst().get();
        assertEquals(new Integer(5+7),s.getRacksLost());
        assertEquals(new Integer(10+5),s.getRacksWon());
        assertEquals(new Integer(3),s.getSetWins());
        assertEquals(new Integer(1),s.getSetLoses());

    }

}
