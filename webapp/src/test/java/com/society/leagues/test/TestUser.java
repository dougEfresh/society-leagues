package com.society.leagues.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest
public class TestUser {

    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
	private int port;
    private String host = "http://localhost";

    @Autowired UserRepository userRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamSeasonRepository teamSeasonRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired HandicapSeasonRepository handicapSeasonRepository;
    @Autowired TeamMatchRepository tmRepository;
    @Autowired List<MongoRepository> repositories;

    private RestTemplate restTemplate = new RestTemplate();
    static boolean initialized = false;
    static HttpHeaders requestHeaders = new HttpHeaders();
    static String JSESSIONID = "";

    @Before
    public void setUp() {
        host += ":" + port;
        if (initialized)
            return ;

        initialized = true;
        for (MongoRepository repository : repositories) {
            repository.deleteAll();
        }

        Team t = teamRepository.save(new Team("testteam"));
        Team t2 = teamRepository.save(new Team("anotherteam"));
        Season s = seasonRepository.save(new Season("9 ball ", new Date(),-1, Division.NINE_BALL_CHALLENGE));
        TeamSeason ts = teamSeasonRepository.save(new TeamSeason(s, t));
        HandicapSeason hs = handicapSeasonRepository.save(new HandicapSeason(Handicap.A, s));

        User u = new User();
        u.setLogin("test");
        u.setFirstName("blah");
        u.setLastName("asdsa");
        u.setEmail("me@you.com");
        u.setRole(Role.ADMIN);
        u.addHandicap(hs);
        u.addTeam(ts);
        u.setPassword(new BCryptPasswordEncoder().encode("abc123"));
        userRepository.save(u);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username","test");
        map.add("password", "abc123");
        ResponseEntity<User> responseEntity = restTemplate.postForEntity(host + "/api/authenticate", map, User.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().getLogin(), "test");
        assertTrue(responseEntity.getHeaders().containsKey("Set-Cookie"));
        JSESSIONID = responseEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        requestHeaders.add("Cookie", JSESSIONID);
    }

    @Test
    public void testUser() {
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);

        User newUser = userRepository.findByLogin("test");

        ResponseEntity<User> responseEntity = restTemplate.exchange(host + "/api/user/" + newUser.getId(), HttpMethod.GET,requestEntity,User.class);
        User returned = responseEntity.getBody();
        assertEquals(returned.getId(),newUser.getId());
        assertEquals(returned.getLogin(),newUser.getLogin());

        responseEntity = restTemplate.exchange(host + "/api/user/login/" + newUser.getLogin(), HttpMethod.GET,requestEntity,User.class);
        returned = responseEntity.getBody();
        assertEquals(returned.getId(),newUser.getId());
        assertEquals(returned.getLogin(),newUser.getLogin());


        responseEntity = restTemplate.exchange(host + "/api/user", HttpMethod.GET,requestEntity,User.class);
        returned = responseEntity.getBody();
        assertEquals(returned.getId(),newUser.getId());
        assertEquals(returned.getLogin(),newUser.getLogin());
    }

     @Test(expected = HttpClientErrorException.class)
     public void testLogin() {
         ResponseEntity<User> responseEntity = restTemplate.getForEntity(host + "/api/user" ,User.class);
         assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());
     }

    @Test
    public void testCreate() {
        User u = new User();
        u.setLogin("user");
        u.setFirstName("blah");
        u.setLastName("asdsa");
        u.setEmail("me@you.com");
        u.setRole(Role.PLAYER);
        u.setPassword(new BCryptPasswordEncoder().encode("abc123"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", JSESSIONID);
        HttpEntity requestEntity = new HttpEntity(u, headers);

        User response = restTemplate.postForEntity(host +"/api/user/create",requestEntity,User.class).getBody();
        assertNotNull(response.getId());
        assertNull(response.getPassword());
        assertEquals("user",response.getLogin());

    }

    @Test
    public void testModify() {
        User newUser = userRepository.findByLogin("test");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", JSESSIONID);
        newUser.setFirstName("new name");
        HttpEntity requestEntity = new HttpEntity(newUser, headers);
        User response = restTemplate.postForEntity(host +"/api/user/modify",requestEntity,User.class).getBody();
        assertNotNull(response.getId());
        assertNull(response.getPassword());
        assertEquals("new name",response.getFirstName());
    }
}
