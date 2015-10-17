package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/teammatch")
@SuppressWarnings("unused")
public class TeamMatchResource {

    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch create(@RequestBody TeamMatch teamMatch) {
        return leagueService.save(teamMatch);
    }


    @RequestMapping(value = "/admin/delete/{teamMatchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String,List<TeamMatch>> delete(Principal principal, @PathVariable String teamMatchId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        resultService.removeTeamMatchResult(tm);
        return getTeamMatchSeason(principal,tm.getSeason().getId());
    }

    @RequestMapping(value = "/admin/create/{seasonId}/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String,List<TeamMatch>> createTemplate(Principal principal, @PathVariable String seasonId, @PathVariable String date ) {
        Season s = new Season(seasonId);
        LocalDateTime dt = LocalDate.parse(date).atStartOfDay();
        //TODO what if there are no matches yet ?
        TeamMatch teamMatch =  leagueService.findAll(TeamMatch.class).parallelStream().filter(tm -> tm.getSeason().equals(s)).findAny().orElse(null);
        if (teamMatch == null)
            return null;

        TeamMatch copy = TeamMatch.copy(teamMatch);
        copy.setId(null);
        copy.setHomeRacks(0);
        copy.setAwayRacks(0);
        copy.setSetAwayWins(0);
        copy.setSetHomeWins(0);
        copy.setMatchDate(dt);
        leagueService.save(copy);
        return getTeamMatchSeason(principal, s.getId());
    }

    @RequestMapping(value = "/admin/modify/{teamMatchId}/team/{type}/{teamId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modifyTeam(Principal principal,
                                @PathVariable String teamMatchId,
                                @PathVariable String type,
                                @PathVariable String teamId) {
        TeamMatch teamMatch = leagueService.findOne(new TeamMatch(teamId));
        Team team = leagueService.findOne(new Team(teamId));
        if (type.equals("winner")) {
            if (teamMatch.getWinner().equals(teamMatch.getHome())) {
                teamMatch.setHome(team);
            } else {
                teamMatch.setAway(team);
            }
        } else {
            if (teamMatch.getLoser().equals(teamMatch.getHome())) {
                teamMatch.setHome(team);
            } else {
                teamMatch.setAway(team);
            }
        }
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

    @RequestMapping(value = "/racks/{teamMatchId}/{teamId}/{racks}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch updateRacks(Principal principal, @PathVariable String teamMatchId, @PathVariable String teamId, @PathVariable Integer racks) {
        TeamMatch  teamMatch = leagueService.findOne(new TeamMatch(teamMatchId));
        Team team = leagueService.findOne(new Team(teamId));
        if (teamMatch.getHome().equals(team)) {
            teamMatch.setHomeRacks(racks);
        } else {
            teamMatch.setAwayRacks(racks);
        }

        return leagueService.save(teamMatch);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/wins/{teamMatchId}/{teamId}/{wins}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch updateWins(Principal principal, @PathVariable String teamMatchId, @PathVariable String teamId, @PathVariable Integer wins) {
        TeamMatch  teamMatch = leagueService.findOne(new TeamMatch(teamMatchId));
        Team team = leagueService.findOne(new Team(teamId));
        if (teamMatch.getHome().equals(team)) {
            teamMatch.setSetHomeWins(wins);
        } else {
            teamMatch.setSetAwayWins(wins);
        }
        return leagueService.save(teamMatch);
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

    @RequestMapping(value = {"/season/{id}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<TeamMatch>> getTeamMatchSeason(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
         List<TeamMatch> results;
        if (s.isActive()) {
            results = leagueService.findCurrent(TeamMatch.class).stream().parallel()
                    .filter(tm -> tm.getSeason().equals(s)).sorted(new Comparator<TeamMatch>() {
                @Override
                public int compare(TeamMatch teamMatch, TeamMatch t1) {
                    return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                }
            }).collect(Collectors.toList());
        } else {
            results = leagueService.findAll(TeamMatch.class).stream().parallel().filter(tm -> tm.getSeason().equals(s)).sorted(new Comparator<TeamMatch>() {
                @Override
                public int compare(TeamMatch teamMatch, TeamMatch t1) {
                    return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                }
            }).collect(Collectors.toList());
        }
        Map<String,List<TeamMatch>> group = results.stream().collect(Collectors.groupingBy(tm -> tm.getMatchDate().toLocalDate().toString()));
        return (Map<String,List<TeamMatch>>) new TreeMap<>(group);
    }

    @RequestMapping(value = "/user/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchUser(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        List<Team> userTeams = leagueService.findAll(Team.class).stream().
                filter(tm -> tm.getMembers().contains(u)).filter(t -> t.getSeason().isActive())
                .collect(Collectors.toList());
        List<TeamMatch> teamMatches = new ArrayList<>();
        for (Team userTeam : userTeams) {
            teamMatches.addAll(leagueService.findAll(TeamMatch.class).stream().filter(tm->tm.hasTeam(userTeam)).collect(Collectors.toList()));
        }
        List<TeamMatch> copy = new ArrayList<>(teamMatches.size());
        teamMatches.stream().forEach(tm->copy.add(LeagueObject.copy(tm)));
        copy.parallelStream().forEach(c->c.setReferenceUser(u));
        return copy;
    }
}
