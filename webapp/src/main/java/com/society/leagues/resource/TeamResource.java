package com.society.leagues.resource;


import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.views.PlayerResultView;
import com.society.leagues.service.LeagueService;
import com.society.leagues.client.views.TeamSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/team")
@SuppressWarnings("unused")
public class TeamResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team create(@RequestBody Team team) {
        return leagueService.save(team);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @JsonView(PlayerResultView.class)
    public Team modify(@RequestBody Team team) {
        Team existingTeam = leagueService.findOne(team);
        if (existingTeam == null) {
            existingTeam = new Team();
        }
        existingTeam.setSeason(leagueService.findOne(team.getSeason()));
        existingTeam.setName(team.getName());
        TeamMembers existingMembers = existingTeam.getMembers();
        if (team.getMembers() != null &&  team.getMembers().getMembers() != null && !team.getMembers().getMembers().isEmpty()) {
            existingMembers.setMembers(new HashSet<>());
            team.getMembers().getMembers().forEach(existingMembers::addMember);
        }
        leagueService.save(existingMembers);
        existingTeam.setMembers(existingMembers);
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/admin/members/add/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team addMembers(Principal principal, @PathVariable String id, @RequestBody List<User> users) {
        Team existingTeam = leagueService.findOne(new Team(id));

        if (existingTeam == null) {
            return null;
        }
        existingTeam.addMembers(users);
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/admin/members/remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team removeMembers(Principal principal, @PathVariable String id, @RequestBody List<User> users) {
        Team existingTeam = leagueService.findOne(new Team(id));
        if (existingTeam == null) {
            return null;
        }
        existingTeam.removeMembers(users);
        return leagueService.save(existingTeam);
    }

    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Team get(Principal principal, @PathVariable String id) {
        Team existingTeam = leagueService.findOne(new Team(id));
        if (existingTeam == null) {
            return null;
        }
        return existingTeam;
    }

    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/user/{id}/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Team getTeamForUserSeason(Principal principal, @PathVariable String id, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(id));
        Season s = leagueService.findOne(new Season(seasonId));
        return leagueService.findAll(Team.class).parallelStream().filter(t->t.getSeason().equals(s) && t.hasUser(u)).findFirst().orElse(null);
    }

    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/get/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal, @PathVariable String id) {
         User u = leagueService.findOne(new User(id));
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.hasUser(u) || t.inSameSeason(u))
                 .collect(Collectors.toList()
                 );
    }

    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/get/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getUsersTeams(Principal principal) {
         User u = leagueService.findByLogin(principal.getName());
        if (u == null) {
            return Collections.emptyList();
        }
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.hasUser(u))
                 .filter(t->t.getSeason().isActive())
                 .collect(Collectors.toList()
                 );
    }
    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamBySeason(Principal principal, @PathVariable String id) {
         Season s = leagueService.findOne(new Season(id));
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.getSeason().equals(s))
                 .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                 .collect(Collectors.toList()
                 );
    }


    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal) {
         return leagueService.findCurrent(Team.class);
    }

    @JsonView(TeamSummary.class)
    @RequestMapping(value = "/get/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        if (u == null) {
            return  Collections.emptyList();
        }

        return leagueService.findCurrent(Team.class).stream().
                filter(t -> t.getMembers().getMembers().contains(u)).collect(Collectors.toList()
        );
    }


}
