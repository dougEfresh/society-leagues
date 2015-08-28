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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest("security-disable=true")
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
        map.add("password","abc123");
        ResponseEntity<User> responseEntity = restTemplate.postForEntity(host + "/api/authenticate", map, User.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().getLogin(),"test");
        assertTrue(responseEntity.getHeaders().containsKey("Set-Cookie"));
        requestHeaders.add("Cookie",responseEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0]);
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

     @Test
     public void testLogin() {


     }
}
