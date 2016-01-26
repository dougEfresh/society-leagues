package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.SeasonApi;
import com.society.leagues.client.api.TeamApi;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.conf.ClientApiConfig;
import com.society.leagues.security.CookieContext;
import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest(randomPort = true,value = {"use.local=true"})
@ActiveProfiles(profiles = "test")
public class TestUser {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
	int port;
    String host = "http://localhost";
    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
    @Autowired ChallengeService challengeService;
    @Autowired LeagueService leagueService;
    @Autowired Utils utils;
    RestTemplate restTemplate = new RestTemplate();
    static HttpHeaders requestHeaders = new HttpHeaders();
    @Autowired ClientApiConfig clientApiConfig;
    UserApi userApi;
    SeasonApi seasonApi;
    TeamApi teamApi;

    @Before
    public void  setup() {
        userApi = clientApiConfig.getApi(UserApi.class,"BASIC",host + ":" + port);
        seasonApi = clientApiConfig.getApi(SeasonApi.class,"BASIC",host + ":" + port);
        teamApi = clientApiConfig.getApi(TeamApi.class,"BASIC",host + ":" + port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("username", "admin.admin@example.com");
        body.add("password","abc123");
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

    @Test
    public void testUser() {
        User me =userApi.get();
        assertTrue(userApi.active().stream().filter(u->!u.isActive()).count()  == 0);
        assertFalse(userApi.all().isEmpty());
        assertEquals("admin",me.getFirstName());
        assertEquals("admin",me.getLastName());
        assertEquals("admin.admin@example.com",me.getLogin());
        assertNull(me.getEmail());
    }

    @Test
    public void testCreate() {
        User before = createUser();
        User u = userApi.create(before);
        assertEquals(before.getFirstName(),u.getFirstName());
        assertEquals(before.getLastName(),u.getLastName());
        assertEquals(before.getLogin(),u.getLogin());
        assertNotNull(u.getId());
    }

    @Test
    public void testModify() {
        User before = LeagueObject.copy(userRepository.findAll().iterator().next());
        before.setFirstName(UUID.randomUUID().toString());
        User u = userApi.modify(before);
        assertEquals(before.getId(),u.getId());
        assertEquals(before.getFirstName(),u.getFirstName());
    }

    public User createUser() {
        User u = new User();
        Fairy fairy = Fairy.create();
        Person person = fairy.person();
        u.setFirstName(person.firstName());
        u.setLastName(person.lastName());
        String login = String.format("%s%s@%s-example.com",u.getFirstName().toLowerCase(),u.getLastName().toLowerCase(),UUID.randomUUID().toString());
        u.setLogin(login);
        u.setEmail(login);
        u.setRole(Role.PLAYER);
        u.setStatus(Status.ACTIVE);
        return u;
    }

    @Test
    public void testChallengeUser() {
        User newUser = createUser();
        Season season = seasonApi.get().stream().filter(s->s.isChallenge()).findFirst().get();
        newUser.addHandicap(new HandicapSeason(Handicap.DPLUS,season));
        User u = userApi.create(newUser);
        assertEquals(u.getHandicap(season),newUser.getHandicap(season));
        assertFalse(teamApi.getTeamsByUser(u.getId()).isEmpty());
        Team team = teamApi.getTeamsByUser(u.getId()).iterator().next();
        assertEquals(u.getName(),team.getName());

        u.setId(null);
        u.setLogin(UUID.randomUUID().toString());

        User sameName = userApi.create(u);
        assertTrue(teamApi.getTeamsByUser(sameName.getId()).isEmpty());
    }

    //@Test
    public void testPasswordReset() {
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        User newUser = userRepository.findByLogin("test");

        ResponseEntity<User> responseEntity = restTemplate.exchange(host + "/api/user/" + newUser.getId(), HttpMethod.GET,requestEntity,User.class);
        User returned = responseEntity.getBody();
        requestEntity = new HttpEntity("newPassword", requestHeaders);
        responseEntity = restTemplate.exchange(host + "/api/user/reset/password/kljkljasd/" + newUser.getId(), HttpMethod.POST, requestEntity, User.class);
        returned = responseEntity.getBody();
        assertEquals("0", returned.getId());

        TokenReset reset = userService.resetRequest(newUser);
        assertTrue(!newUser.getTokens().isEmpty());
        assertTrue(newUser.getTokens().contains(reset));
        responseEntity = restTemplate.exchange(host + "/api/user/reset/password/" + reset.getToken() + "/" + newUser.getId(), HttpMethod.POST, requestEntity, User.class);
        returned = responseEntity.getBody();
        assertEquals(newUser.getId(),returned.getId());
    }
}
