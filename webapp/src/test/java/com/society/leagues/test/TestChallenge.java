package com.society.leagues.test;


import com.society.leagues.Main;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest("server.port=8081")
public class TestChallenge {
    private static Logger logger = Logger.getLogger(TestUser.class);

    @Value("${local.server.port}")
	private int port;
    private String host = "http://localhost";
    @Autowired ChallengRepository challengRepository;
    @Autowired UserRepository userRepository;
    @Autowired SlotRepository slotRepository;
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
    public void testCreateChallenge() {
        User ch = new User(utils.createRandomUser().getId());
        User op = new User(utils.createRandomUser().getId());

        List<Slot> slots = new ArrayList<>();
        for( Slot s : Arrays.asList(new Slot(LocalDateTime.now().minusHours(1)), new Slot(LocalDateTime.now().minusHours(2)), new Slot(LocalDateTime.now().minusHours(3)))) {
            Slot slotId = new Slot();
            slotId.setId(slotRepository.save(s).getId());
            slots.add(slotId);
        }
        Challenge c = new Challenge(Status.PENDING,ch,op,slots);
        HttpEntity requestEntity = new HttpEntity(c, requestHeaders);
        Challenge newChallenge = restTemplate.postForEntity(host + "/api/challenge/create",requestEntity,Challenge.class).getBody();

        assertEquals(ch, newChallenge.getChallenger());
        assertEquals(op, newChallenge.getOpponent());
        assertNotNull(newChallenge.getChallenger().getId());
        assertNotNull(newChallenge.getOpponent().getId());
        assertEquals(ch.getId(), newChallenge.getChallenger().getId());
        assertEquals(op.getId(), newChallenge.getOpponent().getId());


        assertNotNull(newChallenge.getChallenger().getFirstName());
        assertNotNull(newChallenge.getOpponent().getFirstName());

        assertEquals(3, newChallenge.getSlots().size());
        assertEquals(Status.PENDING,newChallenge.getStatus());

        for (Slot slot : newChallenge.getSlots()) {
            assertNotNull(slot.getLocalDateTime());
        }
        assertNull(newChallenge.getAcceptedSlot());
    }

    @Test
    public void testAcceptChallenge() {
        User u1 = new User(utils.createRandomUser().getId());
        User u2 = new User(utils.createRandomUser().getId());
        List<Slot> slots = new ArrayList<>();
        for(Slot s : Arrays.asList(new Slot(LocalDateTime.now().minusHours(1)), new Slot(LocalDateTime.now().minusHours(2)), new Slot(LocalDateTime.now().minusHours(3)))) {
            Slot slotId = new Slot();
            slotId.setId(slotRepository.save(s).getId());
            slots.add(slotId);
        }
        Challenge c = new Challenge(Status.PENDING,u1,u2,slots);
        c = challengRepository.save(c);
        c.setAcceptedSlot(slotRepository.findOne(c.getSlots().get(0).getId()));
        HttpEntity requestEntity = new HttpEntity(c, requestHeaders);
        TeamMatch tm = restTemplate.postForEntity(host + "/api/challenge/accept",requestEntity,TeamMatch.class).getBody();
        assertEquals(tm.getMatchDate(),c.getAcceptedSlot().getLocalDateTime());
    }

    @Test
    public void testDeclineChallenge() {
        User u1 = new User(utils.createRandomUser().getId());
        User u2 = new User(utils.createRandomUser().getId());
        List<Slot> slots = new ArrayList<>();
        for( Slot s : Arrays.asList(new Slot(LocalDateTime.now().minusHours(1)), new Slot(LocalDateTime.now().minusHours(2)), new Slot(LocalDateTime.now().minusHours(3)))) {
            Slot slotId = new Slot();
            slotId.setId(slotRepository.save(s).getId());
            slots.add(slotId);
        }
        Challenge c = new Challenge(Status.ACCEPTED,u1,u2,slots);
        c.setAcceptedSlot(slots.get(0));
        c = challengRepository.save(c);
        HttpEntity requestEntity = new HttpEntity(c, requestHeaders);
        Challenge newChallenge = restTemplate.postForEntity(host + "/api/challenge/decline",requestEntity, Challenge.class).getBody();
        assertEquals(newChallenge.getStatus(), Status.CANCELLED);
        assertNull(newChallenge.getAcceptedSlot());
    }
}
