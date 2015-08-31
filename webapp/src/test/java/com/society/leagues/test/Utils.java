package com.society.leagues.test;


import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Component
public class Utils {

    @Autowired UserRepository userRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired HandicapSeasonRepository handicapSeasonRepository;
    @Autowired TeamMatchRepository tmRepository;
    @Autowired List<MongoRepository> repositories;

    static String JSESSIONID = null;

    public User createAdminUser() {
        User adminUser = userRepository.findByLogin("test");
        if (adminUser != null) {
            return adminUser;
        }
        Season s = seasonRepository.save(new Season("9 ball ", new Date(),-1, Division.NINE_BALL_CHALLENGE));
        Team t = teamRepository.save(new Team(s,"testteam"));
        Team t2 = teamRepository.save(new Team(s,"anotherteam"));
        HandicapSeason hs = handicapSeasonRepository.save(new HandicapSeason(Handicap.A, s));

        User u = new User();
        u.setLogin("test");
        u.setFirstName("blah");
        u.setLastName("asdsa");
        u.setEmail("me@you.com");
        u.setRole(Role.ADMIN);
        u.addHandicap(hs);
        u.addTeam(t);
        u.setPassword(new BCryptPasswordEncoder().encode("abc123"));
        return userRepository.save(u);
    }

    public User createRandomUser() {
        Season s = seasonRepository.findAll().stream().filter(sn->sn.getDivision() == Division.NINE_BALL_CHALLENGE).findFirst().orElse(null);
        if (s == null) {
            s = seasonRepository.save(new Season("9 ball ", new Date(), -1, Division.NINE_BALL_CHALLENGE));
        }
        Team ts = teamRepository.save(new Team(s,UUID.randomUUID().toString()));
        HandicapSeason hs = handicapSeasonRepository.save(new HandicapSeason(Handicap.A, s));

        User u = new User();
        String login = UUID.randomUUID().toString();
        u.setLogin(login);
        u.setFirstName("blah");
        u.setLastName("asdsa");
        u.setEmail(login);
        u.setRole(Role.PLAYER);
        u.addHandicap(hs);
        u.addTeam(ts);
        u.setPassword(new BCryptPasswordEncoder().encode("abc123"));
        return userRepository.save(u);
    }

    public String getSessionId(String url) {
        if (JSESSIONID != null) {
            return JSESSIONID;
        }
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username","test");
        map.add("password", "abc123");
        ResponseEntity<User> responseEntity = restTemplate.postForEntity(url, map, User.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().getLogin(), "test");
        assertTrue(responseEntity.getHeaders().containsKey("Set-Cookie"));
        JSESSIONID =  responseEntity.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        return JSESSIONID;

    }

}
