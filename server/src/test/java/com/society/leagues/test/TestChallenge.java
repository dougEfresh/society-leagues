package com.society.leagues.test;



import com.society.leagues.client.api.domain.*;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestChallenge extends  BaseTest {


    @Test
    public void aCreateChallenge() {
        Season season = seasonApi.active().stream().filter(s->s.isChallenge()).findAny().get();
        Slot slot = challengeApi.challengeSlots().get(0);
        Team challenger = teamApi.seasonTeams(season.getId()).get(0);
        Team opponent = teamApi.seasonTeams(season.getId()).get(1);
        Challenge challenge = new Challenge();
        challenge.setStatus(Status.NOTIFY);
        challenge.setOpponent(opponent);
        challenge.setChallenger(challenger);
        challenge.setSlots(Collections.singletonList(slot));
        Challenge requested  = challengeApi.challenge(challenge);
        assertNotNull(requested);
        assertNotNull(requested.getId());
        assertEquals(challenge.getChallenger(),requested.getChallenger());
        assertEquals(challenge.getOpponent(),requested.getOpponent());
        assertFalse(requested.getSlots().isEmpty());
        assertEquals(challenge.getSlots().get(0),requested.getSlots().get(0));

        List<Challenge> challengeList = challengeApi.challengesForUser(challenge.getUserChallenger().getId());
        assertTrue(challengeList.stream().filter(c->c.getId().equals(requested.getId())).count() == 1);

        List<Team> challengers = challengeApi.challengeUsersOnDate(LocalDate.now().toString());
        List<Team> challengeTeams = teamApi.seasonTeams(season.getId());
        assertTrue(challengers.size() == challengeTeams.size());
    }

    @Test
    public void bAccept() {
        Season season = seasonApi.active().stream().filter(Season::isChallenge).findAny().get();
        Slot slot = challengeApi.challengeSlots().get(0);
        Team challenger = teamApi.seasonTeams(season.getId()).get(0);
        Team opponent = teamApi.seasonTeams(season.getId()).get(1);
        Challenge challenge = new Challenge();
        challenge.setStatus(Status.NOTIFY);
        challenge.setOpponent(opponent);
        challenge.setChallenger(challenger);
        challenge.setSlots(Collections.singletonList(slot));
        Challenge requested  = challengeApi.challenge(challenge);
        requested.setAcceptedSlot(requested.getSlots().get(0));
        Challenge accepted = challengeApi.accept(requested);
        assertNotNull(accepted.getTeamMatch());
        assertNotNull(accepted.getId());
        assertEquals(requested.getSlots().get(0),accepted.getAcceptedSlot());
    }

    @Test
    public void cCancel() {
        Season season = seasonApi.active().stream().filter(Season::isChallenge).findAny().get();
        Slot slot = challengeApi.challengeSlots().get(0);
        Team challenger = teamApi.seasonTeams(season.getId()).get(0);
        Team opponent = teamApi.seasonTeams(season.getId()).get(1);
        Challenge challenge = new Challenge();
        challenge.setStatus(Status.NOTIFY);
        challenge.setOpponent(opponent);
        challenge.setChallenger(challenger);
        challenge.setSlots(Collections.singletonList(slot));
        Challenge requested  = challengeApi.challenge(challenge);
        requested.setAcceptedSlot(requested.getSlots().get(0));
        Challenge accepted = challengeApi.accept(requested);
        Challenge canceled = challengeApi.cancel(accepted);

        assertTrue(canceled.getStatus() == Status.CANCELLED);
    }

    @Test
    public void dBroadcast() {
        Season season = seasonApi.active().stream().filter(Season::isChallenge).findAny().get();
        Slot slot = challengeApi.challengeSlots().get(0);
        Team challenger = teamApi.seasonTeams(season.getId()).get(0);
        Challenge challenge = new Challenge();
        challenge.setStatus(Status.BROADCAST);
        challenge.setChallenger(challenger);
        challenge.setSlots(Collections.singletonList(slot));
        Challenge requested  = challengeApi.challenge(challenge);

        assertTrue(requested.getStatus() == Status.BROADCAST);
        assertNull(requested.getOpponent());

    }
}
