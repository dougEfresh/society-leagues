package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.conf.ClientApiConfig;
import com.society.leagues.mongo.UserRepository;
import com.society.leagues.security.CookieContext;
import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true,value = {"use.local=true"})
@ActiveProfiles(profiles = "test")
public class BaseTest {

    public static Logger logger = Logger.getLogger(TestUser.class);
    @Value("${local.server.port}")
    int port;
    String host = "http://localhost";
    UserApi userApi;
    SeasonApi seasonApi;
    TeamApi teamApi;
    TeamMatchApi teamMatchApi;
    StatApi statApi;
    PlayerResultApi playerResultApi;
    ChallengeApi challengeApi;
    RestTemplate restTemplate = new RestTemplate();
    @Autowired ClientApiConfig clientApiConfig;
    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
    @Autowired ChallengeService challengeService;
    @Autowired LeagueService leagueService;

    @Before
    public void  setup() {
        userApi = clientApiConfig.getApi(UserApi.class,"BASIC",host + ":" + port);
        seasonApi = clientApiConfig.getApi(SeasonApi.class,"BASIC",host + ":" + port);
        teamApi = clientApiConfig.getApi(TeamApi.class,"BASIC",host + ":" + port);
        teamMatchApi = clientApiConfig.getApi(TeamMatchApi.class,"BASIC",host + ":" + port);
        statApi = clientApiConfig.getApi(StatApi.class,"BASIC",host + ":" + port);
        playerResultApi = clientApiConfig.getApi(PlayerResultApi.class,"BASIC",host + ":" + port);
        challengeApi = clientApiConfig.getApi(ChallengeApi.class,"BASIC",host + ":" + port);
        login("admin.admin@example.com","abc123");
        purgeMatches();
    }

    @After
    public void purgeMatches() {
        /*
        List<Season> seasons = seasonApi.get();
        for (Season season : seasons) {
            List<TeamMatch> matches = teamMatchApi.matchesBySeasonList(season.getId());
            for (TeamMatch teamMatch : matches) {
                teamMatchApi.delete(teamMatch.getId());
            }
        }
        */
    }

    @Test
    public void dummyTest() {

    }

    public void login(String user, String pass) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", user);
        body.add("password",pass);
        body.add("springRememberMe", "true");
        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers);

        ResponseEntity<User> responseEntity = restTemplate.exchange(host + ":" + port + "/api/authenticate", HttpMethod.POST, httpEntity, User.class);
        User u = responseEntity.getBody();
        for (String s : responseEntity.getHeaders().get("Set-Cookie")) {
            logger.info("Adding cookie: " + s);
            CookieContext context = new CookieContext(s);
            SecurityContextHolder.setContext(context);
        }
    }
}
