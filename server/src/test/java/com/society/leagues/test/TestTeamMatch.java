package com.society.leagues.test;

import com.society.leagues.client.api.domain.*;
import feign.RetryableException;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;


public class TestTeamMatch extends BaseTest {

    @Test
    public void testCreate() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        List<Team> teams = teamApi.seasonTeams(season.getId());
        TeamMatch tm = new TeamMatch(teams.get(0), teams.get(1), LocalDateTime.now());
        tm.setHomeRacks(1);
        List<TeamMatch> matches = teamMatchApi.save(Collections.singletonList(tm));
        TeamMatch newTm = matches.iterator().next();
        assertEquals(season,newTm.getSeason());
        assertEquals(teams.get(0),newTm.getHome());
        assertEquals(teams.get(1),newTm.getAway());
        assertTrue(newTm.getHomeRacks() == 1);
        assertTrue(newTm.getAwayRacks() == 0);
        assertTrue(newTm.getHomeForfeits() == 0);
        assertTrue(newTm.getAwayForfeits() == 0);
        assertTrue(newTm.getSetAwayWins() == 0);
        assertTrue(newTm.getSetHomeWins() == 0);
        assertTrue(tm.getMatchDate().isEqual(newTm.getMatchDate()));
        List<TeamMatch> homeTeamMatches  = teamMatchApi.getTeamMatchByTeam(teams.get(0).getId());
        assertTrue(homeTeamMatches.stream().filter(m->m.getId().equals(newTm.getId())).count() == 1);
        teamMatchApi.delete(newTm.getId());
        boolean error = false;
        try {
            teamMatchApi.get(newTm.getId());
        } catch (RetryableException r) {
            error = true;
        }
        assertTrue(error);

    }

    @Test
    public void testAdd() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        TeamMatch tm =  teamMatchApi.add(season.getId(), LocalDate.now().toString());
        assertNotNull(tm);
        assertTrue(tm.getMatchDate().toLocalDate().isEqual(LocalDate.now()));

    }

    @Test
    public void testTeamStats() {
        Season season = seasonApi.active().stream().filter(s -> !s.isChallenge()).findAny().get();
        validate(season);
    }

    private TeamMatch validate(Season season) {

        TeamMatch tm = teamMatchApi.add(season.getId(), LocalDate.now().toString());
        Team home = statApi.teamSeasonStats(season.getId()).stream().filter(t->t.equals(tm.getHome())).findAny().get();
        Team away = statApi.teamSeasonStats(season.getId()).stream().filter(t->t.equals(tm.getAway())).findAny().get();
        Stat challengeHome  = null;
        Stat challengeAway = null;
        if (season.isChallenge()) {
            challengeHome = statApi.getTeamMemberStats(home.getId()).get(0);
            challengeAway = statApi.getTeamMemberStats(away.getId()).get(0);
        }
        tm.setHomeRacks(0);
        tm.setAwayRacks(1);
        tm.setSetAwayWins(3);
        tm.setSetHomeWins(1);
        tm.setHomeForfeits(1);
        tm.setAwayForfeits(2);
        TeamMatch teamMatch = teamMatchApi.save(Collections.singletonList(tm)).stream().filter(t -> t.equals(tm)).findAny().get();
        Team h = statApi.teamSeasonStats(season.getId()).stream().filter(st->st.equals(home)).findAny().get();
        Team a = statApi.teamSeasonStats(season.getId()).stream().filter(st->st.equals(away)).findAny().get();

        assertTrue(home.getStats().getLoses() + 1 == h.getStats().getLoses());
        assertTrue(home.getStats().getWins().equals(h.getStats().getWins()));
        assertTrue(home.getStats().getForfeits() + 1 == h.getStats().getForfeits());
        assertTrue(home.getStats().getSetWins() + 1 == h.getStats().getSetWins());
        assertTrue(home.getStats().getSetLoses() + 3 == h.getStats().getSetLoses());
        assertTrue(home.getStats().getRacksWon().equals(h.getStats().getRacksWon()));
        assertTrue(home.getStats().getRacksLost() + 1  == h.getStats().getRacksLost());

        assertTrue(away.getStats().getLoses().equals(a.getStats().getLoses()));
        assertTrue(away.getStats().getWins() + 1 == a.getStats().getWins());
        assertTrue(away.getStats().getForfeits() + 2 == a.getStats().getForfeits());
        assertTrue(away.getStats().getSetWins() + 3 == a.getStats().getSetWins());
        assertTrue(away.getStats().getSetLoses() + 1 == a.getStats().getSetLoses());
        assertTrue(away.getStats().getRacksWon() +1 == a.getStats().getRacksWon());
        assertTrue(away.getStats().getRacksLost().equals(a.getStats().getRacksLost()));

        if (season.isChallenge()) {
            Stat hStat = statApi.getUserSeasonStats(challengeHome.getUser().getId(),season.getId()).get(0);
            Stat aStat = statApi.getUserSeasonStats(challengeAway.getUser().getId(),season.getId()).get(0);

            assertTrue(challengeHome.getLoses() + 1 == hStat.getLoses());
            assertTrue(challengeHome.getWins().equals(hStat.getWins()));
            assertTrue(challengeHome.getRacksWon().equals(hStat.getRacksWon()));
            assertTrue(challengeHome.getRacksLost() + 1 == hStat.getRacksLost());
            assertTrue(hStat.getPoints() > 0);
            assertTrue(aStat.getPoints() > 0);
            assertTrue(aStat.getPoints() > hStat.getPoints());

            assertTrue(challengeAway.getLoses().equals(aStat.getLoses()));
            assertTrue(challengeAway.getWins() + 1 == aStat.getWins());
            assertTrue(challengeAway.getRacksWon() +1 == aStat.getRacksWon());
            assertTrue(challengeAway.getRacksLost().equals(aStat.getRacksLost()));

            List<PlayerResult> hResults = playerResultApi.getResults(challengeHome.getUser().getId(),season.getId());
            List<PlayerResult> aResults = playerResultApi.getResults(challengeAway.getUser().getId(),season.getId());
            assertFalse(hResults.isEmpty());
            assertFalse(aResults.isEmpty());
            assertTrue(hResults.stream().filter(r->r.getTeamMatch().equals(tm)).count() == 1);
            PlayerResult hResult = hResults.stream().filter(r->r.getTeamMatch().equals(tm)).findAny().get();
            PlayerResult aResult = aResults.stream().filter(r->r.getTeamMatch().equals(tm)).findAny().get();

            assertEquals(hResult.getHomeRacks(),teamMatch.getHomeRacks());
            assertEquals(aResult.getAwayRacks(),teamMatch.getAwayRacks());

            assertTrue(hResult.getMatchPoints().getPoints() > 0);
            assertTrue(aResult.getMatchPoints().getPoints() > 0);
        }

        return teamMatch;
    }

    @Test
    public void testChallenge() {
        Season season = seasonApi.active().stream().filter(Season::isChallenge).findAny().get();
        TeamMatch tm = validate(season);
    }

    @Test
    public void testTeamMatchAvailable() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        TeamMatch tm = seasonApi.schedule(season.getId()).get(0);
        tm.getHome().setMembers(teamApi.members(tm.getHome().getId()));
        tm.getAway().setMembers(teamApi.members(tm.getAway().getId()));
        Iterator<User> iterator = tm.getHome().getMembers().getMembers().iterator();

        tm.addHomeNotAvailable(iterator.next().getId());
        tm.addHomeNotAvailable(iterator.next().getId());
        Set<String> notAvailable = tm.getHomeNotAvailable();

        TeamMatch newTeamMatch = teamMatchApi.modifyAvailable(tm);
        assertTrue(notAvailable.containsAll(newTeamMatch.getHomeNotAvailable()));
        assertTrue(newTeamMatch.getAwayNotAvailable().isEmpty());
        for (String s : notAvailable) {
            assertFalse(newTeamMatch.homeAvailable().contains(new User(s)));
            assertTrue(newTeamMatch.homeNotAvailable().contains(new User(s)));
        }

        tm = newTeamMatch;

        iterator = tm.getAway().getMembers().getMembers().iterator();
        tm.addAwayNotAvailable(iterator.next().getId());
        tm.addAwayNotAvailable(iterator.next().getId());
        notAvailable = tm.getAwayNotAvailable();

        newTeamMatch = teamMatchApi.modifyAvailable(tm);
        assertTrue(notAvailable.containsAll(newTeamMatch.getAwayNotAvailable()));

        for (String s : notAvailable) {
            assertFalse(newTeamMatch.awayAvailable().contains(new User(s)));
            assertTrue(newTeamMatch.awayNotAvailable().contains(new User(s)));
        }

        seasonApi.deleteSchedule(season.getId());
    }

}
