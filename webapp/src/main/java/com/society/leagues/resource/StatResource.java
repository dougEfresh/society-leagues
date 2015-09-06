package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/stat")
public class StatResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/team/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Stat getTeamStat(@PathVariable String id) {
        Team team = leagueService.findOne(new Team(id));
        if (team == null) {
            return new Stat();
        }
        List<TeamMatch> matches = leagueService.findTeamMatchByTeam(team).stream().filter(tm->tm.hasResults()).collect(Collectors.toList());
        return Stat.buildTeamStats(team,matches);
    }

    @RequestMapping(value = "/team/{id}/members",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getTeamMemberStats(@PathVariable String id) {
        Team team = leagueService.findOne(new Team(id));
        if (team == null) {
            return Collections.emptyList();
        }
        return Stat.buildTeamMemberStats(team, leagueService.findPlayerResultBySeason(team.getSeason()));
    }

    @RequestMapping(value = "/season/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonStats(@PathVariable String id) {
     Season season = leagueService.findOne(new Season(id));
     List<Team> teams = leagueService.findAll(Team.class).stream().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
     List<Stat> stats = new ArrayList<>(100);
     for (Team team : teams) {
         List<TeamMatch> matches = leagueService.findTeamMatchByTeam(team).stream().filter(tm->tm.hasResults()).collect(Collectors.toList());
         stats.add(Stat.buildTeamStats(team,matches));
     }
     return stats;
    }

}
