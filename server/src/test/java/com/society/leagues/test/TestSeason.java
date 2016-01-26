package com.society.leagues.test;


import com.society.leagues.client.api.domain.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestSeason extends BaseTest {

    @Test
    public void testSeason() {
        List<Season> active = seasonApi.active();
        List<Season> all = seasonApi.get();
        assertNotNull(all);
        assertNotNull(active);
        assertFalse(active.isEmpty());
        assertFalse(all.isEmpty());
        assertTrue(active.stream().filter(s->!s.isActive()).count() == 0);
    }

    @Test
    public void testCreate() {
        Season s = new Season();
         s.setsDate(LocalDate.now());
         s.setDivision(Division.EIGHT_BALL_THURSDAYS);
         s.setRounds(10);
         s.setSeasonStatus(Status.PENDING);
         Season newSeason = seasonApi.create(s);

         assertNotNull(newSeason);
         assertNotNull(newSeason.getId());
         assertEquals(newSeason.getRounds(),new Integer(10));
         assertEquals(newSeason.getDivision(), Division.EIGHT_BALL_THURSDAYS);
         assertEquals(newSeason.getSeasonStatus(), Status.PENDING);
    }

    @Test
    public void testModify() {
        Season s = seasonApi.active().get(2);
        Season original = LeagueObject.copy(seasonApi.active().get(2));

        s.setSeasonStatus(Status.DISABLED);
        s.setRounds(5);
        s.setsDate(LocalDate.now().minusDays(100));
        Season newSeason = seasonApi.modify(s);

        assertNotNull(newSeason);
        assertNotNull(newSeason.getId());
        assertEquals(newSeason.getId(),s.getId());
        assertEquals(newSeason.getRounds(),new Integer(5));
        assertEquals(newSeason.getsDate(), LocalDate.now().minusDays(100));
        assertEquals(newSeason.getSeasonStatus(), Status.DISABLED);

        seasonApi.modify(original);
    }

    @Test
    public void testSchedule() {
        Season season = seasonApi.active().stream().filter(s->!s.isChallenge()).findAny().get();
        List<Team> teams = teamApi.seasonTeams(season.getId());
        List<TeamMatch> matches = seasonApi.schedule(season.getId());
        Map<String,List<TeamMatch>> teamMatches = teamMatchApi.matchesBySeasonSummary(season.getId());
        assertNotNull(matches);
        for (String s : teamMatches.keySet()) {
            assertTrue(teamMatches.get(s).size() == teams.size()/2);
        }
    }
}
