package com.society.leagues.test;


import com.society.leagues.client.api.domain.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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

        Season old = seasonApi.active().stream().filter(sn->sn.getDivision() == Division.EIGHT_BALL_THURSDAYS).findAny().get();
        Season newSeason = seasonApi.create(s);

        assertNotNull(newSeason);
        assertNotNull(newSeason.getId());
        assertEquals(newSeason.getRounds(),new Integer(10));
        assertEquals(newSeason.getDivision(), Division.EIGHT_BALL_THURSDAYS);
        assertEquals(newSeason.getSeasonStatus(), Status.PENDING);

        List<Team> oldTeams = teamApi.seasonTeams(old.getId());
        List<Team> newTeams = teamApi.seasonTeams(newSeason.getId());
        assertTrue(newTeams.stream().filter(t->t.getSeason().equals(old)).count() == 0);

        for (Team oldTeam : oldTeams) {
            assertTrue(newTeams.stream().filter(t->t.getName().equals(oldTeam.getName())).count() == 1);
        }

        seasonApi.delete(newSeason.getId());
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
        teamMatchApi.matchesBySeasonList(season.getId());


        for (Team team : teams) {
            assertTrue(matches.parallelStream().filter(tm->tm.hasTeam(team)).count() == season.getRounds());
            assertTrue(matches.stream().filter(tm->tm.getHome().equals(team)).count() >= (season.getRounds()/2) -2);
            assertTrue(matches.stream().filter(tm->tm.getAway().equals(team)).count() >= (season.getRounds()/2) -2);
        }
        int week = 0;
        for (String s : teamMatches.keySet()) {
            LocalDate dt = LocalDate.parse(s);
            LocalDate md = season.getStartDate().plusWeeks(week++).toLocalDate();
            assertEquals(md,dt);
        }

        assertNotNull(matches);
        for (String s : teamMatches.keySet()) {
            assertEquals(teams.size()/2,teamMatches.get(s).size());
        }
    }
}
