package com.society.leagues.resource;

import com.society.leagues.service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/teammatch")
@SuppressWarnings("unused")
public class TeamMatchResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch create(@RequestBody TeamMatch teamMatch) {
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modify(@RequestBody TeamMatch teamMatch) {
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean delete(@RequestBody TeamMatch teamMatch) {
        return leagueService.delete(teamMatch);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new TeamMatch(id));

    }

    @RequestMapping(value = "/members/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Map<String,Set<User>> getTeamMatchMembers(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        Map<String,Set<User>> members = new HashMap<>();
        Team w = tm.getHomeRacks() > tm.getAwayRacks() ? tm.getHome() : tm.getAway();
        Team l = tm.getHomeRacks() > tm.getAwayRacks() ? tm.getAway() : tm.getHome();
        members.put("winners",w.getMembers());
        members.put("losers",l.getMembers());
        return members;
    }


    @RequestMapping(value = {"/get/season/{id}","/season/{id}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchSeason(Principal principal, @PathVariable String id) {
         Season s = leagueService.findOne(new Season(id));
        if (s.isActive()) {
            return leagueService.findCurrent(TeamMatch.class).stream().parallel()
                    .filter(tm -> tm.getSeason().equals(s)).sorted(new Comparator<TeamMatch>() {
                @Override
                public int compare(TeamMatch teamMatch, TeamMatch t1) {
                    return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                }
            }).collect(Collectors.toList());
        } else {
            return leagueService.findAll(TeamMatch.class).stream().parallel().filter(tm -> tm.getSeason().equals(s)).sorted(new Comparator<TeamMatch>() {
                @Override
                public int compare(TeamMatch teamMatch, TeamMatch t1) {
                    return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                }
            }).collect(Collectors.toList());
        }

    }

    @RequestMapping(value = "/get/user/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchUser(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        List<Team> userTeams = leagueService.findAll(Team.class).stream().
                filter(tm -> tm.getMembers().contains(u)).filter(t -> t.getSeason().isActive())
                .collect(Collectors.toList());
        List<TeamMatch> teamMatches = new ArrayList<>();
        for (Team userTeam : userTeams) {
            teamMatches.addAll(leagueService.findAll(TeamMatch.class).stream().filter(tm->tm.hasTeam(userTeam)).collect(Collectors.toList()));
        }
        teamMatches.parallelStream().forEach(tm->tm.setReferenceUser(u));
        return teamMatches;
    }
}
