package com.society.leagues.test;

import com.society.leagues.client.api.domain.*;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.junit.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;


public class TestUser extends BaseTest {

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
        assertFalse(teamApi.userTeams(u.getId()).isEmpty());
        Team team = teamApi.userTeams(u.getId()).iterator().next();
        assertEquals(u.getName(),team.getName());

        u.setId(null);
        u.setLogin(UUID.randomUUID().toString());

        User sameName = userApi.create(u);
        assertTrue(teamApi.userTeams(sameName.getId()).isEmpty());
    }

    @Test
    public void testPasswordReset() {
        User u = LeagueObject.copy(userApi.all().get(10));
        TokenReset tokenReset = userApi.resetRequest(u);
        assertTrue(tokenReset.getToken().isEmpty());
        u = leagueService.findOne(u);
        assertFalse(u.getTokens().isEmpty());
        Map<String,String> resetRequest= new HashMap<>();
        resetRequest.put("login",u.getLogin());
        resetRequest.put("password","12345");
        User newUser = userApi.resetPassword(u.getTokens().iterator().next().getToken(),resetRequest);
        assertEquals(u.getId(),newUser.getId());

        login(u.getLogin(),"12345");
        u = userApi.get();
        assertEquals(newUser.getId(),u.getId());
    }

    @Test
    public void testProfile() {
        User u = userApi.active().stream().findAny().get();
        LocalTime now = LocalTime.now();

        List<LocalTime> broadcast = new ArrayList<>();
        broadcast.add(now.plusHours(1));
        broadcast.add(now.plusHours(2));

        u.getUserProfile().setDisabledSlots(Collections.singletonList(now.toString()));
        u.getUserProfile().setBroadcastSlotsLocalTime(broadcast);

        User newUser = userApi.modifyProfile(u);
        newUser.getUserProfile().getBroadcastSlots().containsAll(broadcast);
        newUser.getUserProfile().getDisabledSlots().contains(now);
    }

}
