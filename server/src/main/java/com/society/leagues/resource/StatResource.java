package com.society.leagues.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.service.StatService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/stat")
@SuppressWarnings("unused")
public class StatResource {
    @Autowired LeagueService leagueService;
    @Autowired StatService statService;
    @Autowired ResultService resultService;

    final static Logger logger = Logger.getLogger(StatResource.class);


    @RequestMapping(value = "/team/{id}/members",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getTeamMemberStats(@PathVariable String id) {
        Team team = leagueService.findOne(new Team(id));
        if (team == null) {
            return Collections.emptyList();
        }
        List<Stat> stats = new ArrayList<>();
        for (User user : team.getMembers().getMembers()) {
            stats.addAll(getUserSeasonStats(user.getId(),team.getSeason().getId()));
        }
        return stats;
    }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = "/season/users/{id}/summary",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
  public List<Stat> getSeasonUserStats(@PathVariable String id) {
        final Season season = leagueService.findOne(new Season(id));
        if (season == null)
            return Collections.emptyList();
        return statService.getSeasonStats(season);
        //statService.refresh();
  }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = "/user/{id}/summary",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getActiveUserStatsSummary(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        final List<Stat> userStats = new ArrayList<>();
        for (Season season : u.getSeasons()) {
            userStats.addAll(statService.getUserStats(u,season));
        }
        userStats.add(Stat.buildLifeTimeStats(u, userStats));
        return userStats;
      }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = "/user/{id}/{seasonId}/summary",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getUserSeasonStats(@PathVariable String id, @PathVariable String seasonId) {
        final Season s = leagueService.findOne(new Season(seasonId));
        final User u = leagueService.findOne(new User(id));
        return statService.getUserStats(u,s);
     }

    //@JsonView()
    @RequestMapping(value = "/team/{seasonId}/summary",
                method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Team> getTeamStatsSeason(@PathVariable String seasonId) {
        Season s = leagueService.findOne(new Season(seasonId));
        List<Team> teams = leagueService.findAll(Team.class).parallelStream().filter(t->t.getSeason().equals(s)).collect(Collectors.toList());
        resultService.refresh();
        for (Team team : teams) {
            statService.refreshTeamStats(team);
        }
        return teams;
    }

}
